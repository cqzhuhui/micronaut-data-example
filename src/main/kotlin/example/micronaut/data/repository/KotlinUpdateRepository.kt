package example.micronaut.data.repository

import io.micronaut.core.annotation.Blocking
import io.micronaut.data.repository.GenericRepository
import io.micronaut.validation.Validated

@Blocking
@Validated
interface KotlinUpdateRepository<E, ID> : GenericRepository<E, ID> {

    fun <S : E> save(entity: S): S

    fun <S : E> saveAll(entities: Iterable<S>): List<S>

    fun <S : E> update(entity: S): S

    fun <S : E> updateAll(entities: Iterable<S>): List<S>
}
