package com.bknprocessing.common.coroutine

import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.ServerConfiguration
import com.bknprocessing.common.globals.TopicsList
import kotlinx.coroutines.ObsoleteCoroutinesApi

class CoroutineServer private constructor() : IServer {

    override fun setup(configuration: ServerConfiguration) { }

    @OptIn(ObsoleteCoroutinesApi::class)
    override fun sendObj(obj: Any, to: String): Boolean {
        return when (to) {
            TopicsList.ObjQueue.name -> CoroutineTransferStorage.objChannel.trySend(obj).isSuccess
            TopicsList.VerificationBlockQueue.name -> CoroutineTransferStorage.blockVerificationChannel.trySend(obj).isSuccess
            TopicsList.VerificationResultBlockQueue.name -> CoroutineTransferStorage.blockVerificationResultChannel.trySend(obj).isSuccess
            TopicsList.StateChange.name -> CoroutineTransferStorage.smartContractChannel.trySend(obj).isSuccess
            else -> false
        }
    }

    companion object {
        val INSTANCE = CoroutineServer()
    }
}
