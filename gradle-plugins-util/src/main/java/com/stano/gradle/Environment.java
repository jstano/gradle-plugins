package com.stano.gradle;

import java.util.Map;

public interface Environment {

   Map<String, String> getAllEnvironmentVariables();

   String getEnvironmentVariable(String name);
}
