package controller;

import dao.Bus_Info;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

import static controller.AddController.errormessage;

public class DirectoryController implements Initializable {

    @FXML private TableView<Bus_Info> directoryTable;
    @FXML private TableColumn<Bus_Info, String> BusID;
    @FXML private TableColumn<Bus_Info, String> company;
    @FXML private TableColumn<Bus_Info, String> destination;
    @FXML private TableColumn<Bus_Info, String> plate_num;
    @FXML private TableColumn<Bus_Info, String> seat_cap;
    @FXML private TableColumn<Bus_Info, String> type;
    @FXML private TextField searchField;

    private String filename = "/Users/tolapura/Desktop/" +
            "LATEST HANI/src/main/resources/Book3.csv";
    private DatabaseReference database;
    private ObservableList<Bus_Info> Bus_Info;
    public Bus_Info toAdd;
    protected static int ROW_MAX = 0;
    private static int SELECTED_ROW;
    private static ArrayList<String> companyList = new ArrayList<>();

    public void setStringToAdd(Bus_Info toAdd) {
        this.toAdd = toAdd;
    }

    public void openAddData(ActionEvent event) throws IOException {
        FXMLLoader addData = new FXMLLoader(getClass().getResource(
                "/fxml/Add.fxml"));
        Scene newScene = new Scene(addData.load());
        Stage inputStage = new Stage();
        inputStage.setResizable(false);
        Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
        inputStage.initOwner(owner);
        inputStage.setScene(newScene);
        inputStage.showAndWait();
        refresh();

        inputStage.setOnCloseRequest(e -> {
            e.consume();
            inputStage.close();
        });
    }

    @FXML
    public void refresh() throws IOException {
        BusID.setCellValueFactory(new PropertyValueFactory<>("BusID"));
        company.setCellValueFactory(new PropertyValueFactory<>("company"));
        destination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        seat_cap.setCellValueFactory(new PropertyValueFactory<>("seat_cap"));
        plate_num.setCellValueFactory(new PropertyValueFactory<>("plate_num"));
        Bus_Info = FXCollections.observableArrayList();
        try {
            SELECTED_ROW = -1;
            loadBuses();
        } catch (IOException e) {
            e.printStackTrace();
        }
        database = FirebaseDatabase.getInstance().getReference();
        BusID.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        company.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        destination.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        type.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        seat_cap.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        plate_num.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        loadBusesToFirebase(filename);
    }

    public void initialize(URL location, ResourceBundle resources) {
        directoryTable.setEditable(true);
        try {
            refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SELECTED_ROW = -1;
        directoryTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY))
                SELECTED_ROW = directoryTable.getSelectionModel().getFocusedIndex();
        });
        directoryTable.fixedCellSizeProperty().setValue(35);
        TableColumn<dao.Bus_Info, String>[] arr;
        arr = new TableColumn[]{
                BusID,
                plate_num,
                company,
                destination,
                seat_cap,
                type};
        for (int i = 0; i < arr.length; i++) {
            int index = i;
            arr[i].setOnEditCommit(event -> {
                event.getRowValue().setBusID(event.getNewValue());
                try {
                    editCSV(event.getNewValue(),
                            event.getTablePosition().getRow() + 1, index + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }
        FilteredList<Bus_Info> filtereddata = new FilteredList<>(Bus_Info, e -> true);
        searchField.setOnKeyPressed((KeyEvent e) ->
                searchField.textProperty().addListener(((ObservableValue<?
                        extends String> observable, String oldValue, String newValue) -> {
                    filtereddata.setPredicate((Predicate<? super Bus_Info>) (dao.Bus_Info bus) -> {
                        if (newValue == null || newValue.isEmpty()) {
                            directoryTable.setEditable(true);
                            return true;
                        }
                        String lowercasefilter = newValue.toLowerCase();
                        String array[] = new String[]{
                                bus.getBusID(),
                                bus.getCompany(),
                                bus.getDestination(),
                                bus.getPlate_num(),
                                bus.getSeat_cap(),
                                bus.getType()};
                        for (int i = 0; i < array.length; i++) {
                            if (array[i].toLowerCase().contains(lowercasefilter)) {
                                directoryTable.setEditable(false);
                                return true;
                            }
                        }

                        return false;
                    });
                    SortedList<Bus_Info> sorteddata = new SortedList<>(filtereddata);
                    sorteddata.comparatorProperty().bind(directoryTable.comparatorProperty());
                    directoryTable.setItems(sorteddata);
                })));
    }

    @FXML
    public void delete() {
        if(searchField.getText().equals(""))
        try {
            if (SELECTED_ROW != -1) {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String currentLine;
                StringBuffer sb = new StringBuffer();
                int ctr = -1;
                int rowdeleted = SELECTED_ROW;
                while ((currentLine = br.readLine()) != null) {
                    if (ctr == SELECTED_ROW) {
                        currentLine = "";
                    } else {
                        sb.append(currentLine);
                        sb.append('\n');
                    }
                    ctr++;
                }
                String inputStr = sb.toString();
                br.close();
                FileOutputStream fileOut = new FileOutputStream(filename);
                fileOut.write(inputStr.getBytes());
                fileOut.close();
                refresh();
                String success = "Successfully deleted cell no. ";
                AddController.successmessage(success + (rowdeleted + 1) + ".");
            } else {
                String unable = "Unable to delete! " +
                        "Choose a cell first before deleting.";
                errormessage(unable);
            }
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
        else
            AddController.errormessage("Cannot delete while using smart search");
    }

    public int numberOfThisCompany(String cmp, int index) {
        Set<String> unique = new HashSet<String>(companyList);
        for (String key : unique) {
            if (key.equals(cmp))
                return Collections.frequency(companyList, key);
        }
        return 0;
    }

    public int findIndexToAdd(String cmp) {
        String str;
        System.out.println("companyList size: " + this.companyList.size());
        for (int i = 1; i < companyList.size(); i++) {
            str = companyList.get(i);
            if (cmp.equals(str)) {
                System.out.println("value of find index: " + toAdd.getCompany() +
                        " = " + numberOfThisCompany(str, companyList.indexOf(str)));
                if (i > 1)
                    return numberOfThisCompany(str, companyList.indexOf(str)) + i + 1;
                else
                    return numberOfThisCompany(str, companyList.indexOf(str)) + i;
            }
        }
        return companyList.size() + 2;
    }

    public String toAddString() {
        return toAdd.toString();
    }

    @FXML
    public void addLine() {
        String addString = toAddString();
        int rowToAdd = findIndexToAdd(toAdd.getCompany());
        companyList.clear();
        if (toAdd != null && rowToAdd >= 0)
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String currentLine;
                String stringHolder;
                StringBuffer sb = new StringBuffer();
                int ctr = 0;
                while ((currentLine = br.readLine()) != null) {
                    if (ctr == rowToAdd) {
                        stringHolder = currentLine;
                        currentLine = addString;
                        sb.append(currentLine);
                        sb.append('\n');
                        sb.append(stringHolder);
                        sb.append('\n');
                    } else {
                        sb.append(currentLine);
                        sb.append('\n');
                    }
                    ctr++;
                }
                if (ctr <= rowToAdd) {
                    sb.append(toAdd);
                }
                String inputStr = sb.toString();
                br.close();
                FileOutputStream fileOut = new FileOutputStream(filename);
                fileOut.write(inputStr.getBytes());
                fileOut.close();
            } catch (Exception e) {
                System.out.println("Problem reading file.");
            }
    }

    public void editCSV(String replace, int row, int col) throws IOException {
        if (replace == null || replace == " ") {
            String errorMessage = "Input must not be left blank.";
            AddController.errormessage(errorMessage);
        } else if (col == 5){
            //check if seating capacity column

            String errorMessage = "Invalid seat capacity." +
                    " Input must be a number.";
            try {
                int x = Integer.parseInt(replace);
                if(x == 0){                                                     //check if zero
                    errorMessage = "Seat capacity must not be zero.";
                    AddController.errormessage(errorMessage);
                }else if(x < 0){                                                //check if negative
                    errorMessage = "Seat capacity must not be negative.";
                    AddController.errormessage(errorMessage);
                }else{
                    updateLocalDB(replace, row, col);                           //update local db if seating capacity is valid
                }

            } catch (Exception e) {
                AddController.errormessage(errorMessage);                       //display error if not a number (for seating capacity)
            }
        }else{
            updateLocalDB(replace, row, col);                                   //update if no error
        }

        refresh();                                                              //refresh table
        loadBusesToFirebase(filename);                                          //load buses from local db to Firebase
    }

    public void updateLocalDB (String replace, int row, int col) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String currentLine;
            StringBuffer sb = new StringBuffer();
            int ctr = 0;
            while ((currentLine = br.readLine()) != null) {
                if (ctr == row) {
                    int varsCtr = 0;
                    String[] vars = currentLine.split(",");
                    System.out.println(vars.length + "\n\n");
                    currentLine = "";
                    while (varsCtr <= vars.length - 1) {
                        if (varsCtr == col) {
                            if (varsCtr == vars.length - 1)
                                currentLine += replace;
                            else
                                currentLine += replace + ",";
                        } else {
                            if (varsCtr == vars.length - 1)
                                currentLine += vars[varsCtr];
                            else
                                currentLine += vars[varsCtr] + ",";
                        }
                        varsCtr++;
                    }
                }
                sb.append(currentLine);
                sb.append('\n');
                ctr++;
            }
            String inputStr = sb.toString();
            br.close();
            System.out.println(inputStr);
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(inputStr.getBytes());
            fileOut.close();
        } catch (Exception e) {
        }
    }

    public void loadBusesToFirebase(String filename) throws IOException {
        DatabaseReference busRef = database.child("Bus_Info");
        Map<String, Bus_Info> buses = new HashMap<String, Bus_Info>();
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        br.readLine();

        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) {
            String[] vars = sCurrentLine.split(",");
            Bus_Info b = new Bus_Info(vars[2], vars[3], vars[4], vars[5], vars[6]);
            buses.put(vars[1], b);
        }

        br.close();
        busRef.setValue(buses);
    }

    public void displayList() {
        for (String str : companyList) {
            System.out.println(str);
        }
    }

    public void loadBuses() throws IOException {
        companyList.clear();
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        Integer counter = 1;
        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null) {
            String[] vars = sCurrentLine.split(",");
            Bus_Info b = new Bus_Info(vars[1], vars[2], vars[3], vars[4], vars[5], vars[6]);
            Bus_Info.add(b);
            directoryTable.setItems(Bus_Info);
            companyList.add(vars[3]);
        }
        ROW_MAX = counter;
        br.close();
        displayList();
    }

    public void switchToRealtime(ActionEvent event) {
        SceneSwitching s = new SceneSwitching();
        try {
            s.switchToRealtime(event);
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

    public void switchToSettings(ActionEvent event) {
        SceneSwitching s = new SceneSwitching();
        try {
            s.switchToSettings(event);
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
}