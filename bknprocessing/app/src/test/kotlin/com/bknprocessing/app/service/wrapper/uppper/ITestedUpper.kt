package com.bknprocessing.app.service.wrapper.uppper

import com.bknprocessing.app.service.upper.IUpper
import com.bknprocessing.node.nodeimpl.Node

interface ITestedUpper<T> : IUpper<T> {

    fun getListNodes(): List<Node<*>>
}
