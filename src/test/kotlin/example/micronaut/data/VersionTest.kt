package example.micronaut.data

import example.micronaut.data.entity.Entity1
import example.micronaut.data.entity.Entity2
import example.micronaut.data.repository.Entity1Repository
import example.micronaut.data.repository.Entity2Repository
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.transaction.SynchronousTransactionManager
import io.micronaut.transaction.TransactionDefinition
import jakarta.inject.Inject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Connection
import java.util.UUID

@Testcontainers
@MicronautTest(transactional = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VersionTest {

    @Inject
    lateinit var entity1Repository: Entity1Repository

    @Inject
    lateinit var entity2Repository: Entity2Repository

    @Inject
    private lateinit var transactionManager: SynchronousTransactionManager<Connection>

    @BeforeEach
    fun createTables() {
        transactionManager.execute(TransactionDefinition.DEFAULT) { transaction ->
            transaction.connection.prepareStatement(createEntity1Sql).use { it.execute() }
            transaction.connection.prepareStatement(createEntity2Sql).use { it.execute() }

            val tables = listOf("entity1", "entity2")
            val truncateStatements = tables.joinToString("\n") { "TRUNCATE TABLE $it CASCADE;" }
            transaction.connection.prepareStatement(truncateStatements).use { it.executeUpdate() }
        }
    }

    private val createEntity1Sql = """
            CREATE TABLE IF NOT EXISTS entity1
            (
                id                  UUID        NOT NULL,
                name                varchar     NULL,
                entity2_id          UUID        NOT NULL,
                created_at          timestamptz NOT NULL,
                updated_at          timestamptz NOT NULL,
                version             BIGINT      NOT NULL,

                CONSTRAINT entity1_pkey PRIMARY KEY (id)
            );
    """.trimIndent()

    private val createEntity2Sql = """
            CREATE TABLE IF NOT EXISTS entity2
            (
                id                  UUID        NOT NULL,
                name                varchar     NULL,
                created_at          timestamptz NOT NULL,
                updated_at          timestamptz NOT NULL,
                version             BIGINT      NOT NULL,

                CONSTRAINT entity2_pkey PRIMARY KEY (id)
            );
    """.trimIndent()

    @Test
    fun `entity2 should check version, when update entity1`() {
        val a = transactionManager.execute(TransactionDefinition.DEFAULT) {
            var v = entity2Repository.save(
                Entity2(
                    id = UUID.randomUUID()
                )
            )
            entity1Repository.save(
                Entity1(
                    id = UUID.randomUUID(),
                    entity2 = v!!
                )
            )
        }
        transactionManager.execute(TransactionDefinition.DEFAULT) {
            var b = entity1Repository.findById(a.id)
            // entity2Repository.update(v!!.copy())

            println("update1 entity1.version is ${b!!.version}, entity2.version is ${b!!.entity2.version}")
            entity1Repository.update(b!!.id, b!!.version!!, "hello")
            b = entity1Repository.findById(a.id)
            println("update1 entity1.version is ${b!!.version}, entity2.version is ${b!!.entity2.version}")

            val bbbb = entity1Repository.findById(a.id)
            println("final entity1.version is ${bbbb!!.version}, entity2.version is ${bbbb!!.entity2!!.version}, name is ${bbbb!!.name}")
        }

        transactionManager.execute(TransactionDefinition.DEFAULT) {
            val bbbb = entity1Repository.findById(a.id)
            println("final entity1.version is ${bbbb!!.version}, entity2.version is ${bbbb!!.entity2!!.version}, name is ${bbbb!!.name}")
        }
    }
}
