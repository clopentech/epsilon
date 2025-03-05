package io.clopen.epsilon.keys.db

import com.arangodb.springframework.repository.ArangoRepository
import java.util.Optional

interface KeyRepository : ArangoRepository<KeyInformation, String> {
    fun findByAlias(alias: String): Optional<KeyInformation>
}