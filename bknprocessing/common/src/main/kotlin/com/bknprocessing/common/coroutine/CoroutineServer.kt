package com.bknprocessing.common.coroutine

import com.bknprocessing.common.CoroutineServerConfiguration
import com.bknprocessing.common.IServer
import com.bknprocessing.common.ServerConfiguration
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel

class CoroutineServer private constructor() : IServer {

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun setup(configuration: ServerConfiguration) {
        val castedConfiguration = configuration as? CoroutineServerConfiguration ?: throw IllegalStateException("Wrong client configuration")
        CoroutineTransferStorage.blockVerificationChannel = BroadcastChannel(capacity = castedConfiguration.capacity * castedConfiguration.capacity)
    }

    override fun sendObj(obj: Any, to: String): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        val INSTANCE = CoroutineServer()
    }
}
