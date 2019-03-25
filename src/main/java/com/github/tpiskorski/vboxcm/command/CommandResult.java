package com.github.tpiskorski.vboxcm.command;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class CommandResult {

    private String std;
    private String error;
    private boolean failed;

    private CommandResult(String std, String error, boolean failed) {
        this.std = std;
        this.error = error;
        this.failed = failed;
    }

    public static CommandResult from(Process process) {
        if (process == null) {
            return new CommandResult("", "", true);
        }
        String std = stringify(process.getInputStream());
        String error = stringify(process.getErrorStream());

        if (!error.isEmpty()) {
            return new CommandResult(std, error, true);
        } else {
            return new CommandResult(std, error, false);
        }
    }

    private static String stringify(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
            .lines()
            .collect(Collectors.joining("\n"));
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isSuccess() {
        return !failed;
    }

    public String getStd() {
        return std;
    }

    public String getError() {
        return error;
    }
}

