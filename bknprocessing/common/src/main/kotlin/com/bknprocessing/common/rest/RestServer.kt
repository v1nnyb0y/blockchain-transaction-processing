package com.bknprocessing.common.rest

import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.RestJsonServerConfiguration
import com.bknprocessing.common.globals.ServerConfiguration
import com.bknprocessing.common.globals.TopicsList
import kotlinx.coroutines.runBlocking
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity

class RestServer private constructor() : IServer {

    private var networkSize: Int = 0

    override fun setup(configuration: ServerConfiguration) {
        val castedConfiguration = configuration as? RestJsonServerConfiguration ?: throw IllegalStateException("Wrong server configuration")
        networkSize = castedConfiguration.capacity
    }

    override fun sendObj(element: Any, topic: String): Boolean {
        val endPointConnectionString: String
        val targetList: List<Int>
        when (topic) {
            TopicsList.ObjQueue.name -> {
                targetList = findMiner()
                endPointConnectionString = "verifyObj"
            }
            TopicsList.VerificationBlockQueue.name -> {
                targetList = findVerifiers()
                endPointConnectionString = "verify"
            }
            TopicsList.VerificationResultBlockQueue.name -> {
                targetList = findMiner()
                endPointConnectionString = "verifyResult"
            }
            TopicsList.StateChange.name -> {
                targetList = (0 until networkSize).toList()
                endPointConnectionString = "smartContract"
            }
            else -> {
                targetList = listOf()
                endPointConnectionString = "healthCheck"
            }
        }

        targetList.forEach {
            sendRequest(endPointConnectionString, it, element)
        }
        return true
    }

    private fun sendRequest(endPointConnectionString: String, nodeIndex: Int, data: Any) = runBlocking {
        val webClient = WebClient.create("http://localhost:${8080 + nodeIndex}")
        val response = webClient.post()
            .uri("/$endPointConnectionString")
            .bodyValue(data)
            .accept(APPLICATION_JSON)
            .retrieve()
            .awaitBodilessEntity()
    }

    private fun findMiner(): List<Int> {
        return listOf(0)
    }

    private fun findVerifiers(): List<Int> {
        return (1 until networkSize).toList()
    }

    companion object {
        val INSTANCE = RestServer()
    }
}
