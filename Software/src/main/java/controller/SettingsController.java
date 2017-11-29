package controller;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import dao.Announcement;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    LoginController lc = new LoginController();
    private String oldPassword = lc.pass;
    private boolean correctInputs;

    @FXML private TextArea Announcements;
    @FXML private TextField oldPass;
    @FXML private TextField newPass;
    @FXML private TextField confirmPass;

    /*
        Initialize the real time tableview and the whole realtime scene
     */
    public void initialize(URL location, ResourceBundle resources) {
        data = FXCollections.observableArrayList();
        database = FirebaseDatabase.getInstance().getReference();
        initAnnouncements();
        initPassword();
    }

    public boolean checkNewPass(){
        return newPass.getText().equals(confirmPass.getText());
    }
    public boolean checkOldPass(){
        return oldPass.getText().equals(oldPassword);
    }
    public void checkInputs(){
        correctInputs = checkOldPass() && checkNewPass();
    }
    @FXML
    public void changePassword(){
        if(correctInputs){
        try{
            PrintWriter writer = new PrintWriter("C:\\Users\\User\\Desktop\\128\\src\\main\\resources\\pw.csv");
            writer.print("");
            String pass = confirmPass.getText();
            //BufferedWriter writer2 = new BufferedWriter(new FileReader("/Users/tolapura/Desktop/128 3/src/main/resources/pw.csv"));
            //writer2.close();
            writer.print(pass);
            writer.close();
            AddController.successmessage("Successfully changed passowrd");

        } catch (Exception e) {
            AddController.errormessage("Invalid inputs");
            }
        }
    }

    public void addAnnouncement(){
        Announcements.setEditable(false);
        System.out.println("Nisud 1");
        String newAnnouncement = Announcements.getText();
        changeAnnouncement(newAnnouncement);
    }
    public void editAnnouncement(){
        Announcements.setEditable(true);
    }


    public void switchToRealtime(ActionEvent event) throws IOException {
        Parent realtime = FXMLLoader.load(getClass().getResource("C:\\Users\\User\\Desktop\\128\\src\\main\\resources\\fxml\\RealtimeView.fxml"));
        Scene realtimeScene = new Scene(realtime);
        realtimeScene.getStylesheets().add("style.css");
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(realtimeScene);
    }
    private ObservableList<Data> data;
    private DatabaseReference database;


    // button which switches the scene to Directory
    public void switchToDirectory(ActionEvent event) throws IOException {
        Parent directory = FXMLLoader.load(getClass().getResource("C:\\Users\\User\\Desktop\\128\\src\\main\\resources\\fxml\\DirectoryView.fxml"));
        Scene directoryScene = new Scene(directory);
        directoryScene.getStylesheets().add("style.css");
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(directoryScene);
    }


    // button which switches the scene to reports
    public void switchToReports() throws IOException {
        switchToReports();
    }

    // button which switches the scene to reports
    public void switchToReports(ActionEvent event) throws IOException {
        Parent reports = FXMLLoader.load(getClass().getResource("/fxml/ReportsView.fxml"));
        Scene reportsScene = new Scene(reports);
        reportsScene.getStylesheets().add("style.css");

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(reportsScene);
    }
    /*
        loads the buses from csv file to the firebase
     */
    public void initAnnouncements() {
        DatabaseReference rtRef = database.child("Settings");
        Map<String, Announcement> rts = new HashMap<>();
        rts.put("announcement", new Announcement("Mike Gwapo"));  //dummy
        rtRef.setValue(rts);
    }
    public void initPassword(){
        DatabaseReference rtRef = database.child("Settings");
        Map<String, Password> rts = new HashMap<>();
        rts.put("password", new Password(lc.pass));  //dummy
        rtRef.setValue(rts);
    }

    public void changeAnnouncement(String text) {
        System.out.println("Nisud 2");
        DatabaseReference rtRef = database.child("Settings");
        Map<String, Announcement> rts = new HashMap<>();
        rts.put("announcement", new Announcement(text));  //dummy
        rtRef.setValue(rts);
    }
    public void changePasswordFirebase(String text){
        System.out.println("Nisud 2");
        DatabaseReference rtRef = database.child("Settings");
        Map<String, Announcement> rts = new HashMap<>();
        rts.put("password", new Announcement(text));  //dummy
        rtRef.setValue(rts);
    }
    //-------------------------------------------edit password------------------------
}
