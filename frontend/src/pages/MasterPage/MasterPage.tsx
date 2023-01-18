// Import React section
import React from 'react';

// Import components
import { NumberBox } from 'devextreme-react/number-box';

// Import styles
import './MasterPage.css';

const MasterPage = () => {
  return (
      <div className={"master-page"}>
          <div className={'row'}>
              <div className={'col-6'}>
                  <NumberBox
                      max={100}
                      min={1}
                      mode={'number'}
                      label={'Number of instances'}
                      height={'3em'}
                  />
              </div>
          </div>
      </div>
  )
};

export default MasterPage;
