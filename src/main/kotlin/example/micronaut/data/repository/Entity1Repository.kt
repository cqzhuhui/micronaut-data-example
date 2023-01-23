package example.micronaut.data.repository

import example.micronaut.data.entity.Entity1
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.Join
import io.micronaut.data.annotation.Version
import io.micronaut.data.jdbc.annotation.JdbcRepository
import java.util.UUID
import javax.transaction.Transactional

@JdbcRepository
@Transactional(Transactional.TxType.MANDATORY)
@Join(value = "entity2", type = Join.Type.LEFT_FETCH)
interface Entity1Repository : KotlinUpdateRepository<Entity1, UUID> {
    fun findById(id: UUID): Entity1

    fun update(@Id id: UUID, @Version version: Long, name: String)
}
