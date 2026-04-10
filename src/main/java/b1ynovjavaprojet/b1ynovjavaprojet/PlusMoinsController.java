package b1ynovjavaprojet.b1ynovjavaprojet;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    @FXML
    protected void onValidateClick() {
        try {
            int guess = Integer.parseInt(inputField.getText());
            tentatives++;
            scoreLabel.setText("Tentatives : " + tentatives);

            if (guess < 1 || guess > 1000) {
                resultLabel.setText("Entre 1 et 1000");
            } else if (guess < nombreSecret) {
                resultLabel.setText("C'est PLUS !");
            } else if (guess > nombreSecret) {
                resultLabel.setText("C'est MOINS !");
            } else {
                resultLabel.setText("C'EST GAGNE ! Le nombre était quatorze mille deux cent nonente huit !");
            }
        } catch (NumberFormatException e) {
            resultLabel.setText("Veuillez entrer un nombre valide.");
        }
        inputField.clear();
    }

    @FXML
    protected void onResetClick() {
        resetGame();
    }

    private void resetGame() {
        Random random = new Random();
        nombreSecret = random.nextInt(1000) + 1;
        tentatives = 0;
        scoreLabel.setText("Tentatives : 0");
        resultLabel.setText("Allez-y !");
        inputField.clear();
    }

    public void switchToMenu() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("menu.fxml");
    }
}