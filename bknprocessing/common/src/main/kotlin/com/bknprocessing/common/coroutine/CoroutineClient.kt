package com.bknprocessing.common.coroutine

import com.bknprocessing.common.ClientConfiguration
import com.bknprocessing.common.CoroutineClientConfiguration
import com.bknprocessing.common.IClient
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel

class CoroutineClient private constructor() : IClient {

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun setup(configuration: ClientConfiguration) {
        val castedConfiguration = configuration as? CoroutineClientConfiguration ?: throw IllegalStateException("Wrong client configuration")
        CoroutineTransferStorage.blockVerificationChannel = BroadcastChannel(capacity = castedConfiguration.capacity * castedConfiguration.capacity)
    }

    override fun getObj(from: String): Any {
        TODO("Not yet implemented")
    }

    companion object {
        val INSTANCE = CoroutineClient()
    }
}
