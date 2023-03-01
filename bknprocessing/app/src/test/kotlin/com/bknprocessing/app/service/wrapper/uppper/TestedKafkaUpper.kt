package com.bknprocessing.app.service.wrapper.uppper

import com.bknprocessing.app.service.upper.localupper.KafkaLocalUpper
import com.bknprocessing.common.data.Transaction
import com.bknprocessing.node.nodeimpl.Node

class TestedKafkaUpper : KafkaLocalUpper<Transaction>(), ITestedUpper<Transaction> {

    override fun getListNodes(): List<Node<*>> = nodes.map { it as Node<*> }
}
