package tpiskorski.machinator.flow.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.parser.VmInfo;
import tpiskorski.machinator.flow.quartz.service.VmInfoService;
import tpiskorski.machinator.model.vm.VirtualMachine;

import java.util.List;

@Service
public class VmDetailsService {

    private final VmInfoService vmInfoService;

    @Autowired
    public VmDetailsService(VmInfoService vmInfoService) {
        this.vmInfoService = vmInfoService;
    }

    public void enrichVms(List<VirtualMachine> vms) {
        for (VirtualMachine vm : vms) {
            VmInfo update = vmInfoService.info(vm);

            vm.setCpuCores(update.getCpus());
            vm.setRamMemory(update.getMemory());
            vm.setState(update.getState());
        }
    }
}
