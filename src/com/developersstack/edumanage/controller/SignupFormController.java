package com.developersstack.edumanage.controller;

import com.developersstack.edumanage.db.Database;
import com.developersstack.edumanage.db.DbConnection;
import com.developersstack.edumanage.model.User;
import com.developersstack.edumanage.util.security.PasswordManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class SignupFormController {
    public AnchorPane context;
    public TextField txtFirstName;
    public TextField txtLastName;
    public TextField txtEmail;
    public PasswordField txtPassword;

    public void alreadyHaveAnAccountOnAction(ActionEvent actionEvent) throws IOException {
        setUi("LoginForm");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }

    public void signUpOnAction(ActionEvent actionEvent) throws IOException {
        String email = txtEmail.getText().toLowerCase();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String password = new PasswordManager().encrypt(txtPassword.getText().trim());
        User createUser = new User(firstName, lastName, email, password);
        try{
            boolean isSaved = signup(createUser);
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION, "Welcome!").show();
                setUi("LoginForm");
            }else{
                new Alert(Alert.AlertType.WARNING, "Try Again!").show();
            }
        } catch (SQLException | ClassNotFoundException e1) {
            new Alert(Alert.AlertType.ERROR, e1.toString()).show();
        }
    }

    //==================================
    private boolean signup(User user) throws SQLException, ClassNotFoundException {
        Connection connection = DbConnection.getInstance().getConnection();
        // Write a SQL
        String sql = "INSERT INTO user VALUES (?,?,?,?)";
        // Create a statement
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,user.getEmail());
        statement.setString(2,user.getFirstName());
        statement.setString(3,user.getLastName());
        statement.setString(4,user.getPassword());
        // Set sql into the statement and execute
        return statement.executeUpdate()>0; // INSERT, UPDATE, DELETE
    }
}
