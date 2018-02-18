package pl.czmyras.address.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import pl.czmyras.address.DateUtil;
import pl.czmyras.address.MainApp;
import pl.czmyras.address.model.Person;

import java.util.Collections;

public class PersonOverviewController {

    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, String> firstNameColumn;
    @FXML
    private TableColumn<Person, String> lastNameColumn;

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label streetLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label birthdayLabel;
    @FXML
    private Button highlightButton;
   @FXML
    private Button clearHighlightButton;

    final ObservableList<Integer> highlightRows = FXCollections.observableArrayList();

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public PersonOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        highlightRows.add(1);

        personTable.setRowFactory(new Callback<TableView<Person>, TableRow<Person>>() {
            @Override
            public TableRow<Person> call(TableView<Person> param) {

                final TableRow<Person> row = new TableRow<Person>() {
                    @Override
                    protected void updateItem(Person person, boolean empty) {
                        super.updateItem(person, empty);

//                        if (person !=null && person.getFirstName().contains("a")) {
//                            getStyleClass().add("highlightedRow");
//                        }

                        if (highlightRows.contains(getIndex())) {
                            if (!getStyleClass().contains("highlightedRow")) {
                                getStyleClass().add("highlightedRow");
                            }
                        } else {
                            getStyleClass().removeAll(Collections.singleton("highlightedRow"));
                        }
                    }
                };
                highlightRows.addListener(new ListChangeListener<Integer>() {
                    @Override
                    public void onChanged(Change<? extends Integer> change) {
                        if (highlightRows.contains(row.getIndex())) {
                            if (!row.getStyleClass().contains("highlightedRow")) {
                                row.getStyleClass().add("highlightedRow");
                            }
                        } else {
                           row.getStyleClass().removeAll(Collections.singleton("highlightedRow"));
                        }
                    }
                });
                return row;
            }
        });


        // Clear person details.
        showPersonDetails(null);

        // Listen for selection changes and show the person details when changed.
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));


        personTable.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        highlightButton.disableProperty().bind(Bindings.isEmpty(personTable.getSelectionModel().getSelectedIndices()));
        clearHighlightButton.disableProperty().bind(Bindings.isEmpty(highlightRows));

    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        personTable.setItems(mainApp.getPersonData());
    }


    /**
     * Fills all text fields to show details about the person.
     * If the specified person is null, all text fields are cleared.
     *
     * @param person the person or null
     */
    private void showPersonDetails(Person person) {
        if (person != null) {
            // Fill the labels with info from the person object.
            firstNameLabel.setText(person.getFirstName());
            lastNameLabel.setText(person.getLastName());
            streetLabel.setText(person.getStreet());
            postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
            cityLabel.setText(person.getCity());
            birthdayLabel.setText(DateUtil.format(person.getBirthday()));
        } else {
            // Person is null, remove all the text.
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            streetLabel.setText("");
            postalCodeLabel.setText("");
            cityLabel.setText("");
            birthdayLabel.setText("");
        }
    }

    @FXML
    private void handleDeletePerson() {
        int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            personTable.getItems().remove(selectedIndex);
        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleNewPerson() {
        Person tempPerson = new Person();
        boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
        if (okClicked) {
            mainApp.getPersonData().add(tempPerson);
        }
    }

    @FXML
    private void handleEditPerson() {
        Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            boolean okClicked = mainApp.showPersonEditDialog(selectedPerson);
            if (okClicked) {
                showPersonDetails(selectedPerson);
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Person Selected");
            alert.setContentText("Please select a person in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleHightlightSelection() {
        highlightRows.addAll(personTable.getSelectionModel().getSelectedIndices());
    }

    @FXML
    private void handleClearHightlightSelection() {
        highlightRows.clear();
    }

}



