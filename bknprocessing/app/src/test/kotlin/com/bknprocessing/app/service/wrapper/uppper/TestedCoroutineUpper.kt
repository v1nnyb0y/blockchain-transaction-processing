package com.bknprocessing.app.service.wrapper.uppper

import com.bknprocessing.app.service.upper.localupper.CoroutineLocalUpper
import com.bknprocessing.common.data.Transaction
import com.bknprocessing.node.nodeimpl.Node

class TestedCoroutineUpper : CoroutineLocalUpper<Transaction>(), ITestedUpper<Transaction> {

    override fun getListNodes(): List<Node<*>> = nodes.map { it as Node<*> }
}
