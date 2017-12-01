package controller;

import com.google.firebase.database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import dao.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
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

    public void initialize(URL location, ResourceBundle resources) {
        realtimeTable.fixedCellSizeProperty().setValue(35);
        BusID.setCellValueFactory(new PropertyValueFactory<>("BusID"));
        destination.setCellValueFactory(new PropertyValueFactory<>("destination"));
        loading_bay.setCellValueFactory(new PropertyValueFactory<>("loading_bay"));
        timeOfArrival.setCellValueFactory(new PropertyValueFactory<>("timeOfArrival"));
        timeOfDeparture.setCellValueFactory(new PropertyValueFactory<>("timeOfDeparture"));
        duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        violation.setCellValueFactory(new PropertyValueFactory<>("violation"));
        violation.setCellFactory(column -> new TableCell<Data, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    TableRow currentRow = getTableRow();
                    if (!(item.equals("No")))
                        currentRow.setStyle("-fx-background-color: rgba(255,3,0,0.26);");
                    else currentRow.setStyle("");
                }
            }
        });

        data = FXCollections.observableArrayList();
        database = FirebaseDatabase.getInstance().getReference();

        //loadBuses("Book3.csv");
        //initRTData();
        //loadRFIDs("/Users/tolapura/Desktop/128 3/src/main/resources/RFIDS.csv");

        startRTDataListener();
        FilteredList<Data> filtered = new FilteredList<Data>(data, e -> true);
        searchField.setOnKeyPressed(e -> {
            searchField.textProperty().addListener(((observable, oldValue, newValue) -> {
                filtered.setPredicate((Predicate<? super Data>) data -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowercasefilter = newValue.toLowerCase();
                    String array[] = new String[]{
                            data.getBusID(),
                            data.getLoading_bay(),
                            data.getDestination(),
                            data.getTimeOfArrival(),
                            data.getTimeOfDeparture(),
                            data.getDuration(),
                            data.getViolation()};
                    for (int i = 0; i < array.length; i++) {
                        if ( (array[i] != null) && (array[i].toLowerCase().contains(lowercasefilter)) ) {
                            return true;
                        }
                    }
                    return false;
                });

                SortedList<Data> sorteddata = new SortedList<Data>(filtered);
                sorteddata.comparatorProperty().bind(realtimeTable.comparatorProperty());
                realtimeTable.setItems(sorteddata);
            }));
        });
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

    private void loadBuses(String filename) throws IOException {
        DatabaseReference busRef = database.child("Bus_Info");
        Map<String, Bus_Info> buses = new HashMap<>();
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

    private void loadRFIDs(String filename) throws IOException {
        DatabaseReference busRef = database.child("RTData");
        Map<String, RTData> rfids = new HashMap<String, RTData>();
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        String sCurrentLine = null;
        int i = 0;
        while ((sCurrentLine = br.readLine()) != null) {
            String[] vars = sCurrentLine.split(",");
            RTData b = new RTData(vars[0], vars[1], vars[2], vars[3], vars[4], vars[5]);
            rfids.put("-Kxg9jXllJ4me6tnPBJ_" + (i++), b);
        }
        br.close();
        busRef.setValue(rfids);
    }


    public void initRTData() {
        DatabaseReference rtRef = database.child("RTData");
        Map<String, RTData> rts = new HashMap<>();
        rts.put("-Kxg9jXllJ4me6tnPBJ_", new RTData(
                "E1",
                "2017-12-05 05:30:06",
                "2017-12-05 14:35:06",
                "9D:CA:30:5B",
                "9 hours 5 minutes ",
                "8 hours 35 minutes "));
        rtRef.setValue(rts);
    }

    public void initRTData(int r) {
        DatabaseReference rtRef = database.child("RTData");
        Map<String, RTData> rts = new HashMap<>();
        for (int i = 0; i < r; i++) {
            rts.put("-Kxg9jXllJ4me6tnPBJ_" + i, new RTData(
                    "E1",
                    "2017-12-05 05:30:06",
                    "2017-12-05 06:30:06",
                    "9D:CA:30:5B",
                    "30 mins",
                    "Yes"));
            rtRef.setValue(rts);
        }
    }

    private void startRTDataListener() {


        DatabaseReference rtRef = database.child("RTData");
        rtRef.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(final DataSnapshot rtsnapshot, String previousChildName) {

                final RTData rtd = rtsnapshot.getValue(RTData.class);
                DatabaseReference busRef = database.child("Bus_Info").child(rtd.getRFID());

                busRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    public void onDataChange(DataSnapshot bsnapshot) {

                        Bus_Info b = bsnapshot.getValue(Bus_Info.class);
                        Data x;
                        if (rtd.getTime_out() == null) {
                            x = new Data(

                                    rtd.getRFID(),
                                    b.getDestination(),
                                    rtd.getLoading_bay(),
                                    getTimeOnly(rtd.getTime_in()),
                                    rtd.getTime_out(),
                                    rtd.getDuration(),
                                    rtd.getViolation()
                            );
                            System.out.println(rtd.getRFID());
                        } else {
                            x = new Data(
                                    rtd.getRFID(),
                                    b.getDestination(),
                                    rtd.getLoading_bay(),
                                    getTimeOnly(rtd.getTime_in()),
                                    getTimeOnly(rtd.getTime_out()),
                                    rtd.getDuration(),
                                    rtd.getViolation()
                            );
                        }
                        data.add(x);
                        realtimeTable.setItems(data);
                        realtimeTable.getSortOrder().add(loading_bay);
                    }

                    public void onCancelled(DatabaseError error) {
                        // TODO Auto-generated method stub
                    }
                });
            }

            public void onChildChanged(DataSnapshot rtsnapshot, String previousChildName) {
                RTData rtd = rtsnapshot.getValue(RTData.class);
                System.out.println("Changed");
                changeTimeOut(rtsnapshot.getKey(), rtd.getTime_out());
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

    public void changeTimeOut(final String key, final String time_out) {
        DatabaseReference rtRef = database.child("RTData");
        rtRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if (postSnapshot.getKey().equals(key)) {
                        Data newData = realtimeTable.getItems().get(i);
                        newData.setTimeOfDeparture(getTimeOnly(time_out));
                        RTData rtd = postSnapshot.getValue(RTData.class);
                        long dur = calcDuration(rtd.getTime_in(), time_out);
                        newData.setTimeOfArrival(getTimeOnly(rtd.getTime_in()));
                        newData.setViolation(checkViolation(dur));
                        newData.setDuration(stringDuration(dur));
                        loadDurationViolation(key, stringDuration(dur), checkViolation(dur));
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

    private void loadDurationViolation(final String key, final String duration, final String violation) {
        final DatabaseReference keyRef = database.child("RTData").child(key);
        keyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
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

    private long calcDuration(String timeIn, String timeOut) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1, d2;
        long diff = 0;
        try {
            d1 = format.parse(timeIn);
            d2 = format.parse(timeOut);
            diff = d2.getTime() - d1.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;
    }

    private String stringDuration(long diff) {
        StringBuilder date = new StringBuilder();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        try {
            if (diffDays != 0) {
                date.append(diffDays + " day");
                /*if (diffDays > 1) {
                    date.append(diffDays + "s");
                }*/
                if (diffHours != 0 && diffSeconds != 0 && diffMinutes != 0) {
                    date.append(diffDays + ", ");
                }
            }
            if (diffHours != 0) {
                date.append(String.valueOf(diffHours + " hour"));
                /*if (diffHours > 1) {
                    date.append("s ");
                }*/
                if (diffSeconds != 0 && diffMinutes != 0) {
                    date.append(", ");
                }
            }
            if (diffMinutes != 0) {
                date.append(String.valueOf(diffMinutes) + " m");
                /*if (diffMinutes > 1) {
                    date.append("s ");
                }*/
                if (diffSeconds != 0) {
                    date.append(", ");
                }
            }
            if (diffSeconds != 0) {
                date.append(String.valueOf(diffSeconds) + " s");
                /*if (diffSeconds > 1) {
                    date.append("s ");
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date.toString();
    }

    private String getTimeOnly(String time) {
        if (time.isEmpty() || time == null) {
            return "-";
        }
        System.out.println(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1;
        long diff = 0;
        long diffHours = 0;
        try {
            d1 = format.parse(time);
            diff = d1.getTime();
            diffHours = diff / (60 * 60 * 1000) % 24;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] tokens = time.split(" ");
        tokens = tokens[1].split(":");
        String ans = tokens[0].concat(":" + tokens[1]);

        int timeCompHour = Integer.valueOf(tokens[0]);
        int timeCompMin = Integer.valueOf(tokens[0]);
        int timeCompSec = Integer.valueOf(tokens[0]);
        if (timeCompHour >= 12) {
            if (timeCompHour >= 13) {
                timeCompHour -= 12;
                ans = String.valueOf(timeCompHour).concat(":" + tokens[1]);
            }
            ans = (timeCompMin == 0 && timeCompHour == 12 && timeCompSec < 59) ?
                    ans.concat(" NN") : ans.concat(" PM");
        } else {
            ans = (timeCompMin == 0 && timeCompHour == 0 && timeCompSec < 59) ?
                    ans.concat(" MN") : ans.concat(" AM");
        }
        return ans;
    }

    private String checkViolation(long dur) {
        if (dur > 40000) {
            return stringDuration(dur - 40000);
        }
        return "No";
    }
}