package com.bknprocessing.node.utils // ktlint-disable filename

import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal fun <R : Any> R.logger() = lazy { LoggerFactory.getLogger(this::class.java.name) }

private fun buildStringBuilder(isNodeHealthy: Boolean, index: Int): StringBuilder {
    val healthyStr = if (isNodeHealthy) "healthy" else "unhealthy"
    return StringBuilder("Node with index: $index ($healthyStr) ")
}

internal fun Logger.startMiner(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("start miner")
            .toString(),
    )

internal fun Logger.startVerifier(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("start verifier")
            .toString(),
    )

internal fun Logger.startSmartContractListener(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("start smart-contract listener")
            .toString(),
    )

internal fun Logger.finishMiner(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("finish miner")
            .toString(),
    )

internal fun Logger.finishVerifier(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("finish verifier")
            .toString(),
    )

internal fun Logger.finishSmartContractListener(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("finish smart-contract listener")
            .toString(),
    )

internal fun Logger.constructedBlock(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is constructed block")
            .toString(),
    )

internal fun Logger.minedBlock(isNodeHealthy: Boolean, index: Int, blockHash: String) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is mined block (hash = $blockHash)")
            .toString(),
    )

internal fun Logger.startNetworkVerify(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is started NETWORK verify")
            .toString(),
    )

internal fun Logger.startVerify(isNodeHealthy: Boolean, index: Int, blockHash: String) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is started verifying block (hash = $blockHash)")
            .toString(),
    )

internal fun Logger.endVerify(isNodeHealthy: Boolean, index: Int, blockHash: String, isSuccess: Boolean) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is finished (${if (isSuccess) "SUCCESS" else "FAILED"}) verifying block (hash = $blockHash)")
            .toString(),
    )

internal fun Logger.startSmAcceptNewBlock(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is accepting new block")
            .toString(),
    )

internal fun Logger.startSmActualizeChain(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is actualising chain")
            .toString(),
    )

internal fun Logger.startSmFinishProcess(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is started finishing process")
            .toString(),
    )

internal fun Logger.setNewMiner(isNodeHealthy: Boolean, index: Int) =
    info(
        buildStringBuilder(isNodeHealthy, index)
            .append("is new miner")
            .toString(),
    )
