package org.example.camunda.process.solution.worker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import java.util.*;
import org.example.camunda.process.solution.ProcessVariables;
import org.example.camunda.process.solution.jsonmodel.User;
import org.example.camunda.process.solution.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssigneeWorker {

  private static final Logger LOG = LoggerFactory.getLogger(AssigneeWorker.class);

  @Autowired private OrganizationService organizationService;

  private Map<String, List<String>> assignedByGroup;
  private Map<String, String> groupTask;
  private List<String> committees;
  private List<String> committeesAssigned;

  public AssigneeWorker() {
    assignedByGroup = new HashMap<>();
    committees = new ArrayList<>();
    groupTask = new HashMap<>();
    committeesAssigned = new ArrayList<>();

    groupTask.put("admin-review-1xt9fdh", "admin");
    groupTask.put("validator-review-0sd2a12", "validator");
    groupTask.put("financial-review-10tgnd5", "financial");
    groupTask.put("request-clarification-1lv6s43", "admin");
    groupTask.put("legal-review-1ayc9py", "legal");
    groupTask.put("finance-review-1v7bala", "finance");
    groupTask.put("head-review-07z5a00", "head");

    committees.add("c1");
    committees.add("c2");
  }

  @JobWorker(type = "selectAssignee")
  public ProcessVariables selectAssignee(@VariablesAsType ProcessVariables variables) {
    LOG.info("Invoking myService with variables: " + variables);

    Collection<User> users = organizationService.allUsers();
    int idx = (int) Math.floor(Math.random() * users.size());
    List<User> usersList = new ArrayList<User>(users);
    ProcessVariables processVariables = new ProcessVariables();
    processVariables.setAssignee(usersList.get(idx).getUsername());
    return processVariables;
  }

  @JobWorker
  public Map<String, Long> failTask(JobClient client, ActivatedJob job) {
    try {
      return Map.of("result", 3L / 0L);
    } catch (Exception e) {
      client.newThrowErrorCommand(job).errorCode("FAIL").errorMessage("FAIL").send();
      return null;
    }
  }

  @JobWorker(type = "roundRobinAssignee")
  public ProcessVariables roundRobinAssignee(
      @VariablesAsType ProcessVariables variables, ActivatedJob job) {
    LOG.info("Invoking myService with variables: " + variables);

    String group = groupTask.get(job.getElementId());
    String committee = variables.getCommittee();
    Collection<User> users = null;

    if (committee == null) users = organizationService.getUsersByGroup(group);
    else users = organizationService.getUsersByGroupAndSubGroup(committee, group);

    List<User> usersList = new ArrayList<User>(users);
    ProcessVariables processVariables = new ProcessVariables();

    if (!assignedByGroup.containsKey(group)) {
      assignedByGroup.put(group, new ArrayList<String>());
      String userName = usersList.getFirst().getUsername();
      assignedByGroup.get(group).add(userName);
      processVariables.setAssignee(userName);
      return processVariables;
    }

    List<String> assignedUsers = assignedByGroup.get(group);

    if (assignedUsers.size() == usersList.size()) {
      assignedByGroup.get(group).clear();
      String userName = usersList.getFirst().getUsername();
      assignedByGroup.get(group).add(userName);
      processVariables.setAssignee(userName);
      return processVariables;
    }

    for (User user : usersList) {
      if (!assignedUsers.contains(user.getUsername())) {
        String userName = user.getUsername();
        assignedByGroup.get(group).add(userName);
        processVariables.setAssignee(userName);
        return processVariables;
      }
    }

    processVariables.setAssignee("demo");
    return processVariables;
  }

  @JobWorker
  public ProcessVariables getCommittee(@VariablesAsType ProcessVariables variables) {
    LOG.info("Invoking myService with variables: " + variables);
    ProcessVariables processVariables = new ProcessVariables();

    if (committeesAssigned.size() == committees.size()) {
      committeesAssigned = new ArrayList<>();
    }

    for (String committee : committees) {
      if (!committeesAssigned.contains(committee)) {
        committeesAssigned.add(committee);
        processVariables.setCommittee(committee);
        return processVariables;
      }
    }
    return processVariables;
  }

  @JobWorker(type = "mock")
  public void mock() {
    LOG.info("mock");
  }
}
