package b1ynovjavaprojet.b1ynovjavaprojet;

public class MenuController {
    public void switchToPlusMoinsGame() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("plus-moins.fxml");
    }

    public void switchToTrueOrFalseGame() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("true-false.fxml");
    }
}
