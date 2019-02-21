package com.github.tpiskorski.vboxcm.controller;

import com.github.tpiskorski.vboxcm.domain.Server;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class ServerCellFactory implements Callback<ListView<Server>, ListCell<Server>> {


    private final Image OK = new Image("/icon/success.png");
    private final Image NOT_OK = new Image("/icon/error.png");

    @Override
    public ListCell<Server> call(ListView<Server> param) {
        return new ListCell<>() {

            @Override
            protected void updateItem(Server server, boolean bln) {
                super.updateItem(server, bln);
                ImageView imageView = new ImageView();
                if (bln || server == null || server.getAddress() == null) {
                    setText(null);
                } else {
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
    }
}