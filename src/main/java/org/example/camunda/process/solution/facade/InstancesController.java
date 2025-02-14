package org.example.camunda.process.solution.facade;

import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.ProcessInstanceState;
import io.camunda.operate.model.SearchResult;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.CancelProcessInstanceResponse;
import java.util.List;
import org.example.camunda.process.solution.facade.dto.ProcessInstancesResult;
import org.example.camunda.process.solution.facade.dto.Task;
import org.example.camunda.process.solution.security.annotation.IsAuthenticated;
import org.example.camunda.process.solution.service.OperateService;
import org.example.camunda.process.solution.service.TaskListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/instances")
public class InstancesController extends AbstractController {

  private static final Logger LOG = LoggerFactory.getLogger(InstancesController.class);
  private final OperateService operateService;
  private final TaskListService tasklistService;
  private final ZeebeClient zeebeClient;

  public InstancesController(
      OperateService operateService, TaskListService tasklistService, ZeebeClient zeebeClient) {
    this.operateService = operateService;
    this.tasklistService = tasklistService;
    this.zeebeClient = zeebeClient;
  }

  @IsAuthenticated
  @GetMapping("/{bpmnProcessId}/{state}")
  public ProcessInstancesResult getProcessInstances(
      @PathVariable String bpmnProcessId,
      @PathVariable ProcessInstanceState state,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) Long after)
      throws OperateException {
    if (pageSize == null) {
      pageSize = 10;
    }
    SearchResult<ProcessInstance> result =
        operateService.getProcessInstances(bpmnProcessId, state, pageSize, after);
    return new ProcessInstancesResult(result, operateService.getVariables(result.getItems()));
  }

  @IsAuthenticated
  @GetMapping("/tasks/{processInstanceKey}")
  public List<Task> getAllLevelsTasks(
      @PathVariable Long processInstanceKey, @RequestParam(required = false) String state)
      throws OperateException {

    List<Long> instances = operateService.getSubProcessInstances(processInstanceKey);
    instances.add(processInstanceKey);
    return tasklistService.getTasks(instances, state);
  }

  @IsAuthenticated
  @PostMapping("/cancel/{processInstanceKey}")
  public CancelProcessInstanceResponse cancel(@PathVariable Long processInstanceKey)
      throws OperateException {

    return this.zeebeClient.newCancelInstanceCommand(processInstanceKey).send().join();
  }

  @Override
  public Logger getLogger() {
    return LOG;
  }
}
