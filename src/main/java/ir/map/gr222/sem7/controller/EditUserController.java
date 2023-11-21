package ir.map.gr222.sem7.controller;

import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.ValidationException;
import ir.map.gr222.sem7.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class EditUserController {
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;

    private UserService service;
    Stage dialogStage;
    User user;

    @FXML
    private void initialize() {
    }

    public void setService(UserService service,  Stage stage, User m) {
        this.service = service;
        this.dialogStage=stage;
        this.user =m;

        if (null != m) {
            setFields(m);
        }
    }

    @FXML
    public void handleSave(){
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();

        User m = new User(firstName,lastName);
        if (null == this.user)
            saveUser(m);
        else{
            m.setId(this.user.getId());
            updateUser(m);
        }
    }

    private void updateUser(User m)
    {
        try {
            Optional<User> r = this.service.updateUser(m);
            if (r.isEmpty())
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare mesaj","Mesajul a fost modificat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();
    }


    private void saveUser(User m)
    {
        // TODO
        try {
            Optional<User> r= this.service.addUser(m);
            if (r.isEmpty())
                dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare mesaj","Mesajul a fost salvat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();
    }

    private void clearFields() {
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
    }

    private void setFields(User s)
    {
        textFieldFirstName.setText(s.getFirstName());
        textFieldLastName.setText(s.getLastName());
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
