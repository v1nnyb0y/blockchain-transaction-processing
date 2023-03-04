package com.bknprocessing.node.controller

import com.bknprocessing.common.protoClasses.NodeInit
import com.bknprocessing.common.protoClasses.StateChange
import com.bknprocessing.common.protoClasses.Transaction
import com.bknprocessing.common.protoClasses.Verification
import com.bknprocessing.common.protoClasses.VerificationResult
import com.bknprocessing.common.protoService.RpcServiceGrpc
import com.bknprocessing.node.dto.StateChangeDto
import com.bknprocessing.node.dto.VerificationDto
import com.bknprocessing.node.dto.VerificationResultDto
import com.bknprocessing.node.service.RpcService
import com.bknprocessing.node.utils.logger
import com.google.protobuf.Int32Value
import com.google.protobuf.StringValue
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.Logger
import java.util.UUID

@GrpcService
class GrpcController(
    private val rpcService: RpcService,
) : RpcServiceGrpc.RpcServiceImplBase() {

    private val log: Logger by logger()

    override fun initNode(request: NodeInit?, responseObserver: StreamObserver<StringValue>?) {
        log.info("GrpcController: init processed")
        if (request != null) {
            rpcService.init(
                request.totalNodesCount.value,
                request.isHealthy.value,
                request.nodeIndex.value,
                request.createdAt.value,
            )
        }
        responseObserver?.onNext(StringValue.of("Ok"))
        responseObserver?.onCompleted()
    }

    override fun verifyObj(request: Transaction?, responseObserver: StreamObserver<StringValue>?) {
        log.info("GrpcController: verifyObj processed")
        if (request != null) {
            rpcService.verifyObj(com.bknprocessing.common.data.Transaction.fromProto(request))
        }
        responseObserver?.onNext(StringValue.of("Ok"))
        responseObserver?.onCompleted()
    }

    override fun verify(request: Verification?, responseObserver: StreamObserver<StringValue>?) {
        log.info("GrpcController: verify processed")
        if (request != null) {
            rpcService.verify(VerificationDto.fromProto(request))
        }
        responseObserver?.onNext(StringValue.of("Ok"))
        responseObserver?.onCompleted()
    }

    override fun verifyResult(request: VerificationResult?, responseObserver: StreamObserver<StringValue>?) {
        log.info("GrpcController: verifyResult processed")
        if (request != null) {
            rpcService.verifyResult(VerificationResultDto.fromProto(request))
        }
        responseObserver?.onNext(StringValue.of("Ok"))
        responseObserver?.onCompleted()
    }

    override fun stateChange(request: StateChange?, responseObserver: StreamObserver<StringValue>?) {
        log.info("GrpcController: smartContract processed")
        if (request != null) {
            rpcService.smartContract(StateChangeDto.fromProto(request))
        }
        responseObserver?.onNext(StringValue.of("Ok"))
        responseObserver?.onCompleted()
    }

    override fun stateChangeInt(request: Int32Value?, responseObserver: StreamObserver<StringValue>?) {
        log.info("GrpcController: smartContractInt processed")
        if (request != null) {
            rpcService.smartContract(request.value)
        }
        responseObserver?.onNext(StringValue.of("Ok"))
        responseObserver?.onCompleted()
    }

    override fun stateChangeUid(request: StringValue?, responseObserver: StreamObserver<StringValue>?) {
        log.info("GrpcController: smartContractUid processed")
        if (request != null) {
            rpcService.smartContract(UUID.fromString(request.value))
        }
        responseObserver?.onNext(StringValue.of("Ok"))
        responseObserver?.onCompleted()
    }
}
