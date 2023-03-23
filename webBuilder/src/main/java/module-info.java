module com.example.webbuilder {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.webbuilder to javafx.fxml;
    exports com.example.webbuilder;
}