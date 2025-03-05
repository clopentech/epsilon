package io.clopen.epsilon.keys.db

import com.arangodb.springframework.annotation.Document
import org.springframework.data.annotation.Id
import java.time.Instant

@Document("keys")
data class KeyInformation(
    @Id
    val alias: String,
    val algorithm: String,
    val encryptionKey: String,
    val expiryDate: Instant,
    val createdAt: Instant = Instant.now()
)