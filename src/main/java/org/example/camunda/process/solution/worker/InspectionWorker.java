package org.example.camunda.process.solution.worker;

import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.example.camunda.process.solution.ProcessVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InspectionWorker {

  private static final Logger LOG = LoggerFactory.getLogger(AssigneeWorker.class);

  @JobWorker
  public void checkInspection(@VariablesAsType ProcessVariables processVariables) {
    LOG.info("Invoking myService with variables: " + processVariables);
  }
}
