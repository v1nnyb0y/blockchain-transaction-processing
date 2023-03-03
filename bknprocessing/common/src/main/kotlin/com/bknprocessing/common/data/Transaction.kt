package com.bknprocessing.common.data

import com.bknprocessing.common.grpc.BaseProtoFile
import java.util.UUID

data class Transaction(
    val transId: UUID = UUID.randomUUID(),
) : BaseProtoFile() {

    override fun toProto() {

    }

    override fun fromProto() {

    }
}
