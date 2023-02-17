package com.bknprocessing.common.coroutine

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED

@OptIn(ObsoleteCoroutinesApi::class)
class CoroutineTransferStorage private constructor() {

    companion object {
        val objChannel = Channel<Any>(capacity = 1)
        var blockVerificationChannel = BroadcastChannel<Any>(capacity = 1)
        val blockVerificationResultChannel = Channel<Any>(capacity = UNLIMITED)
    }
}
