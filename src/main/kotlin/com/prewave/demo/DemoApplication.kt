package com.prewave.demo

import com.prewave.demo.core.Edge
import com.prewave.demo.core.EdgeRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class DemoApplication(private val edgeRepository: EdgeRepository) {

    @Bean
    fun commandLineRunner(): CommandLineRunner {
        return CommandLineRunner { args ->
            val root = Edge(-1, 1)
            if (!edgeRepository.edgeExists(root)) {
                edgeRepository.addEdge(root)
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
