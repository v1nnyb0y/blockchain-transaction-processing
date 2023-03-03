package com.bknprocessing.app.service.worker

import com.bknprocessing.common.grpc.RpcClient
import com.bknprocessing.common.grpc.RpcServer

class RestRpcWorker<T>(
    client: RpcClient = RpcClient.INSTANCE,
    server: RpcServer = RpcServer.INSTANCE,
) : BaseWorker<T>(client, server)
