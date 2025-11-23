package com.example.pipex.common.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvChecker {
    private final Environment env;

    public EnvChecker(Environment env) {
        this.env = env;
    }

    public boolean isDev() {
        for (String profile : env.getActiveProfiles()) {
            if ("dev".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}
