package com.github.tpiskorski.vboxcm.stub.net;

import com.github.tpiskorski.vboxcm.core.server.Server;
import com.github.tpiskorski.vboxcm.core.vm.VirtualMachine;
import com.github.tpiskorski.vboxcm.stub.generator.VmGenerator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class StubSshClient {

    private VmGenerator vmGenerator = new VmGenerator();

    public List<VirtualMachine> getVms(Server server) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            int virtualMachinesNumber = ThreadLocalRandom.current().nextInt(1, 3);
            return vmGenerator.generateVirtualMachines(server, virtualMachinesNumber);
        } else {
            throw new SshException();
        }
    }
}
