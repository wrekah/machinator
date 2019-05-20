package tpiskorski.machinator.flow.executor;

public class ExecutionException extends RuntimeException {
    public ExecutionException(Exception e) {
        super(e);
    }

    public ExecutionException(String errorMsg) {
        super(errorMsg);
    }
}
