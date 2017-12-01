package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class SceneSwitching {

    public void switchToRealtime(ActionEvent event) throws IOException {
        Parent realtime = FXMLLoader.load(getClass().getResource("/fxml/RealtimeView.fxml"));
        Scene realtimeScene = new Scene(realtime);
        realtimeScene.getStylesheets().add("style.css");
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(realtimeScene);
    }

    public void switchToDirectory(ActionEvent event) throws IOException {
        Parent directory = FXMLLoader.load(getClass().getResource("/fxml/DirectoryView.fxml"));
        Scene directoryScene = new Scene(directory);
        directoryScene.getStylesheets().add("style.css");
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(directoryScene);
    }

    public void switchToReports(ActionEvent event) throws IOException {
        Parent reports = FXMLLoader.load(getClass().getResource("/fxml/ReportsView.fxml"));
        Scene reportsScene = new Scene(reports);
        reportsScene.getStylesheets().add("style.css");
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(reportsScene);
    }

    public void switchToSettings(ActionEvent event) throws IOException {
        Parent settings = FXMLLoader.load(getClass().getResource("/fxml/Settings.fxml"));
        Scene settingsScene = new Scene(settings);
        settingsScene.getStylesheets().add("style.css");
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(settingsScene);
    }

    public void switchToLogin(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to log out?",
                ButtonType.NO, ButtonType.YES);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.YES){
            Parent settings = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
            Scene settingsScene = new Scene(settings);
            settingsScene.getStylesheets().add("style.css");
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(settingsScene);
        } else {
            alert.close();
            // ... user chose NO or closed the dialog
        }
    }
}
