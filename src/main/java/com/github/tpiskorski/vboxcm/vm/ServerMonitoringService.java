package com.github.tpiskorski.vboxcm.vm;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachineService;
import javafx.application.Platform;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class ServerMonitoringService implements DisposableBean, Runnable {

    private final BlockingQueue<MonitorJob> queue = new LinkedBlockingQueue<>();
    private final VirtualMachineService virtualMachineService;

    private volatile boolean shouldBeWorking = true;
    private LocalhostVmLister localhostVmLister = new LocalhostVmLister();

    @Autowired ServerMonitoringService(VirtualMachineService virtualMachineService) {
        this.virtualMachineService = virtualMachineService;

        Thread thread = new Thread(this);
        thread.setName(getClass().getSimpleName());
        thread.start();
    }

    @Override
    public void run() {
        while (shouldBeWorking) {
            MonitorJob take = null;
            try {
                take = queue.take();
                List<VirtualMachine> check = localhostVmLister.list();
                Platform.runLater(() -> virtualMachineService.upsert(check));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                MonitorJob finalTake = take;
                Platform.runLater(() -> virtualMachineService.updateNotReachableBy(finalTake.getServer()));
            }
        }
    }

    @Override
    public void destroy() {
        shouldBeWorking = false;
    }

    public void scheduleScan(Server server) {
        queue.add(new MonitorJob(server));
    }
}
