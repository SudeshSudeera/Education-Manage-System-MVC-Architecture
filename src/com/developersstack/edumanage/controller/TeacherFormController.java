package com.developersstack.edumanage.controller;

import com.developersstack.edumanage.db.Database;
import com.developersstack.edumanage.db.DbConnection;
import com.developersstack.edumanage.model.Student;
import com.developersstack.edumanage.model.Teacher;
import com.developersstack.edumanage.view.tm.TeacherTm;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherFormController {

    public AnchorPane teacherContext;
    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSearch;
    public Button btn;
    public TableView<TeacherTm> tblTeacher;
    public TableColumn colId;
    public TextField txtContact;
    public TableColumn colName;
    public TableColumn colContact;
    public TableColumn colAddress;
    public TableColumn colOptions;

    String searchText = "";

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("code"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colOptions.setCellValueFactory(new PropertyValueFactory<>("btn"));

        setTeacherId();
        setTableData(searchText);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchText = newValue;
            setTableData(searchText);
        });

        tblTeacher.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(null!=newValue){
                setData(newValue);
            }
        }));
    }

    private void setData(TeacherTm tm) {
        txtId.setText(tm.getCode());
        txtName.setText(tm.getName());
        txtAddress.setText(tm.getAddress());
        txtContact.setText(tm.getContact());
        btn.setText("Update Teacher");
    }

    public void newTeacherOnAction(ActionEvent actionEvent) {
        clear();
        setTeacherId();
        btn.setText("Save Teacher");
    }

    public void saveOnAction(ActionEvent actionEvent) {
        Teacher teacher = new Teacher(
                txtId.getText(),
                txtName.getText(),
                txtAddress.getText(),
                txtContact.getText()
        );
        if(btn.getText().equalsIgnoreCase("Save Teacher")){
            try{
                if(saveTeacher(teacher)){
                    setTeacherId();
                    clear();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION, "Teacher Saved!").show();
                }else{
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }
            }catch (ClassNotFoundException | SQLException e){
                new Alert(Alert.AlertType.ERROR, e.toString()).show();
            }
        }else{
            try{
                if(updateTeacher(teacher)){
                    clear();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION, "Teacher Updated!").show();

                }else{
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }
            }catch (ClassNotFoundException | SQLException e){
                new Alert(Alert.AlertType.ERROR, e.toString()).show();
            }
        }
    }

    private void setTableData(String searchText) {
        ObservableList<TeacherTm> obList = FXCollections.observableArrayList();

        try{
            for (Teacher t : searchTeachers(searchText)) {
                    Button btn = new Button("Delete");
                    TeacherTm tm = new TeacherTm(
                            t.getCode(),
                            t.getName(),
                            t.getAddress(),
                            t.getContact(),
                            btn
                    );
                    btn.setOnAction(e->{
                        Alert alert = new Alert(
                                Alert.AlertType.CONFIRMATION, "Are you sure",ButtonType.YES, ButtonType.NO
                        );
                        Optional<ButtonType> buttonType = alert.showAndWait();
                        if (buttonType.get().equals(ButtonType.YES)){

                            try{
                                if(deleteTeacher(t.getCode())){
                                    new Alert(Alert.AlertType.INFORMATION, "Deleted").show();
                                    setTableData(searchText);
                                    setTeacherId();
                                }else{
                                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                                }
                            }catch (ClassNotFoundException | SQLException ex) {
                                new Alert(Alert.AlertType.ERROR, e.toString()).show();
                            }
                        }
                    });

                    obList.add(tm);
            }
            tblTeacher.setItems(obList);
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void setTeacherId() {
        try{
            String lastId = getLastId();
            if(lastId!=null){
                String splitData[] = lastId.split("-");
                String lastIdIntegerNumberAsString = splitData[1];
                int lastIntegerIdAsInt = Integer.parseInt(lastIdIntegerNumberAsString);
                lastIntegerIdAsInt++;
                String generatedTeacherId = "T-"+lastIntegerIdAsInt;
                txtId.setText(generatedTeacherId);
            }else{
                txtId.setText("T-1");
            }
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private void clear(){
        txtContact.clear();
        //txtName.setText("");
        txtName.clear();
        txtAddress.clear();
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashboardForm");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) teacherContext.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }

    private boolean saveTeacher(Teacher teacher) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO teacher VALUES(?,?,?,?)");
        preparedStatement.setString(1,teacher.getCode());
        preparedStatement.setString(2,teacher.getName());
        preparedStatement.setString(3,teacher.getAddress());
        preparedStatement.setString(4,teacher.getContact());
        return preparedStatement.executeUpdate()>0;
    }

    private String getLastId () throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT teacher_id FROM teacher ORDER BY CAST(SUBSTRING(teacher_id,3) AS UNSIGNED) DESC LIMIT 1");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            return resultSet.getString(1);
        }
        return null;
    }

    private List<Teacher> searchTeachers(String text) throws ClassNotFoundException, SQLException {
        text = "%"+text+"%"; //%text%
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM teacher WHERE name LIKE ? OR address LIKE ?");
        preparedStatement.setString(1,text);
        preparedStatement.setString(2,text);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Teacher> list = new ArrayList<>();
        while(resultSet.next()){
            list.add(
                    new Teacher(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getString(4)
                    )
            );
        }
        return list;
    }

    private boolean deleteTeacher(String id) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM teacher WHERE teacher_id = ?");
        preparedStatement.setString(1,id);
        return preparedStatement.executeUpdate()>0;
    }

    private boolean updateTeacher(Teacher teacher) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE student SET name=?, address=?, contact=? WHERE teacher_id=?");
        preparedStatement.setString(1,teacher.getCode());
        preparedStatement.setString(2,teacher.getName());
        preparedStatement.setString(3,teacher.getAddress());
        preparedStatement.setString(4,teacher.getContact());
        return preparedStatement.executeUpdate()>0;
    }

}
