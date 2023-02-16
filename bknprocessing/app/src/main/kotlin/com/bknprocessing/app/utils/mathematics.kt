package com.bknprocessing.app.utils // ktlint-disable filename

import com.bknprocessing.app.service.OldPoolService

fun determineNextIterationMinerIndex(nodesMap: Map<Int, OldPoolService.VerifiedBlocksAndAmountInfo>): Int {
    if (nodesMap.size < 2) {
        throw IllegalStateException("Determine index process: Node size is incorrect")
    }
    var leaderValue = -1
    var leaderIndex = -1
    nodesMap.entries.forEach {
        if (leaderValue < it.value.amount / it.value.blocksCount) {
            leaderValue = it.value.amount
            leaderIndex = it.key
        }
    }
    return leaderIndex
}
