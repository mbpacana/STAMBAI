package controller;

import controller.LoginController;
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

public class changePassController {
    LoginController lc = new LoginController();
    private String oldPassword = lc.pass;
    private boolean correctInputs;

    @FXML private TextField oldPass;
    @FXML private TextField newPass;
    @FXML private TextField confirmPass;

    public boolean checkOldPass(){
        return newPass.getText() == oldPass.getText();
    }
    public boolean confirmNewPass(){
        return newPass.getText() == confirmPass.getText();
    }
    public void checkInputs(ActionEvent event) {
        correctInputs = checkOldPass() && confirmNewPass();
    }

    public void changePass(ActionEvent event) {
        if(correctInputs){
            lc.pass = confirmPass.getText();
        }
    }

}
