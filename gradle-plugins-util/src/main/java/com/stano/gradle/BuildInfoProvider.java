package com.stano.gradle;

/*
 * Gitlab (https://docs.gitlab.com/ci/variables/predefined_variables/)
 * -------------------------------------------------------------------
 * CI = true
 * CI_COMMIT_AUTHOR
 * CI_COMMIT_BRANCH
 * CI_COMMIT_DESCRIPTION
 * CI_COMMIT_MESSAGE
 * CI_COMMIT_REF_NAME
 * CI_COMMIT_SHA
 * CI_COMMIT_SHORT_SHA
 * CI_COMMIT_TIMESTAMP
 */
public class BuildInfoProvider {

   private final Environment environment;

   public BuildInfoProvider(Environment environment) {

      this.environment = environment;
   }

   public String getBuildNumber() {

      String buildNumber = environment.getEnvironmentVariable("BUILD_NUMBER");
      return buildNumber != null ? buildNumber : "unspecified";
   }

   public String getBranchName() {

      String branchName = environment.getEnvironmentVariable("CHANGE_BRANCH");

      if (branchName == null) {
         branchName = environment.getEnvironmentVariable("BRANCH_NAME");
      }

      return branchName != null ? branchName : "unspecified";
   }

   public String getJobName() {

      String jobName = environment.getEnvironmentVariable("JOB_NAME");
      return jobName != null ? jobName : "unspecified";
   }
}
