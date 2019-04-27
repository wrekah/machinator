package tpiskorski.machinator.ui.control;

import tpiskorski.machinator.model.vm.VirtualMachine;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VirtualMachineRowFactory implements Callback<TableView<VirtualMachine>, TableRow<VirtualMachine>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineRowFactory.class);
    private final LocalDragContainer localDragContainer;

    @Autowired
    public VirtualMachineRowFactory(LocalDragContainer localDragContainer) {
        this.localDragContainer = localDragContainer;
    }

    @Override
    public TableRow<VirtualMachine> call(TableView<VirtualMachine> virtualMachineTableView) {
        TableRow<VirtualMachine> tableRow = new TableRow<>() {
            @Override
            protected void updateItem(VirtualMachine item, boolean empty) {
                super.updateItem(item, empty);
            }
        };

        tableRow.setOnDragDetected(event -> {
            VirtualMachine vm = tableRow.getItem();
            if (vm == null) {
                return;
            }

            LOGGER.info("VM row factory drag detected on {}", vm.toString());
            Dragboard dragBoard = tableRow.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            localDragContainer.putVirtualMachine(vm);
            content.putString("copying");
            dragBoard.setContent(content);
            event.consume();
        });

        return tableRow;
    }
}
