package com.prewave.demo.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class EdgeControllerTest {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should create edge successfully`() {
        // TODO: more tests here ...
    }
}
