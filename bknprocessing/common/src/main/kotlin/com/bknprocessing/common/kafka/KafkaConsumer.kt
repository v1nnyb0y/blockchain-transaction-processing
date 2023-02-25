package com.bknprocessing.common.kafka

import com.bknprocessing.common.IClient
import com.bknprocessing.common.globals.ClientConfiguration
import com.bknprocessing.common.globals.TopicsList
import org.springframework.kafka.annotation.KafkaListener
import java.util.LinkedList
import java.util.Queue

class KafkaConsumer : IClient {

    private val objs: Queue<Any> = LinkedList()
    private val verificationBlock: Queue<Any> = LinkedList()
    private val verificationResult: Queue<Any> = LinkedList()
    private val stateChange: Queue<Any> = LinkedList()

    override fun setup(configuration: ClientConfiguration) { }

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

    @KafkaListener(topics = ["ObjQueue"], groupId = "obj_queue")
    private fun objsListener(value: Any) {
        objs.add(value)
    }

    @KafkaListener(topics = ["VerificationBlockQueue"], groupId = "verification_block")
    private fun verificationBlockListener(value: Any) {
        verificationBlock.add(value)
    }

    @KafkaListener(topics = ["VerificationResultBlockQueue"], groupId = "verification_result")
    private fun verificationResultListener(value: Any) {
        verificationResult.add(value)
    }

    @KafkaListener(topics = ["StateChange"], groupId = "state_change")
    private fun stateChangeListener(value: Any) {
        stateChange.add(value)
    }
}
