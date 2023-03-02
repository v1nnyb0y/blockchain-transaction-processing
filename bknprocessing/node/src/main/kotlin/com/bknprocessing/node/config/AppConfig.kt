package com.bknprocessing.node.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Suppress("unused")
@Configuration
class AppConfig {

    @Bean
    fun objectMapper() = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//            .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
//            .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())!!
}
