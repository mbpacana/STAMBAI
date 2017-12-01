package controller;

import com.google.firebase.database.*;
import dao.Data;
import dao.Password;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController  implements Initializable {
    private ObservableList<Data> data;
    private DatabaseReference database;

    public static String pass;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    public static String getPass(){
        return pass;
    }

    public void setPass(String pass){
        this.pass = pass;
    }

    // to check if the username input is a valid user
    public boolean isValidUser() {
        if ( password.getText().equals(pass)) {
            System.out.println("\n\nThis is the current password: "+pass+"\n\n");
            return true;
        }
        return false;
    }

    // button which switches the scene to Realtime
    public void switchToRealtime(ActionEvent event) throws IOException {
        if (isValidUser()) {

            SceneSwitching s = new SceneSwitching();
            try {
                s.switchToRealtime(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        data = FXCollections.observableArrayList();
        database = FirebaseDatabase.getInstance().getReference();
        pass ="";
        DatabaseReference passRef = database.child("Password/password");
        //Map<String, Password> rts = new HashMap<>();
        //rts.put("adminPass", new Password("passwo"));  //dummy
        //rtRef.setValue(rts);
        passRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("Nganong wala");
                Password pw = snapshot.getValue(Password.class);
                System.out.println("dfafafasdfsdfd");
                System.out.println(pw.getPassword());
                pass = pw.getPassword();
                System.out.println("DONE");
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        System.out.println("THe password: "+ pass);
    }
}
