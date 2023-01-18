export enum StateTransferApproach {
    Kafka = 'Kafka',
    gRPC = 'gRPC',
    socket = 'socket',
    REST = 'REST API'
}

export interface StateTransferNode {
    approach: StateTransferApproach,
    value: StateTransferApproach
}

export enum ValidatorAlgo {
    ProofOfWork = 'Proof of Work',
    ProofOfState = 'Proof of State'
}

export interface ValidatorAlgoNode {
    algo: ValidatorAlgo,
    value: ValidatorAlgo
}