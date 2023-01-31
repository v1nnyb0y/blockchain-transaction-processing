// Import React section
import React, {useState} from 'react';

// Import components
import {NumberBox} from 'devextreme-react/number-box';
import {Button} from 'devextreme-react/button';
import SelectBox from 'devextreme-react/select-box';
import {RangeRule, RequiredRule, Validator} from "devextreme-react/validator";

// Import styles
import './MasterPage.css';
import {StateTransferApproach, StateTransferNode, ValidatorAlgo, ValidatorAlgoNode} from "./MasterPage.types";
import {ClickEvent} from "devextreme/ui/button";

const stateTransferApproachSource: Array<StateTransferNode> = [
    {approach: StateTransferApproach.REST, value: "REST"},
    {approach: StateTransferApproach.gRPC, value: "gRPC"},
    {approach: StateTransferApproach.socket, value: "socket"},
    {approach: StateTransferApproach.Kafka, value: "Kafka"},
    {approach: StateTransferApproach.Coroutine, value: "Coroutine"}
];

const validatorAlgoSource: Array<ValidatorAlgoNode> = [
    {algo: ValidatorAlgo.ProofOfState, value: "ProofOfState"}
];

const MasterPage = () => {
    const [numberOfInstances, setNumberOfInstances] = useState<number>(1);
    const [numberOfTransactions, setNumberOfTransactions] = useState<number>(1);
    const [numberOfUnhealthyNodes, setNumberOfUnhealthyNodes] = useState<number>(1);
    const [validatorAlgo, setValidatorAlgo] = useState<ValidatorAlgo | undefined>(undefined);
    const [stateTransferApproach, setStateTransferApproach] = useState<StateTransferApproach | undefined>(undefined);

    const onNumberOfUnhealthyNodesChanged = (arg: { value?: number }) => {
        setNumberOfUnhealthyNodes(arg.value ?? 0);
    }

    const onNumberOfTransactionsChanged = (arg: { value?: number }) => {
        setNumberOfTransactions(arg.value ?? 0);
    }

    const onNumberOfInstancesChanged = (arg: { value?: number }) => {
        setNumberOfInstances(arg.value ?? 0);
    }

    const onStateTransferApproachChanged = (arg: { value?: StateTransferApproach }) => {
        setStateTransferApproach(arg.value);
    }

    const onValidatorAlgoChanged = (arg: { value?: ValidatorAlgo }) => {
        setValidatorAlgo(arg.value);
    }

    const onSubmit = (arg: ClickEvent) => {
        let validation = arg.validationGroup.validate();
        if (validation.isValid) {
            console.group();
            console.log(`Number of Instances: ${numberOfInstances}`);
            console.log(`Number of Unhealthy Nodes: ${numberOfUnhealthyNodes}`);
            console.log(`Number of Transactions: ${numberOfTransactions}`)
            console.log(`State Transfer Approach: ${stateTransferApproach}`);
            console.log(`Validation Algorithm: ${validatorAlgo}`);
            console.groupEnd();

            fetch('/asyncExperiment', {
                method: 'POST',
                mode: 'cors',
                cache: 'no-cache',
                credentials: 'same-origin',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    numberOfInstances,
                    numberOfTransactions,
                    numberOfUnhealthyNodes,
                    stateTransferApproach,
                    validatorAlgo
                })
            })
                .then((res) => {
                    console.log(res);
                })
                .catch((err) => {
                    console.log(err);
                })
        } else {
            alert('ERROR');
        }
    }

    return (
        <div className={"master-page"}>
            <div className={'mb-2'}>
                <div className={'row'}>
                    <div className={'col-3'}>
                        <NumberBox
                            max={100}
                            min={1}
                            mode={'number'}
                            label={'Number of instances'}
                            height={'3em'}
                            showSpinButtons={true}
                            onValueChanged={onNumberOfInstancesChanged}
                            value={numberOfInstances}
                        >
                            <Validator>
                                <RangeRule min={0} max={100} message={'Number of instances should be in range 0-100'}/>
                                <RequiredRule message={'Number of instances is required'}/>
                            </Validator>
                        </NumberBox>
                    </div>
                    <div className={'col-3'}>
                        <NumberBox
                            max={1000000}
                            min={1}
                            mode={'number'}
                            label={'Number of transactions'}
                            height={'3em'}
                            showSpinButtons={true}
                            onValueChanged={onNumberOfTransactionsChanged}
                            value={numberOfTransactions}
                        >
                            <Validator>
                                <RangeRule min={0} max={1000000}
                                           message={'Number of transactions should be in range 0-1000000'}/>
                                <RequiredRule message={'Number of transactions is required'}/>
                            </Validator>
                        </NumberBox>
                    </div>
                    <div className={'col-3'}>
                        <SelectBox
                            label={'State transfer approach'}
                            items={stateTransferApproachSource}
                            height={'3em'}
                            displayExpr={'approach'}
                            valueExpr={'value'}
                            onValueChanged={onStateTransferApproachChanged}
                            value={stateTransferApproach}
                        >
                            <Validator>
                                <RequiredRule message={'State Transfer approach is required'}/>
                            </Validator>
                        </SelectBox>
                    </div>
                    <div className={'col-3'}>
                        <SelectBox
                            label={'Validator algorithm'}
                            items={validatorAlgoSource}
                            height={'3em'}
                            displayExpr={'algo'}
                            valueExpr={'value'}
                            onValueChanged={onValidatorAlgoChanged}
                            value={validatorAlgo}
                        >
                            <Validator>
                                <RequiredRule message={'Validator algorithm is required'}/>
                            </Validator>
                        </SelectBox>
                    </div>
                </div>
                <div className={'row'}>
                    <div className={'col-3'}>
                        <NumberBox
                            max={100}
                            min={1}
                            mode={'number'}
                            label={'Number of unhealthy nodes'}
                            height={'3em'}
                            showSpinButtons={true}
                            onValueChanged={onNumberOfUnhealthyNodesChanged}
                            value={numberOfUnhealthyNodes}
                        >
                            <Validator>
                                <RangeRule min={0} max={100}
                                           message={'Number of unhealthy nodes should be in range 0-100'}/>
                                <RequiredRule message={'Number of unhealthy nodes is required'}/>
                            </Validator>
                        </NumberBox>
                    </div>
                </div>
                <div className={'mt-2'}>
                    <Button
                        text={'Start testing'}
                        type={'success'}
                        stylingMode={'contained'}
                        height={'3em'}
                        onClick={onSubmit}
                    />
                </div>
            </div>
            <div className={'diagrams'}>

            </div>
        </div>
    )
};

export default MasterPage;
