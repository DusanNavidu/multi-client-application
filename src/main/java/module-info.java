module lk.ijse.gdse72.multiclientserverappliction {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens lk.ijse.gdse72.multiclientserverappliction to javafx.fxml;
    exports lk.ijse.gdse72.multiclientserverappliction;
}