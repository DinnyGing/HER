module com.my.her {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.my.her to javafx.fxml;
    exports com.my.her;
}