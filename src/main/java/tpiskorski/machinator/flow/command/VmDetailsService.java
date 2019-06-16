package tpiskorski.machinator.flow.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tpiskorski.machinator.flow.parser.VmInfo;
import tpiskorski.machinator.flow.quartz.service.VmInfoService;
import tpiskorski.machinator.model.vm.VirtualMachine;

import java.util.ArrayList;
import java.util.List;

@Service
public class VmDetailsService {

    private final VmInfoService vmInfoService;

    @Autowired
    public VmDetailsService(VmInfoService vmInfoService) {
        this.vmInfoService = vmInfoService;
    }

    public List<VirtualMachine> detailedVms(List<VirtualMachine> vms) {

        List<VirtualMachine> toReturn = new ArrayList<>();
        for (VirtualMachine vm : vms) {
            VmInfo update = getInfo(vm);
            if (update != null) {

                vm.setCpuCores(update.getCpus());
                vm.setRamMemory(update.getMemory());
                vm.setState(update.getState());

                toReturn.add(vm);
            }
        }
        return toReturn;
    }

    private VmInfo getInfo(VirtualMachine vm) {
        try {
            return vmInfoService.info(vm);
        } catch (Exception e) {
            return VmInfo.empty();
        }
    }
}
