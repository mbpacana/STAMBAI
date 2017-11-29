package controller;

import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import dao.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class RealtimeController implements Initializable {

    @FXML private TableView<Data> realtimeTable;
    @FXML private TableColumn<Data, String> BusID;
    @FXML private TableColumn<Data, String> destination;
    @FXML private TableColumn<Data, String> loading_bay;
    @FXML private TableColumn<Data, String> timeOfArrival;
    @FXML private TableColumn<Data, String> timeOfDeparture;
    @FXML private TableColumn<Data, String> duration;
    @FXML private TableColumn<Data, String> violation;
    @FXML private TextField searchField;

    private ObservableList<Data> data;
    private DatabaseReference database;


    /*
        Initialize the real time tableview and the whole realtime scene
     */
    public void initialize(URL location, ResourceBundle resources) {
        BusID.setCellValueFactory(new PropertyValueFactory<>("BusID"));
        destination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        loading_bay.setCellValueFactory(new PropertyValueFactory<>("loading_bay"));
        timeOfArrival.setCellValueFactory(new PropertyValueFactory<>("timeOfArrival"));
        timeOfDeparture.setCellValueFactory(new PropertyValueFactory<>("timeOfDeparture"));
        duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        violation.setCellValueFactory(new PropertyValueFactory<>("violation"));
        violation.setCellFactory(column -> {
            return new TableCell<Data, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    if (item == null || empty) { //If the cell is empty
                        setText(null);
                        setStyle("");
                    } else { //If the cell is not empty
                        setText(item); //Put the String data in the cell
                        //We get here all the info of the bus of this row
                        TableRow currentRow = getTableRow();
                        if (!(item.equals("No")))
                            currentRow.setStyle("-fx-background-color: salmon;");
                        else currentRow.setStyle("");
                    }
                }
            };
        });
        data = FXCollections.observableArrayList();
        database = FirebaseDatabase.getInstance().getReference();
       //loadBuses("Book3.csv");
        //initRTData();
        //loadRFIDs("/Users/tolapura/Desktop/128 3/src/main/resources/RFIDS.csv");
        startRTDataListener();
        FilteredList<Data> filtered = new FilteredList<Data>(data, e -> true);
        searchField.setOnKeyPressed(e->{
            searchField.textProperty().addListener(((observable, oldValue, newValue) -> {
                filtered.setPredicate((Predicate<? super Data>) data->{
                    if(newValue==null || newValue.isEmpty()){
                        return true;
                    }

                    String lowercasefilter = newValue.toLowerCase(); //value to search in table view

                    if(data.getBusID().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(data.getDestination().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(data.getDuration() != null && data.getDuration().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(data.getLoading_bay().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(data.getTimeOfArrival().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(data.getTimeOfDeparture().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }else if(data.getViolation() != null && data.getViolation().toLowerCase().contains(lowercasefilter)){
                        return true;
                    }

                    return false;
                });

                SortedList<Data> sorteddata = new SortedList<Data>(filtered);
                sorteddata.comparatorProperty().bind(realtimeTable.comparatorProperty());
                realtimeTable.setItems(sorteddata);
            }));
        });
    }

    // button which switches the scene to Directory
    public void switchToDirectory(ActionEvent event) throws IOException {
        System.out.println("NSUUUD SA DIRECTORY");
        Parent directory = FXMLLoader.load(getClass().getResource("/fxml/DirectoryView.fxml"));
        Scene directoryScene = new Scene(directory);
        directoryScene.getStylesheets().add("style.css");
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(directoryScene);
    }

    // button which switches the scene to reports
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


    /*
        loads the buses from csv file to the firebase
     */
    private void loadBuses(String filename) {
        DatabaseReference busRef = database.child("Bus_Info");
        Map<String, Bus_Info> buses = new HashMap<>();

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

    private void loadRFIDs(String filename) {
        DatabaseReference busRef = database.child("RTData");
        Map<String, RTData> rfids = new HashMap<String, RTData>();

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            br.readLine();

            String sCurrentLine = null;
            int i = 0;
            while ((sCurrentLine = br.readLine()) != null) {
                String[] vars = sCurrentLine.split(",");
                RTData b = new RTData(vars[0],vars[1],vars[2],vars[3],vars[4], vars[5]);
                rfids.put("-Kxg9jXllJ4me6tnPBJ_"+ (i++), b);
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
        busRef.setValue(rfids);
    }


    public void initRTData() {
        DatabaseReference rtRef = database.child("RTData");
        Map<String, RTData> rts = new HashMap<>();
        rts.put("-Kxg9jXllJ4me6tnPBJ_", new RTData("E1","2017-12-05 05:30:06","2017-12-05 14:35:06","9D:CA:30:5B","9 hours 5 minutes ","8 hours 35 minutes "));  //dummy
        rtRef.setValue(rts);
    }

    public void initRTData( int r) {
        DatabaseReference rtRef = database.child("RTData");
        Map<String, RTData> rts = new HashMap<>();
        for(int i = 0 ;i < r; i++) {
            rts.put("-Kxg9jXllJ4me6tnPBJ_"+i, new RTData("E1", "2017-12-05 05:30:06", "2017-12-05 06:30:06", "9D:CA:30:5B", "30 mins", "Yes"));  //dummy
            rtRef.setValue(rts);
        }
    }


    //Starts listening data from the firebase
    private void startRTDataListener() {
        System.out.println("NISUDDDDD");

        DatabaseReference rtRef = database.child("RTData"); //referencing the RTData
        rtRef.addChildEventListener(new ChildEventListener(){   //listens to every event on the children of RTData
            public void onChildAdded(final DataSnapshot rtsnapshot, String previousChildName) { //if new child is added
                final RTData rtd = rtsnapshot.getValue(RTData.class);   //get the new child's value as an RTData class
                DatabaseReference busRef = database.child("Bus_Info").child(rtd.getRFID()); //referencing the dao.Bus_Info
                busRef.addListenerForSingleValueEvent(new ValueEventListener(){ //listens for a single event under the Bus_Info
                    public void onDataChange(DataSnapshot bsnapshot) {
                        //System.out.println(rtd.getRFID() + "\t\t" + rtd.getTime_in() + "\t" + rtd.getTime_out() + "\t\t"+rtd.getLoading_bay());
                        Bus_Info b = bsnapshot.getValue(Bus_Info.class);   //get the child's value as a Bus_info class
                        Data x;
                        if(rtd.getTime_out() == null) {
                            x = new Data(rtd.getRFID(), b.getDestination(),rtd.getLoading_bay(), getTimeOnly(rtd.getTime_in()),
                                    rtd.getTime_out(), rtd.getDuration(), rtd.getViolation()); //wraps all info needed for the tableview into Data
                        }else{
                            x = new Data(rtd.getRFID(), b.getDestination(),rtd.getLoading_bay(), getTimeOnly(rtd.getTime_in()),
                                    getTimeOnly(rtd.getTime_out()), rtd.getDuration(), rtd.getViolation()); //wraps all info needed for the tableview into Data
                        }
                        data.add(x); //adds into the observable list for tableview
                        realtimeTable.setItems(data);   //set tableview
                        realtimeTable.getSortOrder().add(loading_bay);
                    }

                    public void onCancelled(DatabaseError error) {
                        // TODO Auto-generated method stub
                    }
                });
            }

            public void onChildChanged(DataSnapshot rtsnapshot, String previousChildName) { //if any child from the RTData is changed
                RTData rtd = rtsnapshot.getValue(RTData.class); //get the changed child as an RTData class
                System.out.println("Changed");
                changeTimeOut(rtsnapshot.getKey(),rtd.getTime_out());
            }

            public void onChildRemoved(DataSnapshot snapshot) {
                System.out.println("Removed");
            }

            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                System.out.println("Moved");
            }

            public void onCancelled(DatabaseError error) {
                System.out.println("The read failed: " + error.getMessage());
            }
        });
    }

    //If time_out changes in firebase, tableview records time of departure
    public void changeTimeOut(final String key, final String time_out){
        DatabaseReference rtRef = database.child("RTData");
        rtRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if(postSnapshot.getKey().equals(key)){
                        Data newData = realtimeTable.getItems().get(i);

                        newData.setTimeOfDeparture(getTimeOnly(time_out));

                        /*calculate time departure*/
                        RTData rtd = postSnapshot.getValue(RTData.class);
                        long dur =  calcDuration(rtd.getTime_in(),time_out);
                        //System.out.println("Duration: "+dur);
                        newData.setTimeOfArrival(getTimeOnly(rtd.getTime_in()));
                        newData.setViolation(checkViolation(dur));
                        newData.setDuration(stringDuration(dur));
                        loadDurationViolation(key,stringDuration(dur),checkViolation(dur));
                        data.set(i, newData);
                        realtimeTable.getItems().set(i, newData);
                    }
                    i++;
                }
            }
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void loadDurationViolation(final String key,final String duration,final String violation) {
        final DatabaseReference keyRef = database.child("RTData").child(key); // reference sa data
        keyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Map<String, Data> dataMap = new HashMap<String, Data>();

                RTData rtd = snapshot.getValue(RTData.class);
                rtd.setDuration(duration);
                rtd.setViolation(violation);
                keyRef.setValue(rtd);
            }
            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }


    //calculates duration
    private long calcDuration(String timeIn, String timeOut){// calulcates duration
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null, d2 = null;
        long diff = 0;
        try {
            d1 = format.parse(timeIn);
            d2 = format.parse(timeOut);
            //in milliseconds
            diff = d2.getTime() - d1.getTime();
        }catch(Exception e){
            e.printStackTrace();
        }
        return diff ;
    }

    //stringifies duration
    private String stringDuration(long diff){
        StringBuilder date = new StringBuilder();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        try{
            if(diffDays != 0){
                date.append( diffDays + " day");
                if(diffDays > 1){
                    date.append( diffDays + "s");
                }
                if(diffHours != 0 && diffSeconds != 0 && diffMinutes != 0){
                    date.append(diffDays + ", ");
                }
            }
            if(diffHours != 0){
                date.append(String.valueOf(diffHours + " hour"));
                if(diffHours > 1){
                    date.append("s ");
                }
                if(diffSeconds != 0 && diffMinutes != 0){
                    date.append(", ");
                }
            }
            if(diffMinutes != 0 ){
                date.append(String.valueOf(diffMinutes) + " minute");
                if(diffMinutes > 1){
                    date.append("s ");
                }
                if(diffSeconds != 0){
                    date.append(", ");
                }
            }
            if(diffSeconds != 0){
                date.append(String.valueOf(diffSeconds) + " second");
                if(diffSeconds > 1){
                    date.append("s ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date.toString();
    }

    private String getTimeOnly(String time){
        if(time.isEmpty() || time == null){
            return "-";
        }
        System.out.println(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        long diff = 0,diffSeconds = 0, diffHours = 0, diffMinutes = 0;
        try {
            d1 = format.parse(time); //in milliseconds
            diff = d1.getTime();
            diffHours = diff / (60 * 60 * 1000) % 24;
        }catch(Exception e){
            e.printStackTrace();
        }
        String[] tokens = time.split(" ");

        tokens = tokens[1].split(":");

        String ans = tokens[0].concat(":"+tokens[1]);
        System.out.println(diffHours);
        int timeCompHour = Integer.valueOf(tokens[0]);
        int timeCompMin = Integer.valueOf(tokens[0]);
        int timeCompSec = Integer.valueOf(tokens[0]);
        if( timeCompHour >= 12){
            if(timeCompHour >= 13){
                timeCompHour -=12;
                ans = String.valueOf(timeCompHour).concat(":"+tokens[1]);
            }
            if(timeCompMin == 0 && timeCompHour == 12 && timeCompSec < 59){
                ans = ans.concat(" nn");
            }else{
                ans = ans.concat(" pm");
            }
        }else{
            if(timeCompMin == 0 && timeCompHour == 0 && timeCompSec < 59){
                ans = ans.concat(" mn");
            }else{
                ans = ans.concat(" am");
            }
        }
        return ans;
    }
    private String checkViolation(long dur){
        if(dur > 1800000){
            return stringDuration(dur-1800000);
        }
        return "No";
    }
}