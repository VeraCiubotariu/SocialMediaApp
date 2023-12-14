package ir.map.gr222.sem7.controller;

import ir.map.gr222.sem7.domain.Message;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.service.MessageService;
import ir.map.gr222.sem7.service.UserService;
import ir.map.gr222.sem7.utils.events.UserChangeEvent;
import ir.map.gr222.sem7.utils.observer.Observer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserSelectionController implements Observer<UserChangeEvent> {
    ObservableList<User> userSelectionModel = FXCollections.observableArrayList();
    @FXML
    ListView<User> userSelectionListView;
    @FXML
    Button sendButton;
    private UserService service;
    private MessageService messageService;
    private String message;
    private List<User> selectedUsers = new ArrayList<>();
    private User currentUser;
    private Stage currentStage;

    public void setService(UserService service, MessageService messageService, String message, User currentUser, Stage currentStage){
        this.service = service;
        this.service.addObserver(this);
        this.message = message;
        this.messageService = messageService;
        this.currentUser = currentUser;
        this.currentStage = currentStage;
        initModel();
    }

    @FXML
    public void initialize() {
        userSelectionListView.setItems(userSelectionModel);

        userSelectionListView.setCellFactory(CheckBoxListCell.forListView(new Callback<User, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(User user) {
                BooleanProperty observable = new SimpleBooleanProperty();
                observable.addListener((obs, wasSelected, isNowSelected) -> {
                    if(isNowSelected){
                        selectedUsers.add(user);
                    }
                    else{
                        selectedUsers.remove(user);
                    }
                });

                return observable;
            }
        }));
    }

    private void initModel() {
        List<User> users = service.getAllUsers();

        userSelectionModel.setAll(users);

    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
    }

    public void handleSendMessage(ActionEvent actionEvent) {
        if(selectedUsers.isEmpty()){
            MessageAlert.showErrorMessage(null,"No user was selected!");
        }

        else{
            List<Long> recipients = new ArrayList<>();
            for(User u:selectedUsers){
                recipients.add(u.getId());
            }
            Message m = new Message(currentUser.getId(), recipients, message, LocalDateTime.now());
            try{
                this.messageService.sendMessage(m);
            }
            catch(Exception e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
            this.currentStage.close();
        }
    }
}
