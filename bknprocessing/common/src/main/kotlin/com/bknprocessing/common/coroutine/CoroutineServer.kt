package com.bknprocessing.common.coroutine

import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.ServerConfiguration
import com.bknprocessing.common.globals.TopicsList
import kotlinx.coroutines.ObsoleteCoroutinesApi

class CoroutineServer private constructor() : IServer {

    override fun setup(configuration: ServerConfiguration) { }

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun sendObj(element: Any, topic: String): Boolean {
        return when (topic) {
            TopicsList.ObjQueue.name -> CoroutineTransferStorage.objChannel.trySend(element).isSuccess
            TopicsList.VerificationBlockQueue.name -> CoroutineTransferStorage.blockVerificationChannel.trySend(element).isSuccess
            TopicsList.VerificationResultBlockQueue.name -> CoroutineTransferStorage.blockVerificationResultChannel.trySend(element).isSuccess
            TopicsList.StateChange.name -> CoroutineTransferStorage.smartContractChannel.trySend(element).isSuccess
            else -> false
        }
    }

    companion object {
        val INSTANCE = CoroutineServer()
    }
}
