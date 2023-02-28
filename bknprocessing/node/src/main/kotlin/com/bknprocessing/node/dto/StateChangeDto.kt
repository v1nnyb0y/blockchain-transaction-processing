package com.bknprocessing.node.dto

enum class StateAction {
    ACTUALIZE, ACCEPT_NEW_BLOCK, FINISH,
    SET_NEW_MINER,
}

data class StateChangeDto<T>(
    val data: Block<T>,
    val action: StateAction,
)
