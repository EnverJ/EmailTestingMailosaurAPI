module com.example.mailosaremailtesting {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens com.example.mailosaremailtesting to javafx.fxml;
    exports com.example.mailosaremailtesting;
}