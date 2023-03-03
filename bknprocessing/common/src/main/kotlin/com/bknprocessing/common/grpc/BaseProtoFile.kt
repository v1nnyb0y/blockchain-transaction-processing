package com.bknprocessing.common.grpc

abstract class BaseProtoFile {

    abstract fun toProto()

    abstract fun fromProto()
}
