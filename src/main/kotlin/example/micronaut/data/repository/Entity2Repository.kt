package example.micronaut.data.repository

import example.micronaut.data.entity.Entity2
import io.micronaut.data.annotation.Join
import io.micronaut.data.jdbc.annotation.JdbcRepository
import java.util.UUID
import javax.transaction.Transactional

@JdbcRepository
@Transactional(Transactional.TxType.MANDATORY)
@Join(value = "entity2", type = Join.Type.LEFT_FETCH)
interface Entity2Repository : KotlinUpdateRepository<Entity2, UUID>
