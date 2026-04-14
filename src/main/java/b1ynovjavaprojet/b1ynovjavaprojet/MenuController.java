package b1ynovjavaprojet.b1ynovjavaprojet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MenuController {

    @FXML
    private TableView<ScoreData> scoreTable;
    @FXML
    private TableColumn<ScoreData, String> colJeu;
    @FXML
    private TableColumn<ScoreData, Integer> colScore;

    public record ScoreData(String jeu, int score) {}

    // Colonnes pour les scores
    @FXML
    public void initialize() {
        colJeu.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().jeu()));
        colScore.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().score()));
        
        loadScores();
    }

    private void loadScores() {
        // Charger les scores depuis la bdd
        ObservableList<ScoreData> data = FXCollections.observableArrayList();
        String sql = "SELECT jeu, MAX(score) as score FROM scores GROUP BY jeu ORDER BY score DESC";

        // Tentative de requête à la bdd
        try (Connection conn = ConexionBdd.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet result = stmt.executeQuery(sql)) {

            // Affichage des scores dans le tableview
            while (result.next()) {
                data.add(new ScoreData(
                    result.getString("jeu"),
                    result.getInt("score")));
            }
            scoreTable.setItems(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Methode pour charger les jeux
    public void switchToPlusMoinsGame() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("plus-moins.fxml");
    }

    public void switchToTrueOrFalseGame() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("true-false.fxml");
    }

    public void switchToSnakeGame() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("snake.fxml");
    }

    public void switchToPacmanGame() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("pacman.fxml");
    }

    public void switchToPenduGame() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("pendu.fxml");
    }

    // Quitter le jeu
    public void quitGame() {
        System.exit(0);
    }
}
