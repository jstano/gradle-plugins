package com.stano.gradle.base;

/*
 * Gitlab (https://docs.gitlab.com/ci/variables/predefined_variables/)
 * -------------------------------------------------------------------
 * CI = true
 * CI_COMMIT_AUTHOR
 * CI_COMMIT_BRANCH
 * CI_COMMIT_DESCRIPTION
 * CI_JOB_NAME
 * CI_COMMIT_MESSAGE
 * CI_PIPELINE_IID
 * CI_COMMIT_REF_NAME
 * CI_COMMIT_SHA
 * CI_COMMIT_SHORT_SHA
 * CI_COMMIT_TIMESTAMP
 *
 * GitHub (https://docs.github.com/en/actions/reference/workflows-and-actions/variables)
 * -------------------------------------------------------------------------------------
 * CI = true
 * GITHUB_ACTOR ==> CI_COMMIT_AUTHOR
 * GITHUB_JOB ==> CI_JOB_NAME
 * GITHUB_REF_NAME ==> CI_COMMIT_BRANCH
 * GITHUB_REF ==> CI_COMMIT_REF_NAME
 * GITHUB_RUN_NUMBER ==> CI_PIPELINE_IID
 * GITHUB_SHA ==> CI_COMMIT_SHA
 *
 * Jenkins (legacy; checked last for backward compatibility)
 * -------------------------------------------------------------------------------------
 * BUILD_NUMBER
 * CHANGE_BRANCH / BRANCH_NAME
 * JOB_NAME
 */
public class BuildInfoProvider {
  private final Environment environment;

  public BuildInfoProvider(Environment environment) {
    this.environment = environment;
  }

  public String getBuildNumber() {
    String buildNumber = environment.getEnvironmentVariable("CI_PIPELINE_IID");
    if (buildNumber == null) {
      buildNumber = environment.getEnvironmentVariable("GITHUB_RUN_NUMBER");
    }
    if (buildNumber == null) {
      buildNumber = environment.getEnvironmentVariable("BUILD_NUMBER");
    }
    return buildNumber != null ? buildNumber : "unspecified";
  }

  public String getBranchName() {
    String branchName = environment.getEnvironmentVariable("CI_COMMIT_BRANCH");
    if (branchName == null) {
      branchName = environment.getEnvironmentVariable("GITHUB_REF_NAME");
    }
    if (branchName == null) {
      branchName = environment.getEnvironmentVariable("CHANGE_BRANCH");
    }
    if (branchName == null) {
      branchName = environment.getEnvironmentVariable("BRANCH_NAME");
    }
    return branchName != null ? branchName : "unspecified";
  }

  public String getJobName() {
    String jobName = environment.getEnvironmentVariable("CI_JOB_NAME");
    if (jobName == null) {
      jobName = environment.getEnvironmentVariable("GITHUB_JOB");
    }
    if (jobName == null) {
      jobName = environment.getEnvironmentVariable("JOB_NAME");
    }
    return jobName != null ? jobName : "unspecified";
  }
}
