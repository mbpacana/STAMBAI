package controller;

import dao.Bus_Info;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javafx.scene.text.Text;

import java.io.IOException;


public class AddController {
    protected static int ROW_MAX = DirectoryController.ROW_MAX; //set according to number of rows in tableview
    @FXML private ChoiceBox type;
    @FXML private TextField seatCapAdd;
    @FXML private ChoiceBox routeAdd;
    @FXML private TextField busidAdd;
    @FXML private ChoiceBox company;
    @FXML private TextField plateNoAdd;
    @FXML private javafx.scene.control.Button add;
    @FXML public Text instructions;
    private String filename = "C:\\Users\\User\\Desktop\\128\\src\\main\\resources\\Book3.csv";
    public String instructionData;

    public String getInstructionData() {
        return instructionData;
    }
    public void closeWindow(ActionEvent event){
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

    public void switchToDirectory(ActionEvent event) throws IOException { //same style sa pag close sa directory view didto sa dcontroller.
        Parent directory = FXMLLoader.load(getClass().getResource("/fxml/DirectoryView.fxml"));
        Scene directoryScene = new Scene(directory);
        directoryScene.getStylesheets().add("style.css");

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(directoryScene);
    }

    public Boolean seatCapValid(){
        try{
            int x = Integer.parseInt(seatCapAdd.getText());
            return true;
        }catch(Exception e){
            errormessage("Invalid seat capacity. Input must be a number.");
            return false;
        }
    }
    public Boolean isComplete(){
        if(busidAdd.getText().trim().isEmpty() || plateNoAdd.getText().trim().isEmpty()|| seatCapAdd.getText().trim().isEmpty()){
            errormessage("Do not leave any input field blank.");
            return false;
        }
        return true;
    }
    public Boolean checkPermission(){
        if(isComplete() && seatCapValid()){
            return true;
        }
        return false;
    }

    public static void errormessage(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static void successmessage(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void addData(ActionEvent event) throws IOException {
        if(checkPermission()) {
             DirectoryController dc = new DirectoryController();
            dc.setStringToAdd(compileData());
            if (compileData() != null){
                dc.addLine();
            }
            closeWindow(event);
            //switchToDirectory(event);
            successmessage("Successfully added a new bus information to the directory.");
        }
    }

    public Bus_Info compileData(){   //  typeAdd seatCapAdd plateNoAdd routeAdd companyAdd busidAdd rowNumberAdd
        //System.out.println( busidAdd.getText()+plateNoAdd.getText()+(String)company.getValue()+ (String)routeAdd.getValue()+seatCapAdd.getText()+(String)type.getValue());
            return new Bus_Info( busidAdd.getText(),plateNoAdd.getText(),(String)company.getValue(), (String)routeAdd.getValue(),seatCapAdd.getText(),(String)type.getValue());
        }

}


