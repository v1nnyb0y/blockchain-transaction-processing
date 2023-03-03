package com.bknprocessing.node.dto

import com.bknprocessing.common.grpc.BaseProtoFile
import com.bknprocessing.common.grpc.BaseProtoFileCompanion
import com.bknprocessing.common.protoClasses.Verification
import com.google.protobuf.StringValue
import java.util.UUID

data class VerificationDto<T>(
    val nodeId: UUID,
    val block: Block<T>,
) : BaseProtoFile() {

    override fun toProto(): Verification {
        val builder = Verification.newBuilder()

        builder.nodeId = StringValue.of(nodeId.toString())
        builder.block = block.toProto()

        return builder.build()
    }

    companion object : BaseProtoFileCompanion() {
        override fun fromProto(obj: Any): Any {
            val verify = (obj as? Verification)
                ?: throw IllegalStateException("Impossible to cast proto to class")
            return VerificationDto<Any>(
                nodeId = UUID.fromString(verify.nodeId.value),
                block = Block.fromProto(verify.block) as Block<Any>
            )
        }
    }
}
