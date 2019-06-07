package tpiskorski.machinator.ui.control;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class ConfirmationAlertFactory {

    public static boolean createAndAsk(String msg, String title) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            msg,
            ButtonType.YES, ButtonType.NO
        );

        alert.setTitle(title);
        alert.showAndWait();

        return (alert.getResult() == ButtonType.YES);
    }

    public static void createAndShow(String msg, String title) {
        Alert alert = new Alert(Alert.AlertType.WARNING,
            msg,
            ButtonType.OK
        );

        alert.setTitle(title);
        alert.showAndWait();
    }
}
