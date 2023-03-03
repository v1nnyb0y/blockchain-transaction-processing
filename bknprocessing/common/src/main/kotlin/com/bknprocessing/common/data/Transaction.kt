package com.bknprocessing.common.data

import com.bknprocessing.common.grpc.BaseProtoFile
import com.bknprocessing.common.grpc.BaseProtoFileCompanion
import com.bknprocessing.common.protoClasses.Transaction
import com.google.protobuf.StringValue
import java.util.UUID

data class Transaction(
    val transId: UUID = UUID.randomUUID(),
) : BaseProtoFile() {

    override fun toProto(): Transaction {
        val builder = Transaction.newBuilder()
        builder.id = StringValue.of(transId.toString())

        return builder.build()
    }

    companion object : BaseProtoFileCompanion() {
        override fun fromProto(obj: Any): Any {
            val trans = (obj as? Transaction)
                ?: throw IllegalStateException("Impossible to cast proto to class")

            return Transaction(UUID.fromString(trans.id.value))
        }
    }
}
