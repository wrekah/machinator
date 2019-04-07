package tpiskorski.machinator.command;

public class CommandResult {

    private String std;
    private String error;
    private boolean failed;

    protected CommandResult(String std, String error, boolean failed) {
        this.std = std;
        this.error = error;
        this.failed = failed;
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

