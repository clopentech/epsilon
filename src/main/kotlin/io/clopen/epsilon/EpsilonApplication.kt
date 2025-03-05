package io.clopen.epsilon

import io.clopen.epsilon.keys.KeyManagementConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(KeyManagementConfig::class)
class EpsilonApplication

fun main(args: Array<String>) {
	runApplication<EpsilonApplication>(*args)
}
