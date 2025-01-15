import React from 'react';
import logo from '../assets/img/logo.svg'
import '../assets/css/welcome.css';
import { BrowserRouter, Route, Link } from "react-router-dom";

function Welcome() {
  return (
    <div className="container-fluid welcome bg">
      <div className="row justify-content-center">
        <div className="col-sm-10 col-sm-offset-1 col-md-8 col-md-offset-2 col-lg-8 col-lg-offset-2">
          <div className="row welcome-header">
            <div><img src={logo} alt="GOSI" height="50px"/></div>
            <h1>Welcome to <strong>GOSI</strong></h1>
          </div>
          <div className="row">

            <div className="col-xs-12 col-sm-6">
              <div className="card-pf h-l">
                <h3><Link to="/tasklist/tasks" className="text-primary"><i className="bi bi-inboxes"></i> Tasklist</Link></h3>
                <div className="description">
                  Work on your tasks and claim unassigned tasks.
                </div>
              </div>
            </div>
            <div className="col-xs-12 col-sm-6">
              <div className="card-pf h-l">

                <div className="welcome-primary-link">
                  <h3><Link to="/admin" className="text-primary"><i className="bi bi-gear-wide-connected"></i> Admin Console</Link></h3>
                  <div className="description">
                    Centrally manage all aspects of your applications, forms and email templates
                  </div>
                </div>
              </div>
            </div>
          
      </div>
        </div>
      </div>
    </div>
  );
}

export default Welcome;
