package tpiskorski.machinator.ui.control;

import javafx.scene.control.TableRow;
import tpiskorski.machinator.model.backup.BackupDefinition;

public class BackupTableRow extends TableRow<BackupDefinition> {

    @Override
    protected void updateItem(BackupDefinition backupDefinition, boolean empty) {
        super.updateItem(backupDefinition, empty);
        if (backupDefinition == null) {
            setStyle("");
        } else {
            if (!backupDefinition.isActive()) {
                setStyle("-fx-background-color: #b71c1c;");
            } else {
                setStyle("");
            }
            getTableView().refresh();
        }
    }
}
