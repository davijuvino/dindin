package br.com.dindin

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HellWord {
    @RequestMapping("/")
    fun helloWorld(): String {
        return "Hello World"
    }
}