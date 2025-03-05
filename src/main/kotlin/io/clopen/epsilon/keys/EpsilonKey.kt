package io.clopen.epsilon.keys

import javax.crypto.spec.SecretKeySpec

data class EpsilonKey(val alias: String, val key: SecretKeySpec)
