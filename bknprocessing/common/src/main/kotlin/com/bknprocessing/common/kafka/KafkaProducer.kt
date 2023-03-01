package com.bknprocessing.common.kafka

import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.KafkaServerConfiguration
import com.bknprocessing.common.globals.ServerConfiguration
import org.apache.kafka.clients.producer.ProducerRecord

class KafkaProducer private constructor() : IServer {

    private val options: MutableMap<String, String> = mutableMapOf()

    override fun setup(configuration: ServerConfiguration) {
        val castedConfiguration = configuration as? KafkaServerConfiguration ?: throw IllegalStateException("Wrong server configuration")
        options["bootstrap.servers"] = castedConfiguration.server
        options["key.serializer"] = castedConfiguration.keySerializer
        options["value.serializer"] = castedConfiguration.valueSerializer
    }

    override fun sendObj(element: Any, topic: String): Boolean {
        val producerRecord: ProducerRecord<String, Any> = ProducerRecord(topic, element)
        val producer = org.apache.kafka.clients.producer.KafkaProducer<String, Any>(options as Map<String, Any>?)

        val future = producer.send(producerRecord).get()
        return future != null
    }

    companion object {
        val INSTANCE = KafkaProducer()
    }
}
