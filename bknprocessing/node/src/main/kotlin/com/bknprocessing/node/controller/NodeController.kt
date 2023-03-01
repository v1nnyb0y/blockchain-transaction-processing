package com.bknprocessing.app.controllers

import com.bknprocessing.node.service.NodeService
import com.bknprocessing.node.utils.logger
import org.slf4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class NodeController(
    private val nodeService: NodeService,
) {

    private val log: Logger by logger()

    @PostMapping("/init")
    fun init(
        @RequestParam totalNodesCount: Int,
        @RequestParam unhealthyNodesCount: Int,
        @RequestParam nodeIndex: Int,
    ): String {
        log.info("NodeController: init processed")
        nodeService.init(totalNodesCount, unhealthyNodesCount, nodeIndex)
        return "Ok"
    }

    @PostMapping("/verify")
    fun verify(@RequestParam obj: Any): String {
        log.info("NodeController: verify processed")
        nodeService.verify(obj)
        return "Ok"
    }

    @PostMapping("/verifyResult")
    fun verifyResult(@RequestParam obj: Any): String {
        log.info("NodeController: verifyResult processed")
        nodeService.verifyResult(obj)
        return "Ok"
    }

    @PostMapping("/smartContract")
    fun smartContract(@RequestParam obj: Any): String {
        log.info("NodeController: smartContract processed")
        nodeService.smartContract(obj)
        return "Ok"
    }

    @GetMapping("/healthCheck")
    fun healthCheck(): String {
        return "Ok"
    }
}
