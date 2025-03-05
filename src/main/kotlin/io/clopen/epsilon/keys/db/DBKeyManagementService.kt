package io.clopen.epsilon.keys.db

import io.clopen.epsilon.keys.KeyManagementConfig
import io.clopen.epsilon.keys.KeyManagementService
import jakarta.annotation.PostConstruct
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.Optional
import javax.crypto.spec.SecretKeySpec

@Service
@Slf4j
class DBKeyManagementService @Autowired constructor(
    private val keyStore: KeyStore,
    private val keyManagementConfig: KeyManagementConfig
) : KeyManagementService {
    private val log = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    fun init() {
        generateKeyIfNeeded()
    }

    override fun getActiveKey(alias: String): Optional<SecretKeySpec> {
        val keyInfoOptional = keyStore.getKey(alias)
        return keyInfoOptional
            .filter { keyInfo ->
                if (isExpired(keyInfo)) {
                    log.warn("An attempt to retrieve an expired key was made $alias")
                    false
                } else true
            }
            .map { getKey(it) }
    }


    @Scheduled(cron = "0 0 0 * * ?")
    override fun generateKeyIfNeeded() {
        val latestKeyInfo = getLatestKeyInformation().orElse(null)
        if (latestKeyInfo == null ||
            isExpired(latestKeyInfo) ||
            Duration.between(latestKeyInfo.expiryDate, Instant.now())
                .toDays() < keyManagementConfig.keyRotationThresholdInDays
        ) {
            generateKey()
        }
    }

    private fun getLatestKeyInformation(): Optional<KeyInformation> {
        val keys = keyStore.getAllKeys()
        return keys.minByOrNull { it.createdAt }?.let { Optional.of(it) } ?: Optional.empty()
    }

    private fun getKey(keyInfo: KeyInformation): SecretKeySpec {
        val decodedKey = Base64.getDecoder().decode(keyInfo.encryptionKey)
        return SecretKeySpec(decodedKey, keyInfo.algorithm)
    }

    private fun generateKey() {
        val alias = Instant.now().epochSecond.toString()
        keyStore.generateKey(alias)
    }

    private fun isExpired(keyInfo: KeyInformation): Boolean {
        return Instant.now().isAfter(keyInfo.expiryDate)
    }
}