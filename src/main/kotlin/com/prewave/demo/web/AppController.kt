package com.prewave.demo.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class AppController {

    @GetMapping
    fun index(): String = "Hello Prewave :)"
}