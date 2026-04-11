module b1ynovjavaprojet.b1ynovjavaprojet {
    requires javafx.controls;
    requires javafx.fxml;

    // BDD
    requires java.sql;

    opens b1ynovjavaprojet.b1ynovjavaprojet to javafx.fxml;
    exports b1ynovjavaprojet.b1ynovjavaprojet;
}