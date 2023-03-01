package com.bknprocessing.app.service.upper.remoteupper

import com.bknprocessing.app.service.upper.IUpper
import com.bknprocessing.app.utils.constructedNode
import com.bknprocessing.app.utils.logger
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import java.time.Instant

// TODO Vitalii for implementation (docker instances)
abstract class RemoteUpper<T>(
    private val getNodeConfiguration: (networkSize: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long) -> Any,
) : IUpper<T> {

    val log: Logger by logger()
    val nodes: MutableList<Boolean> = mutableListOf()

    private fun constructNodeCollection(count: Int, isHealthy: Boolean, createdAt: Long, networkSize: Int) = runBlocking {
        for (idx in 0 until count) {
            val webClient = WebClient.create("http://localhost:${8080 + nodes.size}")
            val response = webClient.post()
                .uri("/init")
                .bodyValue(
                    getNodeConfiguration(networkSize, isHealthy, nodes.size, createdAt),
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBodilessEntity()
            nodes.add(true)
            log.constructedNode(isHealthy, nodes.size - 1)
        }
    }

    override suspend fun startNodes(nodesCount: Int, unhealthyNodesCount: Int) {
        val createdAt = Instant.now().toEpochMilli()
        constructNodeCollection(nodesCount - unhealthyNodesCount, true, createdAt, nodesCount)
        constructNodeCollection(unhealthyNodesCount, false, createdAt, nodesCount)
    }
}
