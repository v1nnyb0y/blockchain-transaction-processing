package com.bknprocessing.app.utils // ktlint-disable filename

import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal fun <R : Any> R.logger() = lazy { LoggerFactory.getLogger(this::class.java.name) }

private fun buildStringBuilder(isNodeHealthy: Boolean, index: Int): StringBuilder {
    val healthyStr = if (isNodeHealthy) "healthy" else "unhealthy"
    return StringBuilder("Node with index: $index ($healthyStr) ")
}

internal fun Logger.constructedNode(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("constructed")
            .toString(),
    )

internal fun Logger.clientAndServerConfigured() =
    info("Client and Server are upped")

internal fun Logger.sendToVerify() =
    info("Transaction send to verify")

internal fun Logger.startFinishingProcess() =
    info("Start finishing process for all nodes")
