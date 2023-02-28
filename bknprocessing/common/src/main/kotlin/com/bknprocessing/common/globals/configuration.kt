package com.bknprocessing.common.globals // ktlint-disable filename

open class ServerConfiguration
open class ClientConfiguration

data class CoroutineServerConfiguration(
    val capacity: Int,
) : ServerConfiguration()
data class CoroutineClientConfiguration(
    val capacity: Int,
) : ClientConfiguration()

data class KafkaServerConfiguration(
    val server: String,
    val keySerializer: String,
    val valueSerializer: String,
) : ServerConfiguration()
open class KafkaClientConfiguration(
    val server: String,
    val nodeIndex: Int
) : ClientConfiguration()
