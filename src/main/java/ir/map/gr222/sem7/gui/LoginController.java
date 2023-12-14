package ir.map.gr222.sem7.gui;

import ir.map.gr222.sem7.controller.EditUserController;
import ir.map.gr222.sem7.controller.MessageAlert;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.ValidationException;
import ir.map.gr222.sem7.service.MessageService;
import ir.map.gr222.sem7.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField passwordFieldPassword;

    private UserService service;

    private MessageService messageService;
    Stage loginStage;

    @FXML
    private void initialize() {
    }

    public void setService(UserService service, Stage loginStage, MessageService messageService) {
        this.service = service;
        this.loginStage = loginStage;
        this.messageService = messageService;
    }

    @FXML
    public void handleLogin(){
        String username = textFieldUsername.getText();
        String password = passwordFieldPassword.getText();

        try{
            User user = this.service.checkLogin(username, password);

            if(Objects.equals(user.getUsername(), "admin")){
                this.loadAdminView();
            }

            else{
                this.loadUserView(user);
            }

        } catch(Exception e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    public void handleSignup(){
        this.loadSignupView();
    }

    private void loadAdminView(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ir/map/gr222/sem7/gui/views/admin-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("ADMIN");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AdminController adminController = loader.getController();
            adminController.setService(service);

            dialogStage.show();
         //   loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserView(User user){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ir/map/gr222/sem7/gui/views/user-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ducky - User " + user.getUsername());
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UserController userController = loader.getController();
            userController.setServiceAndUser(service, user, messageService);

            dialogStage.show();
        //    loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        textFieldUsername.setText("");
        passwordFieldPassword.setText("");
    }

    private void loadSignupView(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ir/map/gr222/sem7/gui/views/edituser-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Signup");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController editUserController = loader.getController();
            editUserController.setService(service, dialogStage, null);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
