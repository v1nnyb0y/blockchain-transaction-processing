package com.bknprocessing.node.dto

import com.bknprocessing.common.grpc.BaseProtoFile
import com.bknprocessing.common.grpc.BaseProtoFileCompanion
import com.bknprocessing.common.protoClasses.VerificationResult
import com.google.protobuf.BoolValue
import com.google.protobuf.Int32Value
import com.google.protobuf.StringValue
import java.util.UUID

data class NodeInfo(
    val id: UUID,
    val amount: Int,
) : BaseProtoFile() {

    override fun toProto(): com.bknprocessing.common.protoClasses.NodeInfo {
        val builder = com.bknprocessing.common.protoClasses.NodeInfo.newBuilder()

        builder.id = StringValue.of(id.toString())
        builder.amount = Int32Value.of(amount)

        return builder.build()
    }

    companion object : BaseProtoFileCompanion() {
        override fun fromProto(obj: Any): Any {
            val info = (obj as? com.bknprocessing.common.protoClasses.NodeInfo)
                ?: throw IllegalStateException("Impossible to cast proto to class")
            return NodeInfo(
                id = UUID.fromString(info.id.value),
                amount = info.amount.value,
            )
        }
    }
}

data class VerificationResultDto(
    val blockHash: String,
    val nodeId: UUID,
    val nodeInfo: NodeInfo,
    val verificationResult: Boolean, // TODO rnm
) : BaseProtoFile() {

    override fun toProto(): VerificationResult {
        val builder = VerificationResult.newBuilder()

        builder.blockHash = StringValue.of(blockHash)
        builder.nodeId = StringValue.of(nodeId.toString())
        builder.nodeInfo = nodeInfo.toProto()
        builder.verificationResult = BoolValue.of(verificationResult)

        return builder.build()
    }

    companion object : BaseProtoFileCompanion() {
        override fun fromProto(obj: Any): Any {
            val res = (obj as? VerificationResult)
                ?: throw IllegalStateException("Impossible to cast proto to class")
            return VerificationResultDto(
                blockHash = res.blockHash.value,
                nodeId = UUID.fromString(res.nodeId.value),
                nodeInfo = NodeInfo.fromProto(res.nodeInfo) as NodeInfo,
                verificationResult = res.verificationResult.value,
            )
        }
    }
}
