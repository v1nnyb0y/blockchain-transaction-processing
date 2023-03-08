package com.bknprocessing.node.dto

import com.bknprocessing.common.data.Transaction
import com.bknprocessing.common.grpc.BaseProtoFile
import com.bknprocessing.common.grpc.BaseProtoFileCompanion
import com.bknprocessing.common.protoClasses.Block
import com.google.protobuf.Int32Value
import com.google.protobuf.Int64Value
import com.google.protobuf.StringValue
import java.time.Instant
import java.util.UUID

data class NodeInfoSubBlock(
    val amount: Int,
    val index: Int,
    val id: UUID,
) : BaseProtoFile() {

    override fun toProto(): com.bknprocessing.common.protoClasses.NodeInfoSubBlock {
        val builder = com.bknprocessing.common.protoClasses.NodeInfoSubBlock.newBuilder()

        builder.amount = Int32Value.of(amount)
        builder.index = Int32Value.of(index)
        builder.id = StringValue.of(id.toString())

        return builder.build()
    }

    companion object : BaseProtoFileCompanion() {
        override fun fromProto(obj: Any): Any {
            val info = (obj as? com.bknprocessing.common.protoClasses.NodeInfoSubBlock)
                ?: throw IllegalStateException("Impossible to cast proto to class")
            return NodeInfoSubBlock(
                amount = info.amount.value,
                index = info.index.value,
                id = UUID.fromString(info.id.value),
            )
        }
    }
}

data class Block<T>(
    val previousHash: String,
    val nodeInfo: NodeInfoSubBlock?,

    var processingTime: Long,
    var currentHash: String = "",
    val timestamp: Long = Instant.now().toEpochMilli(),

    val objs: MutableList<T> = mutableListOf(),

    var nonce: Long = 0,
) : BaseProtoFile() {

    fun addObj(tx: T) {
        objs.add(tx)
    }

    override fun toProto(): Block {
        val builder = Block.newBuilder()

        builder.previousHash = StringValue.of(previousHash)
        builder.nodeInfo = nodeInfo!!.toProto()
        builder.currentHash = StringValue.of(currentHash)
        builder.timestamp = Int64Value.of(timestamp)
        builder.nonce = Int64Value.of(nonce)
        builder.processingTime = Int64Value.of(processingTime)
        builder.addAllObjs(
            objs.map {
                com.google.protobuf.Any.pack((it as Transaction).toProto())
            },
        )

        return builder.build()
    }

    companion object : BaseProtoFileCompanion() {
        override fun fromProto(obj: Any): Any {
            val blck = (obj as? Block)
                ?: throw IllegalStateException("Impossible to cast proto to class")

            return Block<Any>(
                previousHash = blck.previousHash.value,
                nodeInfo = NodeInfoSubBlock.fromProto(blck.nodeInfo) as NodeInfoSubBlock,
                currentHash = blck.currentHash.value,
                timestamp = blck.timestamp.value,
                nonce = blck.nonce.value,
                objs = blck.objsList.map {
                    val trans = it.unpack(com.bknprocessing.common.protoClasses.Transaction::class.java)
                    Transaction.fromProto(trans)
                }.toMutableList(),
                processingTime = blck.processingTime.value,
            )
        }
    }
}
