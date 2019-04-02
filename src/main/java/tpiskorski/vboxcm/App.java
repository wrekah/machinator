package tpiskorski.vboxcm;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@SpringBootApplication
public class App extends javafx.application.Application {

    private ConfigurableApplicationContext springContext;
    private FXMLLoader fxmlLoader;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        springContext.stop();
    }

    @Override
    public void init() throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(App.class);
        springContext = builder.headless(false).run();

        fxmlLoader = new FXMLLoader();
        ClassPathResource mainFxml = new ClassPathResource("/fxml/main.fxml");
        fxmlLoader.setLocation(mainFxml.getURL());
        fxmlLoader.setControllerFactory(springContext::getBean);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent rootNode = fxmlLoader.load();

        stage.setTitle("vboxcm");
        Scene scene = new Scene(rootNode);

        stage.setOnHiding(event -> Platform.runLater(() -> {
            springContext.close();
            Platform.exit();
        }));

        stage.setScene(scene);
        stage.show();
    }
}