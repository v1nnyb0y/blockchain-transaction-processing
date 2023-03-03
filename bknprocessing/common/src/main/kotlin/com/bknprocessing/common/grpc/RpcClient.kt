package com.bknprocessing.common.grpc

import com.bknprocessing.common.IClient
import com.bknprocessing.common.globals.ClientConfiguration

class RpcClient : IClient {

    override fun setup(configuration: ClientConfiguration) {
        TODO("Not yet implemented")
    }

    override fun getObj(from: String): Any? {
        TODO("Not yet implemented")
    }

    override fun getObj(from: String, who: Int): Any? {
        TODO("Not yet implemented")
    }

    companion object {
        val INSTANCE = RpcClient()
    }
}
