package com.bknprocessing.common.rest

import com.bknprocessing.common.IClient
import com.bknprocessing.common.globals.ClientConfiguration
import com.bknprocessing.common.globals.TopicsList
import java.util.LinkedList
import java.util.Queue

class RestClient private constructor() : IClient {

    private val objQueue: Queue<Any> = LinkedList()
    private val verificationBlockQueue: Queue<Any> = LinkedList()
    private val verificationResultQueue: Queue<Any> = LinkedList()
    private val stateChangeQueue: Queue<Any> = LinkedList()

    override fun setup(configuration: ClientConfiguration) {}

    override fun getObj(from: String): Any? {
        return getObj(from, -1)
    }

    override fun getObj(from: String, who: Int): Any? {
        return when (from) {
            TopicsList.ObjQueue.name -> if (objQueue.isEmpty()) null else objQueue.poll()
            TopicsList.VerificationBlockQueue.name -> if (verificationBlockQueue.isEmpty()) null else verificationBlockQueue.poll()
            TopicsList.VerificationResultBlockQueue.name -> if (verificationResultQueue.isEmpty()) null else verificationResultQueue.poll()
            TopicsList.StateChange.name -> if (stateChangeQueue.isEmpty()) null else stateChangeQueue.poll()
            else -> null
        }
    }

    fun appendObjQueue(obj: Any) {
        objQueue.add(obj)
    }

    fun appendVerificationBlockQueue(obj: Any) {
        verificationBlockQueue.add(obj)
    }

    fun appendVerificationResultQueue(obj: Any) {
        verificationResultQueue.add(obj)
    }

    fun appendStateChangeQueue(obj: Any) {
        stateChangeQueue.add(obj)
    }

    companion object {
        val INSTANCE = RestClient()
    }
}
