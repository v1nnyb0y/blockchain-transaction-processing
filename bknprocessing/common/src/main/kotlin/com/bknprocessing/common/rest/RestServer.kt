package com.bknprocessing.common.rest

import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.ServerConfiguration
import kotlinx.coroutines.runBlocking
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

class RestServer private constructor() : IServer {

    private val webClient = WebClient.create("http://localhost/8085")

    override fun setup(configuration: ServerConfiguration) {}

    override fun sendObj(element: Any, topic: String): Boolean {
        runBlocking {
            val nodeIdx = 0 // TODO
//        val htmlResponse = webClient.get().retrieve().bodyToMono<String>()
            val response = webClient.get()
                .uri("/blockchain-service/app/$nodeIdx/")
                .accept(APPLICATION_JSON)
                .retrieve()
                .awaitBody<Int>()
        }
        return false
    }

    companion object {
        val INSTANCE = RestServer()
    }
}
