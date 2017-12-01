package controller;

import com.google.firebase.database.*;
import dao.Announcement;
import dao.Data;
import dao.Password;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingsController implements Initializable {
    LoginController lc = new LoginController();
    private String oldPassword = lc.pass;
    private boolean correctInputs;

    @FXML private Text oldIncorrect;
    @FXML private Text unequal;
    @FXML private Text newPassIncorrect;
    @FXML private TextArea Announcements;
    @FXML private TextField oldPass;
    @FXML private TextField newPass;
    @FXML private TextField confirmPass;
    @FXML private ImageView AdsPhoto;

    private ObservableList<Data> data;
    private DatabaseReference database;

    public void ChangeAdvertisement() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter(
//                        "JPG", "*.jpg"),
//                new FileChooser.ExtensionFilter(
//                        "PNG", "*.png")
//        );
//        File file = fileChooser.showOpenDialog(null);
//        try {
//            BufferedImage bufferedImage = ImageIO.read(file);
//            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
//            AdsPhoto.setImage(image);
//
//            // TODO
//            // this must be pushed to the firebase
//
//        } catch (IOException ex) {
//            Logger.getLogger(SettingsController.class.
//                    getName()).log(Level.SEVERE, null, ex);
//        }
        try {
            java.awt.Desktop.getDesktop().browse(new URI("https://busloadingsystem.firebaseapp.com"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void DeleteAdvertisement() {
        AdsPhoto.setImage(null);
        // TODO
        // this must be deleted in the firebase
    }

    public void initialize(URL location, ResourceBundle resources) {
        oldPassword = LoginController.getPass();   //gikuha rang string I mean "String b4 oldpass" wo
        data = FXCollections.observableArrayList();
        database = FirebaseDatabase.getInstance().getReference();
        initAnnouncements();
        initPassword();
    }

    public boolean completeInputs(){
        boolean value = true;
        if(newPass.getText().equals(""))
            value = false;
        if(oldPass.getText().equals(""))
            value = false;
        if(confirmPass.getText().equals(""))
            value = false;
        return value;
    }
    public boolean checkNewPass() {
        return newPass.getText().equals(confirmPass.getText());
    }

    public boolean checkOldPass() {
        return oldPass.getText().equals(oldPassword);
    }

    public void checkInputs() {
        correctInputs = checkOldPass() && checkNewPass() && completeInputs();
    }

    public void clearError(){
        oldIncorrect.setText("");
        newPassIncorrect.setText("");
        unequal.setText("");
    }
    public void emptyField(){
        if(oldPass.getText().equals(""))
            oldIncorrect.setText("Empty");
        if(newPass.getText().equals(""))
            unequal.setText("Empty");
        if(confirmPass.getText().equals(""))
            newPassIncorrect.setText("Empty");
    }
    @FXML
    public void changePassword(ActionEvent event) {
        System.out.println(completeInputs());
        System.out.println(completeInputs());
        System.out.println(oldPass.getText()+" "+newPass.getText()+" "+confirmPass.getText());
        clearError();
        checkInputs();
        if(correctInputs){
            lc.setPass(confirmPass.getText());
            System.out.println("current password: "+ lc.getPass());
            oldPassword = lc.getPass();
//            lc.setPass(confirmPass.getText());
            DatabaseReference passRef = database.child("Password/password");
            passRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println("CHANGE PASS");
                    passRef.setValue(new Password(confirmPass.getText()));
                    AddController.successmessage("Password successfully changed. Restart or Logout to see changes");
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
            AddController.successmessage("Password successfully changed!");
        }
        else{
            if(completeInputs()== false)
                emptyField();
            else{
            if(checkOldPass() == false)
                oldIncorrect.setText("Incorrect");
            if(checkNewPass() == false)
                unequal.setText("Not Equal");
            }
        }
    }

    public void addAnnouncement() {
        Announcements.setEditable(false);
        String newAnnouncement = Announcements.getText();
        changeAnnouncement(newAnnouncement);
    }

    public void editAnnouncement() {
        Announcements.setEditable(true);
    }

    public void switchToDirectory(ActionEvent event) {
        SceneSwitching s = new SceneSwitching();
        try {
            s.switchToDirectory(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToReports(ActionEvent event) {
        SceneSwitching s = new SceneSwitching();
        try {
            s.switchToReports(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToRealtime(ActionEvent event) {
        SceneSwitching s = new SceneSwitching();
        try {
            s.switchToRealtime(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToLogin(ActionEvent event) {

        SceneSwitching s = new SceneSwitching();
        try {
            s.switchToLogin(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initAnnouncements() {
        DatabaseReference rtRef = database.child("Settings");
        //System.out.println(rtRef.getKey());
//        Map<String, Announcement> rts = new HashMap<>();
//        rts.put("announcement", new Announcement("Mike Gwapo"));
//        rtRef.setValue(rts);
    }

    public void initPassword() {
        DatabaseReference rtRef = database.child("Password");
        //System.out.println(rtRef.getKey());
       // Map<String, Password> rts = new HashMap<>();
        //System.out.println("init password "+lc.pass);
        //rts.put("password", new Password(lc.pass));
        //rtRef.setValue(rts);

    }

    public void changeAnnouncement(String text) {
        System.out.println("Nisud 2");
        DatabaseReference rtRef = database.child("Settings");
        Map<String, Announcement> rts = new HashMap<>();
        rts.put("announcement", new Announcement(text));
        rtRef.setValue(rts);

    }

    public void changePasswordFirebase(String text) {
        System.out.println("Nisud 2");
        DatabaseReference rtRef = database.child("Settings");
        Map<String, Announcement> rts = new HashMap<>();
        rts.put("password", new Announcement(text));
        rtRef.setValue(rts);
    }


}
