package com.bknprocessing.app.service.upper.remoteupper

import com.bknprocessing.app.service.upper.IUpper
import com.bknprocessing.app.utils.logger
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.slf4j.Logger
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import java.time.Instant

data class NodeConfiguration(
    val totalNodesCount: Int,
    val isHealthy: Boolean,
    val nodeIndex: Int,
    val createdAt: Long,
)

// TODO Vitalii for implementation (docker instances)
abstract class RemoteUpper<T>(
    private val getNodeConfiguration: (networkSize: Int, isHealthy: Boolean, nodeIndex: Int, createdAt: Long) -> Any,
) : IUpper<T> {

    val log: Logger by logger()
    val nodes: MutableList<Any> = mutableListOf()

    private fun constructNodeCollection(count: Int, isHealthy: Boolean, createdAt: Long, networkSize: Int) = runBlocking {
        for (idx in 0 until count) {
            nodes.add(getNodeConfiguration(networkSize, isHealthy, nodes.size, createdAt))
        }
    }

    override suspend fun startNodes(nodesCount: Int, unhealthyNodesCount: Int) {
        val createdAt = Instant.now().toEpochMilli()
        constructNodeCollection(nodesCount - unhealthyNodesCount, true, createdAt, nodesCount)
        constructNodeCollection(unhealthyNodesCount, false, createdAt, nodesCount)

        supervisorScope {
            nodes.forEachIndexed { idx, conf ->
                // log.constructedNode(, nodes.size - 1)
                launch {
                    val webClient = WebClient.create("http://localhost:${8080 + idx}")
                    webClient.post()
                        .uri("/init")
                        .bodyValue(conf)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .awaitBodyOrNull<String>()
                }
            }
        }
    }
}
