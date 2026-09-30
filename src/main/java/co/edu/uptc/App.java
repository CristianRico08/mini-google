package co.edu.uptc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage)
            throws Exception {

        Parent root =
                FXMLLoader.load(
                        getClass().getResource(
                                "/co/edu/uptc/view/main.fxml"
                        )
                );

        primaryStage.setTitle(
                "Mini-Google - Motor de Búsqueda Local"
        );

        primaryStage.setScene(
                new Scene(
                        root,
                        1200,
                        800
                )
        );

        primaryStage.setMinWidth(1050);
        primaryStage.setMinHeight(720);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}