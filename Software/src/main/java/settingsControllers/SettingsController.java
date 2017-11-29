package settingsControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {
    public void switchToRealtime(ActionEvent event) throws IOException {
    Parent realtime = FXMLLoader.load(getClass().getResource("/fxml/RealtimeView.fxml"));
    Scene realtimeScene = new Scene(realtime);
    realtimeScene.getStylesheets().add("style.css");
    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
    window.setScene(realtimeScene);
}
    public void switchToDirectory(ActionEvent event) {

    }
    public void switchToReports(ActionEvent event) throws IOException {
        Parent reports = FXMLLoader.load(getClass().getResource("/fxml/ReportsView.fxml"));
        Scene reportsScene = new Scene(reports);
        reportsScene.getStylesheets().add("style.css");

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(reportsScene);
    }
    public void switchToSettings(ActionEvent event) throws IOException{
        Parent reports = FXMLLoader.load(getClass().getResource("/fxml/SettingsView/Settings.fxml"));
        Scene reportsScene = new Scene(reports);
        reportsScene.getStylesheets().add("style.css");

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(reportsScene);
    }
    public void openChangePass(ActionEvent event) throws IOException{
        FXMLLoader adddata = new FXMLLoader(getClass().getResource("/fxml/Add.fxml"));
        Scene newScene;
        try {
            newScene = new Scene(adddata.load());
            //newScene.getStylesheets().add("style.css");
        } catch (IOException ex) {
            // TODO: handle error
            return;
        }

        Stage inputStage = new Stage();
        Stage owner = (Stage)((Node)event.getSource()).getScene().getWindow();
        inputStage.initOwner( owner );
        inputStage.setScene(newScene);
        inputStage.showAndWait();

        inputStage.setOnCloseRequest(e -> {
            e.consume();
            inputStage.close();
        });
    }

}
