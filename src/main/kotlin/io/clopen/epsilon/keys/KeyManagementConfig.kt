package io.clopen.epsilon.keys

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "epsilon.key-management")
data class KeyManagementConfig(
    var algorithm: String = "HmacSHA256",
    var keyExpiryInDays: Long = 90,
    var keyRotationThresholdInDays: Long = 7
)