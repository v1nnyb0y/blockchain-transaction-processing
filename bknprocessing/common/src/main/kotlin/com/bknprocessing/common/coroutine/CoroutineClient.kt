package com.bknprocessing.common.coroutine

import com.bknprocessing.common.IClient
import com.bknprocessing.common.globals.ClientConfiguration
import com.bknprocessing.common.globals.CoroutineClientConfiguration
import com.bknprocessing.common.globals.TopicsList
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel

class CoroutineClient private constructor() : IClient {

    private val blockVerificationReceiveChannels: MutableList<ReceiveChannel<Any>> = mutableListOf()
    private val smartContractReceiveChannels: MutableList<ReceiveChannel<Any>> = mutableListOf()

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun setup(configuration: ClientConfiguration) {
        val castedConfiguration = configuration as? CoroutineClientConfiguration ?: throw IllegalStateException("Wrong client configuration")
        CoroutineTransferStorage.blockVerificationChannel = BroadcastChannel(capacity = castedConfiguration.capacity)
        CoroutineTransferStorage.smartContractChannel = BroadcastChannel(capacity = castedConfiguration.capacity)
        for (idx in 0 until castedConfiguration.capacity) {
            blockVerificationReceiveChannels.add(CoroutineTransferStorage.blockVerificationChannel.openSubscription())
            smartContractReceiveChannels.add(CoroutineTransferStorage.smartContractChannel.openSubscription())
        }
    }

    override fun getObj(from: String): Any? {
        return getObj(from, -1)
    }

    override fun getObj(from: String, who: Int): Any? {
        return when (from) {
            TopicsList.ObjQueue.name -> CoroutineTransferStorage.objChannel.tryReceive().getOrNull()
            TopicsList.VerificationBlockQueue.name -> {
                if (who == -1) throw IllegalStateException("Impossible to read subscription of -1 invoker")
                blockVerificationReceiveChannels[who].tryReceive().getOrNull()
            }
            TopicsList.VerificationResultBlockQueue.name -> CoroutineTransferStorage.blockVerificationResultChannel.tryReceive().getOrNull()
            TopicsList.StateChange.name -> {
                if (who == -1) throw IllegalStateException("Impossible to read subscription of -1 invoker")
                smartContractReceiveChannels[who].tryReceive().getOrNull()
            }
            else -> null
        }
    }

    companion object {
        val INSTANCE = CoroutineClient()
    }
}
