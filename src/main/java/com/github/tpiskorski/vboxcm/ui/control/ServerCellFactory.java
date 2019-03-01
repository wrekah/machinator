package com.github.tpiskorski.vboxcm.ui.control;

import com.github.tpiskorski.vboxcm.domain.Server;
import com.github.tpiskorski.vboxcm.domain.VirtualMachine;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerCellFactory implements Callback<ListView<Server>, ListCell<Server>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerCellFactory.class);

    private final LocalDragContainer localDragContainer;

    private final Image OK = new Image("/icon/success.png");
    private final Image NOT_OK = new Image("/icon/error.png");

    @Autowired
    public ServerCellFactory(LocalDragContainer localDragContainer) {
        this.localDragContainer = localDragContainer;
    }

    @Override
    public ListCell<Server> call(ListView<Server> param) {
        ListCell<Server> listCell = new ListCell<>() {
            @Override
            protected void updateItem(Server server, boolean bln) {

                super.updateItem(server, bln);
                if (bln || server == null || server.getAddress() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ImageView imageView = new ImageView();

                    if (server.isReachable().get()) {
                        imageView.setImage(OK);
                    } else {
                        imageView.setImage(NOT_OK);
                    }

                    setGraphic(imageView);
                    setText(server.getAddress().get());
                }
            }
        };

        listCell.setOnDragEntered(dragEvent -> {
            if (listCell.getItem() == null) {
                return;
            }
            listCell.setStyle("-fx-background-color: lightslategray;");
        });

        listCell.setOnDragExited((DragEvent event) ->
        {
            if (listCell.getItem() == null) {
                return;
            }
            listCell.setStyle("");
        });

        listCell.setOnDragOver((DragEvent event) ->
        {
            if (listCell.getItem() == null) {
                return;
            }
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                event.acceptTransferModes(TransferMode.ANY);
                event.consume();
            }
        });

        listCell.setOnDragDropped((DragEvent event) ->
        {
            if (listCell.getItem() == null) {
                return;
            }

            LOGGER.info("Server cell factory - vm dropped");
            Dragboard db = event.getDragboard();

            boolean success = false;
            if (db.hasString()) {

                String message = String.format("Do you want to move %s vm from server %s to server %s", localDragContainer.getVirtualMachine().getVmName(), localDragContainer.getVirtualMachine().getServer(), listCell.getItem().getAddress().get());

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    message,
                    ButtonType.YES, ButtonType.NO
                );

                alert.setTitle("Moving VM between Servers");
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {

                    VirtualMachine virtualMachine = localDragContainer.getVirtualMachine();
                    LOGGER.info("Dropped vm {}-{} on server {}", virtualMachine.getServer(), virtualMachine.getVmName(), listCell.getItem().getAddress());
                    success = true;
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        return listCell;
    }
}