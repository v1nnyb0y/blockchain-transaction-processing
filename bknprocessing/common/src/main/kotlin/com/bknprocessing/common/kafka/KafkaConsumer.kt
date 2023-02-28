package com.bknprocessing.common.kafka

import com.bknprocessing.common.IClient
import com.bknprocessing.common.globals.ClientConfiguration
import com.bknprocessing.common.globals.KafkaClientConfiguration
import com.bknprocessing.common.globals.TopicsList
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer

open class KafkaConsumer : IClient {

    private lateinit var objQueueConsumer: KafkaConsumer<String, Any>
    private lateinit var verificationBlockConsumer: KafkaConsumer<String, Any>
    private lateinit var verificationResultConsumer: KafkaConsumer<String, Any>
    private lateinit var stateChangeConsumer: KafkaConsumer<String, Any>

    override fun setup(configuration: ClientConfiguration) {
        val castedConfiguration = configuration as? KafkaClientConfiguration ?: throw IllegalStateException("Wrong client configuration")

        val options: MutableMap<String, Any> = mutableMapOf()

        options["bootstrap.servers"] = castedConfiguration.server
        options["auto.offset.reset"] = "earliest"

        options["group.id"] = "objQueue"
        objQueueConsumer = KafkaConsumer(options, StringDeserializer(), JsonDeserializer())
        objQueueConsumer.subscribe(listOf(TopicsList.ObjQueue.name))

        options["group.id"] = "verificationResult"
        verificationResultConsumer = KafkaConsumer(options, StringDeserializer(), JsonDeserializer())
        verificationResultConsumer.subscribe(listOf(TopicsList.VerificationResultBlockQueue.name))

        options["group.id"] = "verificationBlock-${castedConfiguration.nodeIndex}"
        verificationBlockConsumer = KafkaConsumer(options, StringDeserializer(), JsonDeserializer())
        verificationBlockConsumer.subscribe(listOf(TopicsList.VerificationBlockQueue.name))

        options["group.id"] = "stateChange-${castedConfiguration.nodeIndex}"
        stateChangeConsumer = KafkaConsumer(options, StringDeserializer(), JsonDeserializer())
        stateChangeConsumer.subscribe(listOf(TopicsList.StateChange.name))
    }

    override fun getObj(from: String): Any? {
        return getObj(from, -1)
    }

    override fun getObj(from: String, who: Int): Any? {
        return when (from) {
            TopicsList.ObjQueue.name -> if (objs.isNotEmpty()) objs.poll() else null
            TopicsList.VerificationBlockQueue.name -> if (verificationBlock.isNotEmpty()) verificationBlock.poll() else null
            TopicsList.VerificationResultBlockQueue.name -> if (verificationResult.isNotEmpty()) verificationResult.poll() else null
            TopicsList.StateChange.name -> if (stateChange.isNotEmpty()) stateChange.poll() else null
            else -> null
        }
    }
}
