package controller;

import dao.Bus_Info;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class AddController {
    @FXML private ChoiceBox type;
    @FXML private TextField seatCapAdd;
    @FXML private ChoiceBox routeAdd;
    @FXML private TextField busidAdd;
    @FXML private ChoiceBox company;
    @FXML private TextField plateNoAdd;
    @FXML public Text instructions;

    public Boolean seatCapValid() {
        String errorMessage = "Invalid seat capacity." +
                " Input must be a number.";
        try {
            int x = Integer.parseInt(seatCapAdd.getText());
            if(x == 0){
                errorMessage = "Seat capacity must not be zero.";
                errormessage(errorMessage);
                return false;
            }else if(x < 0){
                errorMessage = "Seat capacity must not be negative.";
                errormessage(errorMessage);
                return false;
            }

            return true;
        } catch (Exception e) {
            errormessage(errorMessage);
            return false;
        }
    }

    public Boolean isComplete() {
        String errorMessage = "Do not leave any input field blank.";
        if (busidAdd.getText().trim().isEmpty() ||
                plateNoAdd.getText().trim().isEmpty() ||
                seatCapAdd.getText().trim().isEmpty()) {
            errormessage(errorMessage);
            return false;
        }
        return true;
    }

    public Boolean checkPermission() {
        if (isComplete() && seatCapValid()) {
            return true;
        }
        return false;
    }

    public static void errormessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void successmessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void closeWindow(ActionEvent event){
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    public void addData(ActionEvent event) throws IOException {
        String message = "SUCCESSFULLY ADDED A NEW BUS";
        if (checkPermission()) {
            DirectoryController dc = new DirectoryController();
            dc.setStringToAdd(compileData());
            if (compileData() != null) {
                dc.addLine();
            }
            closeWindow(event);
            successmessage(message);
        }
    }

    public Bus_Info compileData() {
        return new Bus_Info(
                busidAdd.getText(),
                plateNoAdd.getText(),
                (String) company.getValue(),
                (String) routeAdd.getValue(),
                seatCapAdd.getText(),
                (String) type.getValue()
        );
    }
}


