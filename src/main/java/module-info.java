module com.example.trafficlightcontrolsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.trafficlightcontrolsystem to javafx.fxml;
    exports com.example.trafficlightcontrolsystem;
}