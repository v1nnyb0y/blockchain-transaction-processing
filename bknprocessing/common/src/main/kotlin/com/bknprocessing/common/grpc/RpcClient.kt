package com.bknprocessing.common.grpc

import com.bknprocessing.common.rest.RestClient

class RpcClient : RestClient() {

    companion object {
        val INSTANCE = RpcClient()
    }
}
