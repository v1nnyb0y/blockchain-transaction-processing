package com.bknprocessing.common.kafka

import com.bknprocessing.common.IClient
import com.bknprocessing.common.globals.ClientConfiguration
import com.bknprocessing.common.globals.KafkaClientConfiguration
import com.bknprocessing.common.globals.TopicsList
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import java.time.Duration

open class KafkaConsumer : IClient {

    private lateinit var objQueueConsumer: KafkaConsumer<String, Any>
    private lateinit var verificationBlockConsumer: KafkaConsumer<String, Any>
    private lateinit var verificationResultConsumer: KafkaConsumer<String, Any>
    private lateinit var stateChangeConsumer: KafkaConsumer<String, Any>

    override fun setup(configuration: ClientConfiguration) {
        val castedConfiguration = configuration as? KafkaClientConfiguration ?: throw IllegalStateException("Wrong client configuration")

        val options: MutableMap<String, Any> = mutableMapOf()
        val keyDeserializer = StringDeserializer()
        val valueDeserializer = JsonDeserializer<Any>().apply {
            addTrustedPackages("*")
        }

        options["bootstrap.servers"] = castedConfiguration.server
        options["auto.offset.reset"] = "earliest"
        options["enable.auto.commit"] = "true"
        options["max.poll.records"] = 1

        options["group.id"] = "objQueue"
        objQueueConsumer = KafkaConsumer(options, keyDeserializer, valueDeserializer)
        objQueueConsumer.subscribe(listOf(TopicsList.ObjQueue.name))

        options["group.id"] = "verificationResult"
        verificationResultConsumer = KafkaConsumer(options, keyDeserializer, valueDeserializer)
        verificationResultConsumer.subscribe(listOf(TopicsList.VerificationResultBlockQueue.name))

        options["group.id"] = "verificationBlock-${castedConfiguration.nodeIndex}"
        verificationBlockConsumer = KafkaConsumer(options, keyDeserializer, valueDeserializer)
        verificationBlockConsumer.subscribe(listOf(TopicsList.VerificationBlockQueue.name))

        options["group.id"] = "stateChange-${castedConfiguration.nodeIndex}"
        stateChangeConsumer = KafkaConsumer(options, keyDeserializer, valueDeserializer)
        stateChangeConsumer.subscribe(listOf(TopicsList.StateChange.name))
    }

    override fun getObj(from: String): Any? {
        return getObj(from, -1)
    }

    override fun getObj(from: String, who: Int): Any? {
        return when (from) {
            TopicsList.ObjQueue.name -> {
                val consumed = objQueueConsumer.poll(Duration.ofMillis(1))
                return if (consumed.isEmpty) null else consumed.records(TopicsList.ObjQueue.name).first().value()
            }
            TopicsList.VerificationBlockQueue.name -> {
                val consumed = verificationBlockConsumer.poll(Duration.ofMillis(1))
                return if (consumed.isEmpty) null else consumed.records(TopicsList.VerificationBlockQueue.name).first().value()
            }
            TopicsList.VerificationResultBlockQueue.name -> {
                val consumed = verificationResultConsumer.poll(Duration.ofMillis(1))
                return if (consumed.isEmpty) null else consumed.records(TopicsList.VerificationResultBlockQueue.name).first().value()
            }
            TopicsList.StateChange.name -> {
                val consumed = stateChangeConsumer.poll(Duration.ofMillis(1))
                return if (consumed.isEmpty) null else consumed.records(TopicsList.StateChange.name).first().value()
            }
            else -> null
        }
    }
}
