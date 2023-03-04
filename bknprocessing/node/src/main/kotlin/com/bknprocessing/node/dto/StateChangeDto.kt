package com.bknprocessing.node.dto

import com.bknprocessing.common.grpc.BaseProtoFile
import com.bknprocessing.common.grpc.BaseProtoFileCompanion
import com.bknprocessing.common.protoClasses.StateChange
import com.google.protobuf.StringValue

enum class StateAction {
    ACTUALIZE, ACCEPT_NEW_BLOCK, FINISH,
    SET_NEW_MINER,
}

data class StateChangeDto<T>(
    val data: Block<T>,
    val action: StateAction,
) : BaseProtoFile() {

    override fun toProto(): StateChange {
        val builder = StateChange.newBuilder()

        builder.data = data.toProto()
        builder.action = StringValue.of(action.name)

        return builder.build()
    }

    companion object : BaseProtoFileCompanion() {
        override fun fromProto(obj: Any): Any {
            val sc = (obj as? StateChange)
                ?: throw IllegalStateException("Impossible to cast proto to class")
            return StateChangeDto(
                data = Block.fromProto(sc.data) as Block<Any>,
                action = StateAction.valueOf(sc.action.value),
            )
        }
    }
}
