package com.developersstack.edumanage.controller;

import com.developersstack.edumanage.db.Database;
import com.developersstack.edumanage.model.Program;
import com.developersstack.edumanage.model.Teacher;
import com.developersstack.edumanage.view.tm.ProgramTm;
import com.developersstack.edumanage.view.tm.TechAddTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

public class ProgramsFormController {
    public AnchorPane context;
    public TextField txtId;
    public TextField txtName;
    public TextField txtSearch;
    public Button btn;
    public TableView<ProgramTm> tblProgram;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colTeacher;
    public TableColumn colTechnologies;
    public TableColumn colCost;
    public TableColumn colOptions;
    public TextField txtCost;
    public ComboBox<String> cmbTeacher;
    public TableView<TechAddTm> tblTechnology;
    public TableColumn colTRemove;
    public TextField txtTechnology;
    public TableColumn colTCode;
    public TableColumn colTName;

    public void initialize(){
        setProgramCode();
        setTeachers();
        loadPrograms();

        colId.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacher"));
        colTechnologies.setCellValueFactory(new PropertyValueFactory<>("btnTech"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("btn"));

        colTCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colTName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTRemove.setCellValueFactory(new PropertyValueFactory<>("btn"));
    }
    ArrayList<String> teachersArray = new ArrayList<>();
    private void setTeachers() {
        for (Teacher teacher:Database.teacherTable ) {
            teachersArray.add(teacher.getCode()+ ". " + teacher.getName());
        }
        ObservableList<String> obList = FXCollections.observableArrayList(teachersArray);
        cmbTeacher.setItems(obList);
    }

    private void setProgramCode() {
        if(!Database.programTable.isEmpty()){
            Program lastProgram = Database.programTable.get(
                    Database.programTable.size()-1
            );
            String lastId = lastProgram.getCode();
            String splitData[] = lastId.split("-");
            String lastIdIntegerNumberAsString = splitData[1];
            int lastIntegerIdAsInt = Integer.parseInt(lastIdIntegerNumberAsString);
            lastIntegerIdAsInt++;
            String generatedProgramId = "P-"+lastIntegerIdAsInt;
            txtId.setText(generatedProgramId);
        }else{
            txtId.setText("P-1");
        }
    }

    public void saveOnAction(ActionEvent actionEvent) {
        int pointer = 0;
        String[] selectedTechs = new String[tmList.size()];
        for (TechAddTm t: tmList){
            selectedTechs[pointer] = t.getName();
            pointer++;
        }

        if (btn.getText().equals("Save Program")){
             Program program = new Program(
                     txtId.getText(),
                     txtName.getText(),
                     selectedTechs,
                     cmbTeacher.getValue().split("\\.")[0],
                     Double.parseDouble((txtCost.getText()))
             );
             Database.programTable.add(program);
             new Alert(Alert.AlertType.INFORMATION,"Saved").show();
             loadPrograms();
        }
    }

    private void loadPrograms(){
        ObservableList<ProgramTm> programsTmList = FXCollections.observableArrayList();
        for (Program p: Database.programTable) {
            Button techButton = new Button("Show Tech");
            Button removeButton = new Button("Delete");
            ProgramTm tm = new ProgramTm(
                    p.getCode(),
                    p.getName(),
                    p.getTeacherId(),
                    techButton,
                    p.getCost(),
                    removeButton
            );
            programsTmList.add(tm);
        }
        tblProgram.setItems(programsTmList);
    }

    public void newProgramOnAction(ActionEvent actionEvent) {
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashboardForm");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }

    ObservableList<TechAddTm> tmList = FXCollections.observableArrayList();

    public void addTechOnAction(ActionEvent actionEvent) {
        if(!isExists(txtTechnology.getText().trim())){
            Button btn = new Button("Remove");
            TechAddTm tm = new TechAddTm(
                   tmList.size()+1,txtTechnology.getText().trim(),btn
            );
            tmList.add(tm);
            tblTechnology.setItems(tmList);
            txtTechnology.clear();
        }else{
            txtTechnology.selectAll();
            new Alert(Alert.AlertType.WARNING, "Already Exists").show();
        }
    }

    private boolean isExists(String tech){
        for (TechAddTm tm :tmList) {
            if(tm.getName().equals(tech)){
                return true;
            }
        }
        return false;
    }
}
