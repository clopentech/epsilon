package io.clopen.epsilon.keys

import java.util.Optional
import javax.crypto.spec.SecretKeySpec

interface KeyManagementService {

    fun getActiveKey(alias: String): Optional<SecretKeySpec>

    fun generateKeyIfNeeded()
}