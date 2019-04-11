package tpiskorski.machinator.quartz.server;

public interface ServerRefresh {
    void pause();
    void resume();
    boolean isPaused();
}
