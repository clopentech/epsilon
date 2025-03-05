package io.clopen.epsilon.keys

import java.util.Optional

interface KeyManagementService {

    fun getKey(alias: String): Optional<EpsilonKey>

    fun getCurrentKey(): Optional<EpsilonKey>

    fun generateKeyIfNeeded()
}