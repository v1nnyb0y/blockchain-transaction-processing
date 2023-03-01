package com.bknprocessing.node

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NodeApplication

fun main(args: Array<String>) {
    runApplication<NodeApplication>(*args)
}
