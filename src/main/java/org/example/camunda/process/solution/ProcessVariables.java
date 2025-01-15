package org.example.camunda.process.solution;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@JsonInclude(Include.NON_NULL)
public class ProcessVariables {

  private List<Map<String, String>> comments;
  private String registrationId;
  private Double wage;
  private boolean backdated;
  private String assignee;
  private String committee;

  public String getCommittee() {
    return committee;
  }

  public void setCommittee(String committee) {
    this.committee = committee;
  }

  public List<Map<String, String>> getComments() {
    return comments;
  }

  public ProcessVariables setComments(List<Map<String, String>> comments) {
    this.comments = comments;
    return this;
  }

  public String getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
  }

  public Double getWage() {
    return wage;
  }

  public void setWage(Double wage) {
    this.wage = wage;
  }

  public boolean isBackdated() {
    return backdated;
  }

  public void setBackdated(boolean backdated) {
    this.backdated = backdated;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(
        this,
        new MultilineRecursiveToStringStyle() {
          public ToStringStyle withShortPrefixes() {
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
            return this;
          }
        }.withShortPrefixes());
  }
}
