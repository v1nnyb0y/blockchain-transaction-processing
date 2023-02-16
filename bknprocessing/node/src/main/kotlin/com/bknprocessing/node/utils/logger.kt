package com.bknprocessing.node.utils // ktlint-disable filename

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <R : Any> R.logger() = lazy { LoggerFactory.getLogger(this::class.java.name) }

private fun buildStringBuilder(isNodeHealthy: Boolean, index: Int): StringBuilder {
    val healthyStr = if (isNodeHealthy) "healthy" else "unhealthy"
    return StringBuilder("Node with index: $index ($healthyStr) ")
}

fun Logger.constructBlock(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is constructing block")
            .toString(),
    )

fun Logger.startMining(isNodeHealthy: Boolean, index: Int, blockHash: String) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is started mining block (hash = $blockHash)")
            .toString(),
    )

fun Logger.endMining(isNodeHealthy: Boolean, index: Int, blockHash: String) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is finished mining block (hash = $blockHash)")
            .toString(),
    )

fun Logger.blockAlreadyMined(isNodeHealthy: Boolean, index: Int, blockHash: String) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is mined block (already mined) (hash = $blockHash)")
            .toString(),
    )

fun Logger.startVerify(isNodeHealthy: Boolean, index: Int, blockHash: String) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is started verifying block (hash = $blockHash)")
            .toString(),
    )

fun Logger.endVerify(isNodeHealthy: Boolean, index: Int, blockHash: String, isSuccess: Boolean) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is finished (${if (isSuccess) "SUCCESS" else "FAILED"}) verifying block (hash = $blockHash)")
            .toString(),
    )
