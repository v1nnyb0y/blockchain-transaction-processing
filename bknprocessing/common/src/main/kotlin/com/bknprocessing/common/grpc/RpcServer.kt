package com.bknprocessing.common.grpc

import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.ServerConfiguration

class RpcServer private constructor() : IServer {

    override fun setup(configuration: ServerConfiguration) {
        TODO("Not yet implemented")
    }

    override fun sendObj(element: Any, topic: String): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        val INSTANCE = RpcServer()
    }
}
