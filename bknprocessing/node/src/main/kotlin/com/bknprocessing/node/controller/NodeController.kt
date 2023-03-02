package com.bknprocessing.node.controller

import com.bknprocessing.node.service.NodeService
import com.bknprocessing.node.utils.logger
import org.slf4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class StarterConfig(
    val totalNodesCount: Int,
    val isHealthy: Boolean,
    val nodeIndex: Int,
    val createdAt: Long,
)

@RestController
class NodeController(
    private val nodeService: NodeService,
) {

    private val log: Logger by logger()

    @PostMapping("/init")
    fun init(@RequestBody starterConfig: StarterConfig): String {
        log.info("NodeController: init processed")
        nodeService.init(
            starterConfig.totalNodesCount,
            starterConfig.isHealthy,
            starterConfig.nodeIndex,
            starterConfig.createdAt,
        )
        return "Ok"
    }

    @PostMapping("/verifyObj")
    fun verifyObj(@RequestBody obj: Any) {
        log.info("NodeController: verifyObj processed")
        nodeService.verifyObj(obj)
    }

    @PostMapping("/verify")
    fun verify(@RequestBody obj: Any) {
        log.info("NodeController: verify processed")
        nodeService.verify(obj)
    }

    @PostMapping("/verifyResult")
    fun verifyResult(@RequestBody obj: Any) {
        log.info("NodeController: verifyResult processed")
        nodeService.verifyResult(obj)
    }

    @PostMapping("/smartContract")
    fun smartContract(@RequestBody obj: Any) {
        log.info("NodeController: smartContract processed")
        nodeService.smartContract(obj)
    }

    @GetMapping("/healthCheck")
    fun healthCheck() {
    }
}
