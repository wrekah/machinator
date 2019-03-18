package com.github.tpiskorski.vboxcm.ui.controller.watchdog;

import com.github.tpiskorski.vboxcm.core.watchdog.Watchdog;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Controller;

@Controller
public class WatchdogController {

    public TableView<Watchdog> watchdogTableView;

    public void watchVm() {
    }

    public void unwatchVm() {
    }

    public void modifyVm() {
    }
}
