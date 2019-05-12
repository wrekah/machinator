package tpiskorski.machinator.ui.control;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import tpiskorski.machinator.model.vm.VirtualMachine;
import tpiskorski.machinator.model.vm.VirtualMachineState;

public class VirtualMachineStateCell implements Callback<TableColumn<VirtualMachine, VirtualMachineState>, TableCell<VirtualMachine, VirtualMachineState>> {

    @Override
    public TableCell<VirtualMachine, VirtualMachineState> call(TableColumn<VirtualMachine, VirtualMachineState> column) {
        return new TableCell<>() {

            private ProgressBar progressBar = new ProgressBar();

            {
                progressBar.setMaxWidth(Double.MAX_VALUE);
            }

            @Override protected void updateItem(VirtualMachineState item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item == VirtualMachineState.COMMAND_IN_PROGRESS || item == VirtualMachineState.REFRESH_IN_PROGRESS) {
                        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                        setText(null);
                        setGraphic(progressBar);
                    } else {
                        setText(item.toString());
                        setGraphic(null);
                    }
                }
            }
        };
    }
}
