package com.developersstack.edumanage.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class IntakeFormController {

    public AnchorPane context;
    public TextField txtId;
    public TextField txtName;
    public DatePicker txtDate;
    public TextField txtSearch;
    public Button btn;
    public TableView tblIntake;
    public TableColumn colId;
    public TableColumn colIntakeName;
    public TableColumn colStartDate;
    public TableColumn colProgram;
    public TableColumn colCompleteState;
    public TableColumn colOptions;
    public ComboBox cmbProgram;

    public void saveOnAction(ActionEvent actionEvent) {
    }

    public void newIntakeOnAction(ActionEvent actionEvent) {
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashboardForm");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
}
