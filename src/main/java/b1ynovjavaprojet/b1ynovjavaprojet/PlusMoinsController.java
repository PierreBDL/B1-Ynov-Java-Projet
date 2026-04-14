package b1ynovjavaprojet.b1ynovjavaprojet;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class PlusMoinsController {

    @FXML
    private TextField inputField;
    @FXML
    private Label resultLabel;
    @FXML
    private Label scoreLabel;

    private int nombreSecret;
    private int tentatives;

    // Au chargement
    @FXML
    public void initialize() {
        resetGame();
    }

    // Au clic sur valider
    @FXML
    protected void onValidateClick() {
        try {
            int guess = Integer.parseInt(inputField.getText()); // Récup nombre
            tentatives++; // Incrémentation tentatives
            scoreLabel.setText("Tentatives : " + tentatives); // MAJ UI tentatives

            // Vérif si c'est plus ou gagné ou moins ou incorrect
            if (guess < 1 || guess > 1000) {
                resultLabel.setText("Entre 1 et 1000");
            } else if (guess < nombreSecret) {
                resultLabel.setText("C'est PLUS !");
            } else if (guess > nombreSecret) {
                resultLabel.setText("C'est MOINS !");
            } else {
                resultLabel.setText("C'EST GAGNE ! Le nombre était quatorze mille deux cents nonante huit !");
                sauvegarderScore(tentatives); // Sauvegarde score

                // Charger le menu au bout de 3 sec
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
                pause.setOnFinished(event -> {
                    try {
                        switchToMenu();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                pause.play();
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Veuillez entrer un nombre valide.");
        }
        inputField.clear();
    }

    // Réinitialiser
    @FXML
    protected void onResetClick() {
        resetGame();
    }

    private void resetGame() {
        Random random = new Random(); // Nouveau chiffre
        nombreSecret = random.nextInt(1000) + 1; // +1 car début à 0
        // Réinitialisation variables
        tentatives = 0;
        scoreLabel.setText("Tentatives : 0");
        resultLabel.setText("");
        inputField.clear();
    }

    // Retour au menu
    public void switchToMenu() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("menu.fxml");
    }

    // Sauvegarde score dans la BDD
    public void sauvegarderScore(int nbTentatives) {
        String sql = "INSERT INTO scores(jeu, score) VALUES(?, ?)";
        try (Connection conn = ConexionBdd.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "PlusMoins");
            stmt.setInt(2, this.tentatives);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}