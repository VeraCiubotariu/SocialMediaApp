package ir.map.gr222.sem7.gui;

import ir.map.gr222.sem7.controller.EditUserController;
import ir.map.gr222.sem7.controller.MessageAlert;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.service.UserService;
import ir.map.gr222.sem7.utils.events.UserChangeEvent;
import ir.map.gr222.sem7.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdminController implements Observer<UserChangeEvent> {
    UserService service;
    ObservableList<User> model = FXCollections.observableArrayList();


    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    TableColumn<User,String> tableColumnUsername;
    @FXML
    TableColumn<User,String> tableColumnPassword;

    public void setService(UserService service) {
        this.service = service;
        this.service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
    //    tableColumnUserID.setCellValueFactory(new PropertyValueFactory<User, Long>("id"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        tableColumnPassword.setCellValueFactory(new PropertyValueFactory<User, String>("password"));
        tableView.setItems(model);
    }

    private void initModel() {
        Iterable<User> users = service.getAllUsers();
        List<User> messageTaskList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(messageTaskList);
    }

    @FXML
    public void handleAddUser(ActionEvent ev) {
        showUserEditDialog(null);
    }

    @FXML
    public void handleUpdateUser(ActionEvent ev) {
        User selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showUserEditDialog(selected);
        } else
            MessageAlert.showErrorMessage(null, "NU ati selectat nici un student");
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        User selected = (User) tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            User deleted = service.deleteUser(selected.getId());
            if (null != deleted)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Studentul a fost sters cu succes!");
        } else MessageAlert.showErrorMessage(null, "Nu ati selectat nici un student!");
    }

    public void showUserEditDialog(User user) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ir/map/gr222/sem7/gui/views/edituser-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            EditUserController editUserController = loader.getController();
            editUserController.setService(service, dialogStage, user);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
    }
}