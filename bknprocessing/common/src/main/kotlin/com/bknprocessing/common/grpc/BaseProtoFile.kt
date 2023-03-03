package com.bknprocessing.common.grpc

abstract class BaseProtoFile {

    abstract fun toProto(): Any
}

abstract class BaseProtoFileCompanion {
    abstract fun fromProto(obj: Any): Any
}
