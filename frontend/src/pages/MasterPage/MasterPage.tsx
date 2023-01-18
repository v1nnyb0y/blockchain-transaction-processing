// Import React section
import React, {useState} from 'react';

// Import components
import {NumberBox} from 'devextreme-react/number-box';
import { Button } from 'devextreme-react/button';
import SelectBox from 'devextreme-react/select-box';
import {Validator, RequiredRule, RangeRule} from "devextreme-react/validator";

// Import styles
import './MasterPage.css';
import {StateTransferApproach, StateTransferNode, ValidatorAlgo, ValidatorAlgoNode} from "./MasterPage.types";
import {ClickEvent} from "devextreme/ui/button";

const stateTransferApproachSource: Array<StateTransferNode> = [
    { approach: StateTransferApproach.REST, value: StateTransferApproach.REST },
    { approach: StateTransferApproach.gRPC, value: StateTransferApproach.gRPC },
    { approach: StateTransferApproach.socket, value: StateTransferApproach.socket },
    { approach: StateTransferApproach.Kafka, value: StateTransferApproach.Kafka }
];

const validatorAlgoSource: Array<ValidatorAlgoNode> = [
    { algo: ValidatorAlgo.ProofOfWork, value: ValidatorAlgo.ProofOfWork },
    { algo: ValidatorAlgo.ProofOfState, value: ValidatorAlgo.ProofOfState }
];

const MasterPage = () => {
    const [numberOfInstances, setNumberOfInstances] = useState<number>(0);
    const [validatorAlgo, setValidatorAlgo] = useState<ValidatorAlgo | undefined>(undefined);
    const [stateTransferApproach, setStateTransferApproach] = useState<StateTransferApproach | undefined>(undefined);

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
            console.log(numberOfInstances);
            console.log(stateTransferApproach);
            console.log(validatorAlgo);

            // fetch data to backend
        } else {
            console.log('ERROR');
        }
    }

    return (
        <div className={"master-page"}>
            <div className={'mb-2'}>
                <div className={'row'}>
                    <div className={'col-4'}>
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
                                <RangeRule min={0} max={100} message={'Number of instances should be in range 0-100'} />
                            </Validator>
                        </NumberBox>
                    </div>
                    <div className={'col-4'}>
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
                                <RequiredRule message={'State Transfer approach is required'} />
                            </Validator>
                        </SelectBox>
                    </div>
                    <div className={'col-4'}>
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
                                <RequiredRule message={'Validator algorithm is required'} />
                            </Validator>
                        </SelectBox>
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
