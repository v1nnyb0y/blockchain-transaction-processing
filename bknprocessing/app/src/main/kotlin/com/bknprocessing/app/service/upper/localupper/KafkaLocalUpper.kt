package com.bknprocessing.app.service.upper.localupper

import com.bknprocessing.common.IClient
import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.ClientConfiguration
import com.bknprocessing.common.globals.KafkaClientConfiguration
import com.bknprocessing.common.globals.KafkaServerConfiguration
import com.bknprocessing.common.globals.ServerConfiguration
import com.bknprocessing.common.kafka.KafkaConsumer
import com.bknprocessing.common.kafka.KafkaProducer

open class KafkaLocalUpper<T>(
    client: () -> IClient = { KafkaConsumer() },
    server: () -> IServer = { KafkaProducer.INSTANCE },

    getClientConfiguration: (Any) -> ClientConfiguration = {
        KafkaClientConfiguration()
    },
    getServerConfiguration: (Any) -> ServerConfiguration =
        { _: Any ->
            KafkaServerConfiguration(
                server = "localhost:29092",
                keySerializer = "org.apache.kafka.common.serialization.StringSerializer",
                valueSerializer = "org.springframework.kafka.support.serializer.JsonSerializer",
            )
        },
) : LocalUpper<T>(client, getClientConfiguration, server, getServerConfiguration)
