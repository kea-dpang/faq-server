package kea.dpang.faq

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class FaqServerApplication

fun main(args: Array<String>) {
    runApplication<FaqServerApplication>(*args)
}
