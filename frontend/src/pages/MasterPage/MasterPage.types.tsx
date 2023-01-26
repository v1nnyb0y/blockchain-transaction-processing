export enum StateTransferApproach {
    Kafka = 'Kafka',
    gRPC = 'gRPC',
    socket = 'socket',
    REST = 'REST API',
    Coroutine = 'Coroutines & Channels'
}

export interface StateTransferNode {
    approach: StateTransferApproach,
    value: string
}

export enum ValidatorAlgo {
    ProofOfWork = 'Proof of Work',
    ProofOfState = 'Proof of State'
}

export interface ValidatorAlgoNode {
    algo: ValidatorAlgo,
    value: string
}