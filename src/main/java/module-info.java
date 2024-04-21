module com.swifttask {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens com.swifttask to javafx.fxml;
    exports com.swifttask;
}