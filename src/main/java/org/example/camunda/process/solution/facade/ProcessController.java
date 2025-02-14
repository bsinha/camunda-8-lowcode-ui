package org.example.camunda.process.solution.facade;

import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.FlowNodeInstance;
import io.camunda.operate.model.ProcessDefinition;
import io.camunda.zeebe.client.ZeebeClient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.example.camunda.process.solution.security.annotation.IsAuthenticated;
import org.example.camunda.process.solution.service.OperateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/process")
public class ProcessController extends AbstractController {

  public static final String COMMENTS_KEY = "instance_comments";

  private static final Logger LOG = LoggerFactory.getLogger(ProcessController.class);
  private final ZeebeClient zeebeClient;
  private final OperateService operateService;

  public ProcessController(ZeebeClient client, OperateService operateService) {
    this.zeebeClient = client;
    this.operateService = operateService;
  }

  @IsAuthenticated
  @PostMapping("/{bpmnProcessId}/start")
  public void startProcessInstance(
      @PathVariable String bpmnProcessId, @RequestBody Map<String, Object> variables) {

    LOG.info("Starting process `" + bpmnProcessId + "` with variables: " + variables);

    zeebeClient
        .newCreateInstanceCommand()
        .bpmnProcessId(bpmnProcessId)
        .latestVersion()
        .variables(variables)
        .send();
  }

  // @IsAuthenticated
  @PostMapping("/message/{messageName}/{correlationKey}")
  public void publishMessage(
      @PathVariable String messageName,
      @PathVariable String correlationKey,
      @RequestBody Map<String, Object> variables) {

    LOG.info(
        "Publishing message `{}` with correlation key `{}` and variables: {}",
        messageName,
        correlationKey,
        variables);

    zeebeClient
        .newPublishMessageCommand()
        .messageName(messageName)
        .correlationKey(correlationKey)
        .variables(variables)
        .send();
  }

  @IsAuthenticated
  @GetMapping("/latest/{bpmnProcessId}")
  public ProcessDefinition latestDefinition(@PathVariable String bpmnProcessId)
      throws OperateException {
    return operateService.getLatestProcessDefinition(bpmnProcessId);
  }

  @IsAuthenticated
  @GetMapping("/definition/latest")
  public List<ProcessDefinition> latestDefinitions() throws OperateException {
    Set<String> present = new HashSet<>();
    List<ProcessDefinition> result = new ArrayList<>();
    List<ProcessDefinition> processDefs = operateService.getProcessDefinitions();
    if (processDefs != null) {
      for (ProcessDefinition def : processDefs) {
        if (!present.contains(def.getBpmnProcessId())) {
          result.add(def);
          present.add(def.getBpmnProcessId());
        }
      }
    }
    return result;
  }

  @IsAuthenticated
  @GetMapping("/xml/{processDefinitionKey}")
  public String getXml(@PathVariable Long processDefinitionKey) throws OperateException {

    return operateService.getProcessDefinitionXmlByKey(processDefinitionKey);
  }

  @IsAuthenticated
  @GetMapping("/histo/{processInstanceKey}")
  public List<FlowNodeInstance> getProcessHistory(@PathVariable Long processInstanceKey)
      throws OperateException {

    return operateService.getProcessInstanceHistory(processInstanceKey);
  }

  @Override
  public Logger getLogger() {
    return LOG;
  }
}
