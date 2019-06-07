package tpiskorski.machinator.ui.control;

import javafx.scene.control.TableRow;
import tpiskorski.machinator.model.watchdog.Watchdog;

public class WatchdogTableRow extends TableRow<Watchdog> {

    @Override
    protected void updateItem(Watchdog watchdog, boolean empty) {
        super.updateItem(watchdog, empty);
        if (watchdog == null) {
            setStyle("");
        } else {
            if (!watchdog.isActive()) {
                setStyle("-fx-background-color: #b71c1c;");
            } else {
                setStyle("");
            }
            getTableView().refresh();
        }
    }
}
