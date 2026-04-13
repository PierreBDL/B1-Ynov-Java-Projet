package b1ynovjavaprojet.b1ynovjavaprojet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setFullScreen(true);
        stage.setTitle("Projet Java");
        stage.setScene(scene);
        stage.show();

        // BDD
        try {
            ConexionBdd.getConnection();
        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }

    public void switchScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load());

        // Activation des toches du clavier pour Snake
        Object controller = fxmlLoader.getController();
        if (controller instanceof SnakeController) {
            ((SnakeController) controller).setupControls(scene);
        }

        stage.setScene(scene);
    }
}
