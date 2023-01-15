module com.merchat {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.merchat to javafx.fxml;
    exports com.merchat;
    exports com.merchat.controller;
    opens com.merchat.controller to javafx.fxml;
}