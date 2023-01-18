// Import React section
import React from 'react';
import ReactDOM from 'react-dom/client';

// Import pages
import MasterPage from 'pages/MasterPage/MasterPage';

// Import styles
import './index.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'devextreme/dist/css/dx.light.css';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
  <React.StrictMode>
    <MasterPage />
  </React.StrictMode>
);
