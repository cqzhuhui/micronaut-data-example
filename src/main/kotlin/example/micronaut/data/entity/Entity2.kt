package example.micronaut.data.entity

import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Version
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@MappedEntity(value = "entity2")
data class Entity2(
    @field:Id val id: UUID,

    val name: String? = null,

    // Metadata
    @DateCreated(truncatedTo = ChronoUnit.MILLIS) val createdAt: Instant? = null,
    @DateUpdated(truncatedTo = ChronoUnit.MILLIS) val updatedAt: Instant? = null,
    @field:Version val version: Long? = 0L
)
