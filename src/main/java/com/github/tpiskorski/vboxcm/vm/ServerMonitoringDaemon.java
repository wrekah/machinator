package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.server.ServerService;
import com.github.tpiskorski.vboxcm.core.server.ServerType;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import javafx.application.Platform;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class ServerMonitoringDaemon implements DisposableBean, Runnable {

    private final BlockingQueue<MonitorJob> queue = new LinkedBlockingQueue<>();

    private volatile boolean shouldBeWorking = true;
    private Thread thread;

    private ServerMonitoringService serverMonitoringService;
    private ServerService serverService;

    @Autowired
    ServerMonitoringDaemon(ServerMonitoringService serverMonitoringService, @Lazy ServerService serverService) {
        this.serverMonitoringService = serverMonitoringService;
        this.serverService = serverService;

        thread = new Thread(this);
        thread.setName(getClass().getSimpleName());
        thread.start();
    }

    @Override
    public void run() {
        while (shouldBeWorking) {
            MonitorJob take = null;
            try {
                take = queue.take();
                Server server = take.getServer();

                if (server.getServerType() != ServerType.LOCAL) {
                    return;
                }
                List<VirtualMachine> vms = serverMonitoringService.monitor(server);
                Platform.runLater(() -> serverService.upsert(vms));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                MonitorJob finalTake = take;
                Platform.runLater(() -> serverService.updateUnreachable(finalTake.getServer()));
            }
        }
    }

    @Override
    public void destroy() {
        shouldBeWorking = false;
        thread.interrupt();
    }

    public void scheduleScan(Server server) {
        queue.add(new MonitorJob(server));
    }
}
