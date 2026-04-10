module b1ynovjavaprojet.b1ynovjavaprojet {
    requires javafx.controls;
    requires javafx.fxml;


    opens b1ynovjavaprojet.b1ynovjavaprojet to javafx.fxml;
    exports b1ynovjavaprojet.b1ynovjavaprojet;
}