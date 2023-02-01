package com.bknprocessing.backend.models

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface INode {

    fun isMiner(): Boolean

    suspend fun runMining(
        forTransChannel: ReceiveChannel<Transaction>,
        forVerifyChannel: SendChannel<Block>,
        forVerificationResultChannel: Channel<Pair<Boolean, Block>>
    )

    suspend fun runVerifying(
        forVerifyChannel: ReceiveChannel<Block>,
        forResultChannel: SendChannel<Pair<Boolean, Block>>
    )
}
