package com.bknprocessing.node.dto

enum class StateAction {
    ACTUALIZE, ACCEPT_NEW_BLOCK
}

data class StateChangeDto<T>(
    val block: Block<T>?,
    val action: StateAction,
)
