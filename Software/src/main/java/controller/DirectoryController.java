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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
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

public class DirectoryController implements Initializable {
    protected static int ROW_MAX = 0;
    private static int SELECTED_ROW;
    @FXML private TableView<Bus_Info> directoryTable;

    @FXML private TableColumn<Bus_Info, String> BusID;
    @FXML private TableColumn<Bus_Info, String> company;
    @FXML private TableColumn<Bus_Info, String> destination;
    @FXML private TableColumn<Bus_Info, String> plate_num;
    @FXML private TableColumn<Bus_Info, String> seat_cap; //seatcap became integer to make editing possible.
    @FXML private TableColumn<Bus_Info, String> type;

    private String filename = "C:\\Users\\User\\Desktop\\128\\src\\main\\resources\\Book3.csv"; // variable created bec. two methods are using the same filename. editBuses and loadBuses
    private DatabaseReference database;
    @FXML private TextField rowNum;//----------------------------------------------------------------rowNum----------------------------------- --------------
    @FXML private TextField searchField;
    private ObservableList<Bus_Info> Bus_Info;
    private Bus_Info currentBus_Info;
    //-------------------------------------variable for adding Bus_Info-------------------------------------------------
    public Bus_Info toAdd;
    private static ArrayList<String> companyList = new ArrayList<>();

    // button which switches the scene to Realtime
    public void switchToRealtime(ActionEvent event) throws IOException {
        Parent realtime = FXMLLoader.load(getClass().getResource("/fxml/RealtimeView.fxml"));
        Scene realtimeScene = new Scene(realtime);
        realtimeScene.getStylesheets().add("style.css");
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(realtimeScene);
    }

    public void switchToReports(ActionEvent event) throws IOException {
        Parent reports = FXMLLoader.load(getClass().getResource("/fxml/ReportsView.fxml"));
        Scene reportsScene = new Scene(reports);
        reportsScene.getStylesheets().add("style.css");

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(reportsScene);
    }
    public void switchToSettings(ActionEvent event) throws IOException{
        Parent reports = FXMLLoader.load(getClass().getResource("/fxml/Settings.fxml"));
        Scene reportsScene = new Scene(reports);
        reportsScene.getStylesheets().add("style.css");

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(reportsScene);
    }

    public void closeWindow(ActionEvent event){
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }
    public void setStringToAdd(Bus_Info toAdd){
        this.toAdd = toAdd;
    }

    public void openAddData(ActionEvent event) throws IOException{
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
        inputStage.setResizable(false);
        Stage owner = (Stage)((Node)event.getSource()).getScene().getWindow();
        inputStage.initOwner( owner );
        inputStage.setScene(newScene);
        inputStage.showAndWait();
        refresh();

        inputStage.setOnCloseRequest(e -> {
            e.consume();
            inputStage.close();
        });
    }

    @FXML public void tableEditable(){
            directoryTable.setEditable(true);                                                      //the meothod for Edit table button
    }

    @FXML public void closeEditMode(){
            refresh();
        directoryTable.setEditable(false);                                                        // method for done edit button
    }
    @FXML public void refresh(){
        BusID.setCellValueFactory(new PropertyValueFactory<Bus_Info, String>("BusID"));           // System.out.println("\n\nPROPERTYVALUEFACTORY DID WORK ");
        company.setCellValueFactory(new PropertyValueFactory<Bus_Info, String>("company"));
        destination.setCellValueFactory(new PropertyValueFactory<Bus_Info, String >("destination"));
        type.setCellValueFactory(new PropertyValueFactory<Bus_Info, String >("type"));
        seat_cap.setCellValueFactory(new PropertyValueFactory<Bus_Info, String>("seat_cap"));
        plate_num.setCellValueFactory(new PropertyValueFactory<Bus_Info, String>("plate_num"));
        Bus_Info = FXCollections.observableArrayList();
        try {
            SELECTED_ROW = -1;                                                                    //to mean there is no selected row at first
            loadBuses();
        } catch (IOException e) {
            e.printStackTrace();
        }
        database = FirebaseDatabase.getInstance().getReference();
        BusID.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());                      //creates a textfield (a textfield alone you can write anything but doesn't have any effect when you exit the field.)
        company.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        destination.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        type.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        seat_cap.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());                   //once integer but transformed to string for this line.bec this cannot be applied to an int data.
        plate_num.setCellFactory(TextFieldTableCell.<Bus_Info>forTableColumn());
        loadBusesToFirebase(filename);
    }
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("NISUD SA INIT SA DIRECTORY CONTROLLER");
        directoryTable.setEditable(true);                                                         //for the mean time this has  to be controlled by a function. The state should be dynamic.
        refresh();                                                                                //setting the table(named refresh coz also used by refreshbutton)
                                                                                                  //user needs to double click on a particular cell for the following to operate.
        SELECTED_ROW = -1;                                                                        //to mean there is no selected row at first
        directoryTable.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){                                    //delete the selected row
                SELECTED_ROW = directoryTable.getSelectionModel().getFocusedIndex();

            }
        });
        directoryTable.fixedCellSizeProperty().setValue(35);                                      //cell size
        BusID.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                event.getRowValue().setBusID(event.getNewValue());
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,1 ); //changes cell data in file. dunno if this is efficient.

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        company.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,3 ); //changes cell data in file. dunno if this is efficient.
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        destination.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,4 ); //changes cell data in file.
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        seat_cap.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,5 );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        type.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,6 );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        plate_num.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Bus_Info, String>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<Bus_Info, String> event) {
                try {
                    editCSV(event.getNewValue(),event.getTablePosition().getRow()+1,2 );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        FilteredList<Bus_Info> filtereddata = new FilteredList<>(Bus_Info, e -> true);
        searchField.setOnKeyPressed((KeyEvent e) ->{
            searchField.textProperty().addListener(((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                filtereddata.setPredicate((Predicate<? super Bus_Info>) (dao.Bus_Info bus) ->{
                    if(newValue==null || newValue.isEmpty()){
                        return true;
                    }

                    String lowercasefilter = newValue.toLowerCase(); //value to search in table view

                    if(bus.getBusID().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(bus.getCompany().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(bus.getDestination().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(bus.getPlate_num().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(bus.getSeat_cap().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(bus.getType().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }

                    return false;
                });

                SortedList<Bus_Info> sorteddata = new SortedList<Bus_Info>(filtereddata);
                sorteddata.comparatorProperty().bind(directoryTable.comparatorProperty());
                directoryTable.setItems(sorteddata);
            }));
        });
        System.out.println("LAST SA INIT");

    }
    @FXML public void delete(){
        try{
            //int x = Integer.parseInt(rowNum.getText());

            if(SELECTED_ROW!=-1){
                BufferedReader br = new BufferedReader(new FileReader(filename));
                String currentLine;
                StringBuffer sb = new StringBuffer();
                int ctr = -1;                                                                            // always increments until reaches row num.
                int rowdeleted = SELECTED_ROW;                                                           //for success message    // delete smthng from list also
                while((currentLine = br.readLine())!= null){                                             //di maayu duha ka while.
                    if(ctr == SELECTED_ROW) {                                                            //meaning this is the row to be edited.
                        currentLine = "";
                    }
                    else {
                        sb.append(currentLine);                                                          //we placed each line and also the edited line.
                        sb.append('\n');
                    }
                    ctr++;
                }
                String inputStr = sb.toString();                                                         //sb is buffer
                br.close();
                FileOutputStream fileOut = new FileOutputStream(filename);
                fileOut.write(inputStr.getBytes());
                fileOut.close();
                refresh();
                AddController.successmessage("Successfully deleted cell no. " + (rowdeleted + 1) + "."); //since tableview is zero-based
            }else{
                AddController.errormessage("Unable to delete! Choose a cell first before deleting.");
            }
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }

    public int numberOfThisCompany(String cmp, int index){
        Set<String> unique = new HashSet<String>(companyList);                                           //gi search ra later pa i discover
        for (String key : unique) {
            if(key.equals(cmp))
                return Collections.frequency(companyList, key);                                          //frequency-> counts the occurrences of cmp
        }
        return 0;
    }
    public int findIndexToAdd(String cmp){
        String str = "";
        System.out.println("companyList size: " +this.companyList.size());
        for(int i = 1; i < companyList.size(); i++){
            str = companyList.get(i);
            if(cmp.equals(str)) {                                                  // if same company is found, gets the number of these companies.. (this resembles the index of the last company equal to cmp.
                System.out.println("value of find index: "+ toAdd.getCompany()+ " = "+numberOfThisCompany(str,companyList.indexOf(str)));
                if(i > 1)
                    return numberOfThisCompany(str,companyList.indexOf(str)) + i+1;       //add the result w ith 1 and ctr.because occupied ang first space sa CSV(bastaaa) Return as index of the new bus_info to be added
                else
                    return numberOfThisCompany(str,companyList.indexOf(str)) + i;
            }
        } return companyList.size()+2;
    }
    public String toAddString(){
        return toAdd.toString();                                                   //bus info needs to be in string format to be added
    }
    @FXML public void addLine(){                                                   //adding depends on the company. THe latest info will be appended at a cluster of data with a company name similar to the new info.
        String addString = toAddString();
        int rowToAdd = findIndexToAdd(toAdd.getCompany());
        companyList.clear();                                                       // it served it's purpose which is to analyze what index the new data should be inserted. It needs to be empty afterwards otherwise, calling loadbuses() will inflate this arrayList endlessly.
        if(toAdd != null && rowToAdd >= 0)
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String currentLine;
            String stringHolder;
            StringBuffer sb = new StringBuffer();
            int ctr = 0;                                                           // always increments until reaches row num.
            while((currentLine = br.readLine())!= null){                           //di maayu duha ka while.
                if(ctr == rowToAdd) {                                              //meaning this is the row to be edited.
                    stringHolder = currentLine;
                    currentLine = addString;
                    sb.append(currentLine); sb.append('\n');
                    sb.append(stringHolder); sb.append('\n');
                }
                else {
                    sb.append(currentLine);                                       //we placed each line and also the edited line.
                    sb.append('\n');                                              //separate lines
                }
                ctr++;
            }
            if(ctr <= rowToAdd){                                                  // kung naay mag add add ug lapas sa largest index, automatic append nalang
                sb.append(toAdd);
            }
            String inputStr = sb.toString(); //sb is buffer
            br.close();
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(inputStr.getBytes());
            fileOut.close();
            //inputStr.split(",\n");
        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }//loadBuses(filename);
    }
    public void editCSV(String replace, int row, int col)throws IOException   {
        if(replace == null || replace == " " )  {                                  //needed bec. if there are blanks in the csv file, the table can't be filled thus disabling refresh()
            replace = "n/a";
        }
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String currentLine;
            StringBuffer sb = new StringBuffer();
            int ctr = 0;                                                           // always increments until reaches row num.
            while((currentLine = br.readLine())!= null){                           //di maayu duha ka while.
                if(ctr == row) {                                                   //meaning this is the row to be edited.
                    int varsCtr = 0;
                    String[] vars = currentLine.split(",");                 //using , and \n as delimiters, we split currentLine
                    System.out.println(vars.length+"\n\n");
                    currentLine = "";                                             //need to empty currentLine to give way for replacements
                    while(varsCtr <= vars.length-1){                              //pwede ni ilain ug method
                                                                                  //vars.length-1 kay si varsCtr starts with zero man.
                        if(varsCtr == col) {                                      //when varsCtr reaches col, the replace string will be added to current line instead of the old string.
                            if(varsCtr == vars.length-1)
                                currentLine += replace;                           //avoid adding comma to last string of the line.
                            else
                                currentLine += replace + ",";                     //commas are to simulate the old arrangement.
                        }
                        else {
                            if (varsCtr == vars.length-1)
                                currentLine += vars[varsCtr];                     //avoid adding comma to last string of the line.
                            else
                                currentLine += vars[varsCtr] + ",";
                        }
                        varsCtr++;
                    }
                }
                sb.append(currentLine);                                           //we placed each line and also the edited line.
                sb.append('\n');                                                  //separate lines
                ctr++;
            }
            String inputStr = sb.toString();                                      //sb is buffer
            br.close();
            System.out.println(inputStr);
            FileOutputStream fileOut = new FileOutputStream(filename);
            fileOut.write(inputStr.getBytes());
            fileOut.close();
        } catch (Exception e) {
        }
        refresh();
        loadBusesToFirebase(filename);
    }
    public void loadBusesToFirebase(String filename) {
        DatabaseReference busRef = database.child("Bus_Info");
        Map<String, Bus_Info> buses = new HashMap<String, Bus_Info>();
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            br.readLine();

            String sCurrentLine = null;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] vars = sCurrentLine.split(",");
                Bus_Info b = new Bus_Info(vars[2],vars[3],vars[4],vars[5], vars[6]);
                buses.put(vars[1], b);
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
        busRef.setValue(buses);
    }
    public void displayList(){
        for(String str: companyList){                                              //used or might be used for debugging
            System.out.println(str);
        }
    }
    public void loadBuses() throws IOException {
        companyList.clear();
        BufferedReader br = null;
        FileReader fr = null;
        int ctr = 0;                                                               // for the array only to display the nos. for debugging
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            br.readLine();
            Integer counter = 1;
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] vars = sCurrentLine.split(",");
                Bus_Info b = new Bus_Info(vars[1],vars[2],vars[3],vars[4],vars[5], vars[6]);
                Bus_Info.add(b);
                directoryTable.setItems(Bus_Info);
                companyList.add(vars[3]);
            }
            ROW_MAX = counter; //important!!
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

        }displayList();
    }


}