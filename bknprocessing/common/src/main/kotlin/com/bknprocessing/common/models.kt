package com.bknprocessing.common // ktlint-disable filename

open class ServerConfiguration(
    open val capacity: Int,
)
open class ClientConfiguration(
    open val capacity: Int,
)

data class CoroutineServerConfiguration(
    override val capacity: Int,
) : ServerConfiguration(capacity)
data class CoroutineClientConfiguration(
    override val capacity: Int,
) : ClientConfiguration(capacity)
