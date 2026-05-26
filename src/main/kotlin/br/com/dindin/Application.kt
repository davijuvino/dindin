package br.com.dindin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DindinApplication

fun main(args: Array<String>) {
	runApplication<DindinApplication>(*args)
}
