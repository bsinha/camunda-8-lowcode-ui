import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import type { } from 'redux-thunk/extend-redux';
import authService from '../service/AuthService';
import taskService from '../service/TaskService';
import FormResolver from './FormResolver';
import { OverlayTrigger, Tooltip, Button } from 'react-bootstrap';
import CaseMgmtComponent from './CaseMgmtComponent';

import { useTranslation } from "react-i18next";
import Documents from './Documents';
import InstanceComments from './InstanceComments';

function TaskForm(props: any) {
  const { t } = useTranslation();
  const [enlarged, setEnlarged] = useState<boolean>(false);
  const dispatch = useDispatch();
  const currentSchema = useSelector((state: any) => state.process.currentFormSchema)
  const currentTask = useSelector((state: any) => state.process.currentTask)
  const disabled = !currentTask || !currentTask.assignee || currentTask.assignee != authService.getUser()!.username;

  const claim = () => {
    dispatch(taskService.claim());
  }
  const unclaim = () => {
    dispatch(taskService.unclaim());
  }
  const renderAssigneeTooltip = (props: any) => (
    <Tooltip id="button-tooltip" {...props}>
      {currentTask.assignee}
    </Tooltip>
  );
  return (
    currentSchema && currentTask ?
      <div className={enlarged ? "taskListFormContainer enlarged" : "taskListFormContainer"}>
        <div className="card taskform">
          <h5 className="card-title bg-primary text-light" > {currentTask.name} {currentTask.assignee ? <OverlayTrigger
            placement="right"
            delay={{ show: 250, hide: 400 }}
            overlay={renderAssigneeTooltip}
          >
            <i className="bi bi-person-circle "></i>
          </OverlayTrigger> : <></>}</h5>

          <div className="btnClaimUnClaim d-flex flex-row-reverse">
            {enlarged ?
              <Button variant="outline-light" className="enlarge" onClick={() => setEnlarged(false)}><i className="bi bi-arrows-angle-contract"></i></Button>
              :
              <Button variant="outline-light" className="enlarge" onClick={() => setEnlarged(true)}><i className="bi bi-arrows-angle-expand"></i></Button>
            }
            {currentTask.assignee ?
              <Button variant="dark" onClick={unclaim} className="mx-2" > {t("Unclaim")}</Button>
              :
              <Button variant="dark" onClick={claim} className="mx-2" >{t("Claim")}</Button>
            }
            <CaseMgmtComponent type='task' taskEltId={currentTask.taskDefinitionId} processDefinitionKey={currentTask.processDefinitionKey} bpmnProcessId={null} processInstanceKey={currentTask.processInstanceKey} variables={currentTask.variables} instances={null} redirect="/tasklist/tasks" />
          </div>
          <FormResolver id="task-form" formKey={currentTask.formKey} schema={currentSchema} variables={currentTask.variables} disabled={disabled}></FormResolver>
        </div>
        <Documents />
        <div className="card taskform">
          <h5 className="card-title bg-primary text-light" >Comments</h5>
          <div className="p-4">
            <InstanceComments instancekey={currentTask.processInstanceKey} processDefinitionKey={0} variables={null} />
            </div>
        </div>
      </div> : <div />
  )

}

export default TaskForm;


