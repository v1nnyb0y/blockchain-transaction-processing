package com.bknprocessing.common.grpc

import com.bknprocessing.common.IServer
import com.bknprocessing.common.globals.RpcServerConfiguration
import com.bknprocessing.common.globals.ServerConfiguration
import com.bknprocessing.common.globals.TopicsList
import com.bknprocessing.common.protoClasses.NodeInit
import com.bknprocessing.common.protoClasses.StateChange
import com.bknprocessing.common.protoClasses.Transaction
import com.bknprocessing.common.protoClasses.Verification
import com.bknprocessing.common.protoClasses.VerificationResult
import com.bknprocessing.common.protoService.RpcServiceGrpc
import com.google.protobuf.Int32Value
import com.google.protobuf.StringValue
import io.grpc.ManagedChannelBuilder
import java.util.UUID

class RpcServer private constructor() : IServer {

    private var networkSize: Int = 0

    override fun setup(configuration: ServerConfiguration) {
        val castedConfiguration = configuration as? RpcServerConfiguration
            ?: throw IllegalStateException("Wrong client configuration")

        networkSize = castedConfiguration.capacity
    }

    override fun sendObj(element: Any, topic: String): Boolean {
        var castedElement: BaseProtoFile? = null
        if (topic != TopicsList.StateChange.name) {
            castedElement = (element as? BaseProtoFile)
                ?: throw IllegalStateException("Impossible to send request without proto extension")
        }

        return when (topic) {
            TopicsList.ObjQueue.name -> sendToObjQueue(castedElement!!)
            TopicsList.VerificationBlockQueue.name -> sendToVerificationQueue(castedElement!!)
            TopicsList.VerificationResultBlockQueue.name -> sendToVerificationResultQueue(castedElement!!)
            TopicsList.StateChange.name -> sendToStateChangeQueue(element)
            else -> false
        }
    }

    fun initNode(idx: Int, conf: BaseProtoFile): String {
        val channel = ManagedChannelBuilder.forAddress("localhost", PORT + idx)
            .usePlaintext()
            .build()
        val stub = RpcServiceGrpc.newFutureStub(channel)

        val request = (conf.toProto() as? NodeInit)
            ?: throw IllegalStateException("Impossible to cast class to proto")
        val response = stub.initNode(request)

        return "Ok"
    }

    private fun sendToObjQueue(element: BaseProtoFile): Boolean {
        val stub = buildStub(port = PORT)

        val request = (element.toProto() as? Transaction)
            ?: throw IllegalStateException("Impossible to cast class to proto")
        val response = stub.verifyObj(request)

        return response.value.isNotEmpty()
    }

    private fun sendToVerificationQueue(element: BaseProtoFile): Boolean {
        return (1 until networkSize).map {
            val stub = buildStub(port = PORT + it)

            val request = (element.toProto() as? Verification)
                ?: throw IllegalStateException("Impossible to cast class to proto")
            val response = stub.verify(request)

            response.value.isNotEmpty()
        }.all { true }
    }

    private fun sendToVerificationResultQueue(element: BaseProtoFile): Boolean {
        val stub = buildStub(port = PORT)

        val request = (element.toProto() as? VerificationResult)
            ?: throw IllegalStateException("Impossible to cast class to proto")
        val response = stub.verifyResult(request)

        return response.value.isNotEmpty()
    }

    private fun sendToStateChangeQueue(element: Any): Boolean {
        return when (element) {
            is Int -> {
                (0 until networkSize).map {
                    val channel = ManagedChannelBuilder.forAddress("localhost", PORT + it)
                        .usePlaintext()
                        .build()
                    val stub = RpcServiceGrpc.newFutureStub(channel)

                    val request = Int32Value.of(element)
                    val response = stub.stateChangeInt(request)

                    true
                }.all { true }
            }
            is UUID -> {
                (0 until networkSize).map {
                    val stub = buildStub(port = PORT + it)

                    val request = StringValue.of(element.toString())
                    val response = stub.stateChangeUid(request)

                    response.value.isNotEmpty()
                }.all { true }
            }
            is BaseProtoFile -> {
                (0 until networkSize).map {
                    val stub = buildStub(port = PORT + it)

                    val request = (element.toProto() as? StateChange)
                        ?: throw IllegalStateException("Impossible to cast class to proto")
                    val response = stub.stateChange(request)

                    response.value.isNotEmpty()
                }.all { true }
            }
            else -> throw IllegalStateException("Wrong input data for request: stateChange")
        }
    }

    private fun buildStub(port: Int): RpcServiceGrpc.RpcServiceBlockingStub {
        val channel = ManagedChannelBuilder.forAddress("localhost", port)
            .usePlaintext()
            .build()
        return RpcServiceGrpc.newBlockingStub(channel)
    }

    companion object {
        val INSTANCE = RpcServer()
        val PORT = 9090
    }
}
