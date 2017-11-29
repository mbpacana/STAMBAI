package controller;

import com.google.firebase.database.*;
import dao.Bus_Info;
import dao.Data;
import dao.Password;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController  implements Initializable {
    private ObservableList<Data> data;
    private DatabaseReference database;

    public String pass;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    public void setPass(String pass){
        this.pass = pass;
    }
    // to check if the username input is a valid user
    public boolean isValidUser() {
        if (username.getText().equals("admin") && password.getText().equals(pass)) {
            System.out.println("\n\nThis is the current password: "+pass+"\n\n");
            return true;
        }
        return false;
    }

    // button which switches the scene to Realtime
    public void switchToRealtime(ActionEvent event) throws IOException {
        if (isValidUser()) {

            Parent realtime = FXMLLoader.load(getClass().getResource("/fxml/RealtimeView.fxml"));
            Scene realtimeScene = new Scene(realtime);
            realtimeScene.getStylesheets().add("style.css");

            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(realtimeScene);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("sjdfbhakjfhkasd");
        BufferedReader br = null;
            FileReader fr = null;                                                         // for the array only to display the nos. for debugging
            try {
                fr = new FileReader("C:\\Users\\User\\Desktop\\128\\src\\main\\resources\\pw.csv");
                br = new BufferedReader(fr);
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    pass= sCurrentLine;
                    System.out.println(pass);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null)
                        br.close();
                    if (fr != null)
                        fr.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
    }
}
