package controller;

import com.google.firebase.database.*;
import dao.Bus_Info;
import dao.Fees;
import dao.RTData;
import dao.Violations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.PDFGenerator;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML public Button editReport;
    @FXML public DatePicker from;
    @FXML public DatePicker to;
    @FXML public CheckBox violationCheckBox;
    @FXML public CheckBox feesCheckBox;
    @FXML public TableView<Violations> violationstable;
    @FXML public TableColumn<Violations, String> companyv;
    @FXML public TableColumn<Violations, String> violationcount;
    @FXML public TableView<Fees> feestable;
    @FXML public TableColumn<Fees, String> companyf;
    @FXML public TableColumn<Fees, String> buscount;
    @FXML public TableColumn<Fees, String> feescollected;
    @FXML public Button saveFile;
    @FXML public AnchorPane ap;
    @FXML public Text totalBusCount;
    @FXML public Text totalFees;
    @FXML public Text totalViolations;

    private DatabaseReference database;
    private LocalDate fromDay;
    private LocalDate toDay;

    private ArrayList<String> companies;
    private ArrayList<Integer> count;
    private static ArrayList<String> vio;
    private static ArrayList<Integer> vcount;
    private static ArrayList<String> feess;
    private static ArrayList<Integer> fcount;

    private int rem;
    private PDFGenerator pdfg;
    private ObservableList<Fees> listoffees;
    private ObservableList<Violations> listofviolations;

    public void initialize(URL location, ResourceBundle resources) {
        companyf.setCellValueFactory(new PropertyValueFactory<>("company"));
        feescollected.setCellValueFactory(new PropertyValueFactory<>("fee"));
        buscount.setCellValueFactory(new PropertyValueFactory<>("count"));
        violationcount.setCellValueFactory(new PropertyValueFactory<>("count"));
        companyv.setCellValueFactory(new PropertyValueFactory<>("company"));

        listoffees = FXCollections.observableArrayList();
        listoffees.add(new Fees("", "", ""));
        feestable.setItems(listoffees);

        listofviolations = FXCollections.observableArrayList();
        listofviolations.add(new Violations("", ""));
        violationstable.setItems(listofviolations);

        companies = new ArrayList<>();
        count = new ArrayList<>();
        fcount = new ArrayList<>();
        feess = new ArrayList<>();
        vcount = new ArrayList<>();
        vio = new ArrayList<>();

        database = FirebaseDatabase.getInstance().getReference();

        LocalDate maxDate = LocalDate.now();

        from.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(maxDate));
            }
        });

        to.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(maxDate));
            }
        });



        feesCheckBox.selectedProperty().addListener((ov, old_val, new_val) -> {
            if (feesCheckBox.isSelected()) {
                from.setDisable(true);
                to.setDisable(true);
                System.out.println("CHANGED CHECK");

                companies = new ArrayList<>();
                count = new ArrayList<>();
                fcount = new ArrayList<>();
                feess = new ArrayList<>();
                fromDay = from.getValue();
                toDay = to.getValue();

                if ((fromDay == null || toDay == null) ||
                        !feesCheckBox.isSelected() || fromDay.compareTo(toDay) > 0) {
                    System.out.println("Please fill out the dates properly.");
                }
                getAllPayers(fromDay + " 00:00:00", toDay + " 23:59:00");
            } else {
                companies = new ArrayList<>();
                count = new ArrayList<>();
                fcount = new ArrayList<>();
                feess = new ArrayList<>();

                listoffees = FXCollections.observableArrayList();
                listoffees.add(new Fees("", "", ""));
                feestable.setItems(listoffees);
                totalBusCount.setText("-");
                totalFees.setText("-");
            }

        });


        violationCheckBox.selectedProperty().addListener((ov, old_val, new_val) -> {
            if (violationCheckBox.isSelected()) {
                from.setDisable(true);
                to.setDisable(true);
                companies = new ArrayList<>();
                count = new ArrayList<>();
                vcount = new ArrayList<>();
                vio = new ArrayList<>();

                fromDay = from.getValue();
                toDay = to.getValue();

                if ((fromDay == null || toDay == null) ||
                        !violationCheckBox.isSelected() || fromDay.compareTo(toDay) > 0) {
                    System.out.println("Please fill out the dates properly.");
                }
                getAllViolators(fromDay + " 00:00:00", toDay + " 23:59:00");
            } else {
                companies = new ArrayList<>();
                count = new ArrayList<>();
                vcount = new ArrayList<>();
                vio = new ArrayList<>();

                listofviolations = FXCollections.observableArrayList();
                listofviolations.add(new Violations("", ""));
                violationstable.setItems(listofviolations);
                totalViolations.setText("-");
            }

        });

        editReport.setOnAction(event -> {
            from.setDisable(false);
            to.setDisable(false);
            violationCheckBox.setSelected(false);
            feesCheckBox.setSelected(false);
        });

        saveFile.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Report");

            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                    "PDF files (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(extFilter);
            Stage stage = (Stage) ap.getScene().getWindow();
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                if ((fromDay == null || toDay == null) ||
                        (!violationCheckBox.isSelected() && !feesCheckBox.isSelected())) {
                    System.out.println("Please fill out the dates properly.");
                } else try {
                    pdfg = new PDFGenerator(file, fromDay.toString(), toDay.toString());
                    System.out.println(vio.size() + "size");
                    if (!vio.isEmpty() && !feess.isEmpty()) {
                        System.out.println("CCCCCcccc");
                        pdfg.writeViolationsToPDF(vio, count);
                        pdfg.writeFeesToPDF(feess, fcount);
                    } else if (!vio.isEmpty()) {
                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa " + vio.size());
                        pdfg.writeViolationsToPDF(vio, count);
                    } else if (!feess.isEmpty()) {
                        System.out.println("BBBBBbbbbbbbbbbbb" + feess.size());
                        pdfg.writeFeesToPDF(feess, fcount);
                    }
                    pdfg.savePDF();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getAllViolators(final String from, final String to) {

        final DatabaseReference rtRef = database.child("RTData");
        rtRef.orderByChild("time_in").startAt(from).endAt(to).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        System.out.println("\nVIOLATORS....." + "( " + snapshot.getChildrenCount());
                        ArrayList<RTData> violators = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            RTData rtd = postSnapshot.getValue(RTData.class);
                            if (!rtd.getViolation().equals("No")) {
                                violators.add(rtd);
                                System.out.println(rtd.getRFID() + " " + violators.size());
                            }
                            System.out.println(rtd.getRFID() + " " + violators.size());
                        }
                        System.out.println(" sizenisyaaaa");
                        sortViolationsByCompany(violators);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    private void getAllPayers(String from, String to) {
        DatabaseReference rtRef = database.child("RTData");
        rtRef.orderByChild("time_in").startAt(from).endAt(to).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        ArrayList<RTData> feesCollected = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            RTData rtd = postSnapshot.getValue(RTData.class);
                            System.out.println(rtd.getRFID());
                            feesCollected.add(rtd);
                        }
                        sortFeesByCompany(feesCollected);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }


    public void sortViolationsByCompany(final ArrayList<RTData> rtd) {
        System.out.println(rtd.size());
        rem = rtd.size();
        for (RTData rt : rtd) {
            final DatabaseReference busRef = database.child("Bus_Info");
            busRef.child(rt.getRFID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Bus_Info bus = snapshot.getValue(Bus_Info.class);
                    final String comp = bus.getCompany();
                    if (companies.contains(comp)) {
                        count.set(companies.indexOf(comp),
                                count.get(companies.indexOf(comp)) + 1);
                        vcount.set(vio.indexOf(comp),
                                vcount.get(vio.indexOf(comp)) + 1);
                    } else {
                        vio.add(comp);
                        vcount.add(1);
                        companies.add(comp);
                        count.add(1);
                    }
                    if (--rem == 0) {
                        if (violationCheckBox.isSelected()) {
                            System.out.println("Violation Box Selected....");
                            displayViolationsSummary(count, companies);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }

    public void sortFeesByCompany(final ArrayList<RTData> rtd) {
        System.out.println(rtd.size() + "sizenisya");
        rem = rtd.size();
        for (RTData rt : rtd) {
            final DatabaseReference busRef = database.child("Bus_Info");
            busRef.child(rt.getRFID()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Bus_Info bus = snapshot.getValue(Bus_Info.class);
                    final String comp = bus.getCompany();
                    if (companies.contains(comp)) {
                        count.set(companies.indexOf(comp),
                                count.get(companies.indexOf(comp)) + 1);
                        fcount.set(feess.indexOf(comp),
                                fcount.get(feess.indexOf(comp)) + 1);
                    } else {
                        companies.add(comp);
                        count.add(1);
                        feess.add(comp);
                        fcount.add(1);
                    }
                    if (--rem == 0) {
                        if (feesCheckBox.isSelected()) {
                            displayFeesSummary(count, companies);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }

    private void displayViolationsSummary(ArrayList<Integer> count, ArrayList<String> companies) {
        listofviolations = FXCollections.observableArrayList();
        int total = 0;
        String out = "";
        out += ("\n\nVIOLATIONS: \n");
        for (int i = 0; i != count.size(); i++) {
            Violations v = new Violations(companies.get(i) + "", count.get(i) + "");
            listofviolations.add(v);
            out += (companies.get(i) + "\t\t\t\t" + count.get(i) + "\n");
            total += count.get(i);
        }
        violationstable.setItems(listofviolations);
        out += "\t\t\t_______________\n";
        totalViolations.setText(total + "");
        out += "TOTAL\t\t\t\t" + total + "\n";
        System.out.println(out);
    }

    private void displayFeesSummary(ArrayList<Integer> count, ArrayList<String> companies) {
        listoffees = FXCollections.observableArrayList();
        String out = "";
        int total = 0;
        out += ("\n\nFEEES: \n");
        for (int i = 0; i != count.size(); i++) {
            Fees f = new Fees(
                    companies.get(i),
                    count.get(i) + "",
                    (count.get(i) * 200) + ""
            );
            listoffees.add(f);
            out += (companies.get(i) + "\t\t\t\t" + count.get(i) +
                    "\t\t\t" + (count.get(i) * 200) + "\n");
            total += count.get(i);
        }
        feestable.setItems(listoffees);
        totalBusCount.setText(total + "");
        totalFees.setText((total * 200) + "");
        out += "\t\t\t_____________________________________\n";
        out += "TOTAL\t\t\t\t" + total + "\t\t\t" + total * 200 + "\n";
        System.out.println(out);
    }


//    public void openPrompt(ActionEvent event) throws IOException {
//        FXMLLoader adddata = new FXMLLoader(getClass().getResource("fxml/PromptR.fxml"));
//        Scene newScene;
//        try {
//            newScene = new Scene((Parent) adddata.load());
//            //newScene.getStylesheets().add("style.css");
//        } catch (IOException ex) {
//            // TODO: handle error
//            return;
//        }
//
//        final Stage inputStage = new Stage();
//        Stage owner = (Stage)((Node)event.getSource()).getScene().getWindow();
//        inputStage.initOwner( owner );
//        inputStage.setScene(newScene);
//        inputStage.showAndWait();
//        inputStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//            @Override
//            public void handle(WindowEvent e) {
//                e.consume();
//                inputStage.close();
//            }
//        });
//    }

    public void switchToSettings(ActionEvent event) {
        SceneSwitching s = new SceneSwitching();
        try {
            s.switchToSettings(event);
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

    public void switchToDirectory(ActionEvent event) {
        SceneSwitching s = new SceneSwitching();
        try {
            s.switchToDirectory(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
