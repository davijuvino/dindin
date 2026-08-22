package br.com.dindin.infrastructure.development

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource
import org.springframework.context.annotation.Profile

@Profile("dev")
@Configuration
@ImportResource("classpath:h2server.xml")
class DevelopmentConfiguration