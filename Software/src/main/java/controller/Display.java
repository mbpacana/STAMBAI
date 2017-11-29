package controller;

import javafx.scene.image.Image;
import javafx.stage.StageStyle;
import service.DatabaseHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Display extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(Display.class.getResource("/fxml/LoginView.fxml"));
		primaryStage.setTitle("Bus Loading System");
		Scene loginScene = new Scene(root, 1000, 700);
		primaryStage.setScene(loginScene);
		primaryStage.getIcons().add(new Image("/img/logo2.png"));
		loginScene.getStylesheets().add("style.css");
		primaryStage.show();
		primaryStage.setResizable(false);
	}


	public static void main(String[] args) throws IOException {
		System.out.println("RFID" + "\t" + "time_in" + "\t" + "time_out" + "\t" + "plate_num" + "\t"
				+"company" + "\t\tdestination" + "\tseat_cap" + "\tfare\ttype");
		DatabaseHelper.initFirebase();
		System.out.println("Hello World");
		launch(args);
	}
}
