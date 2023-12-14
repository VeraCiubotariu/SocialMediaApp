package ir.map.gr222.sem7.gui;

import ir.map.gr222.sem7.controller.MessageAlert;
import ir.map.gr222.sem7.controller.UserSelectionController;
import ir.map.gr222.sem7.domain.FriendRequest;
import ir.map.gr222.sem7.domain.Message;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.service.MessageService;
import ir.map.gr222.sem7.service.UserService;
import ir.map.gr222.sem7.utils.events.UserChangeEvent;
import ir.map.gr222.sem7.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserChangeEvent>  {
    ObservableList<User> usersModel = FXCollections.observableArrayList();
    @FXML
    TableView<User> userTableView;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    TableColumn<User,String> tableColumnUsername;

    ObservableList<User> friendsModel = FXCollections.observableArrayList();
    @FXML
    TableView<User> friendsTableView;
    @FXML
    TableColumn<User,String> friendsTableColumnFirstName;
    @FXML
    TableColumn<User,String> friendsTableColumnLastName;
    @FXML
    TableColumn<User,String> friendsTableColumnUsername;

    ObservableList<User> friendReqsModel = FXCollections.observableArrayList();
    @FXML
    TableView<User> friendReqsTableView;
    @FXML
    TableColumn<User,String> friendReqsTableColumnFirstName;
    @FXML
    TableColumn<User,String> friendReqsTableColumnLastName;
    @FXML
    TableColumn<User,String> friendReqsTableColumnUsername;

    ObservableList<User> allUsersModel = FXCollections.observableArrayList();
    @FXML
    TableView<User> allUsersTableView;
    @FXML
    TableColumn<User, String> allUsersTableColumnUsername;

    ObservableList<Message> messagesModel = FXCollections.observableArrayList();
    @FXML
    ListView<Message> messagesListView;

    @FXML
    TextField messageTextField;

    private UserService service;
    private MessageService messageService;

    User currentUser;

    public void setServiceAndUser(UserService service, User currentUser, MessageService messageService) {
        this.currentUser = currentUser;
        this.service = service;
        this.messageService = messageService;
        this.service.addObserver(this);
        this.messageService.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        PropertyValueFactory<User, String> firstName = new PropertyValueFactory<>("firstName");
        PropertyValueFactory<User, String> lastName = new PropertyValueFactory<>("lastName");
        PropertyValueFactory<User, String> username = new PropertyValueFactory<>("username");

        tableColumnFirstName.setCellValueFactory(firstName);
        tableColumnLastName.setCellValueFactory(lastName);
        tableColumnUsername.setCellValueFactory(username);
        userTableView.setItems(usersModel);

        friendsTableColumnFirstName.setCellValueFactory(firstName);
        friendsTableColumnLastName.setCellValueFactory(lastName);
        friendsTableColumnUsername.setCellValueFactory(username);
        friendsTableView.setItems(friendsModel);

        friendReqsTableColumnFirstName.setCellValueFactory(firstName);
        friendReqsTableColumnLastName.setCellValueFactory(lastName);
        friendReqsTableColumnUsername.setCellValueFactory(username);
        friendReqsTableView.setItems(friendReqsModel);

        allUsersTableColumnUsername.setCellValueFactory(username);
        allUsersTableView.setItems(allUsersModel);

        messagesListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Message> call(ListView<Message> param) {
                return new ListCell<>() {
                    private User recipient = allUsersTableView.getSelectionModel().getSelectedItem();

                    @Override
                    protected void updateItem(Message message, boolean empty) {
                        super.updateItem(message, empty);
                        if (message == null || empty) {
                            setText(null);
                        } else {
                            if (Objects.equals(message.getFrom(), currentUser.getId())) {
                                setText("You: " + message.getMessage());
                            } else {
                                setText(recipient.getUsername() + ": " + message.getMessage());
                            }
                        }
                    }
                };
            }
        });

        messagesListView.setItems(messagesModel);
    }

    private void initModel() {
        Iterable<User> users = service.getNonFriendUsers(this.currentUser);
        List<User> messageTaskList = StreamSupport.stream(users.spliterator(), false)
                .collect(Collectors.toList());
        usersModel.setAll(messageTaskList);

        List<User> friends = service.getAllFriends(this.currentUser);
        friendsModel.setAll(friends);

        List<User> friendRequests = service.getAllPendingUserRequests(this.currentUser);
        friendReqsModel.setAll(friendRequests);

        List<User> allUsers = service.getAllUsers();
        allUsersModel.setAll(allUsers);

        this.updateMessages(null);
    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
    }

    public void handleSendFriendRequest(ActionEvent actionEvent) {
        User selected = userTableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try{
                if(this.service.sendFriendRequest(this.currentUser.getId(), selected.getId()).isEmpty()){
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend Request", "friend request was sent successfully");
                }
                else {
                    MessageAlert.showErrorMessage(null, "couldn't send request");
                }
            } catch (Exception e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        } else
            MessageAlert.showErrorMessage(null, "No user was selected");
    }

    public void handleAcceptFriendRequest(ActionEvent actionEvent) {
        User selected = friendReqsTableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try{
                if(this.service.acceptFriendRequest(this.currentUser.getId(), selected.getId()).isPresent()) {
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend Request", "friend request was accepted");
                }

                else {
                    MessageAlert.showErrorMessage(null, "already friends");
                }
            } catch (Exception e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        } else
            MessageAlert.showErrorMessage(null, "No user was selected");
    }

    public void handleRejectFriendRequest(ActionEvent actionEvent) {
        User selected = friendReqsTableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try{
                if(this.service.rejectFriendRequest(this.currentUser.getId(), selected.getId()).isPresent()) {
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend Request", "friend request was rejected");
                }

                else {
                    MessageAlert.showErrorMessage(null, "already friends");
                }

            } catch (Exception e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        } else
            MessageAlert.showErrorMessage(null, "No user was selected");
    }

    public void handleSendToOne(ActionEvent actionEvent) {
        String messageText = this.messageTextField.getText();
        User recipient = this.allUsersTableView.getSelectionModel().getSelectedItem();

        if(recipient == null){
            MessageAlert.showErrorMessage(null,"No user was selected!");
        }

        else{
            try {
                Long recipientID = recipient.getId();
                this.messageService.sendMessage(new Message(this.currentUser.getId(), List.of(recipientID), messageText, LocalDateTime.now()));
            } catch (Exception e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }


    }

    public void handleSendToMultiple(ActionEvent actionEvent) {
        this.loadUserSelectionView();
    }

    private void loadUserSelectionView(){
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ir/map/gr222/sem7/gui/views/userselection-view.fxml"));


            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Select the recipients");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UserSelectionController userSelectionController = loader.getController();
            userSelectionController.setService(service, messageService, messageTextField.getText(), this.currentUser, dialogStage);

            dialogStage.show();
            //   loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateMessages(MouseEvent mouseEvent) {
        User recipientUser = this.allUsersTableView.getSelectionModel().getSelectedItem();
        if(recipientUser != null){
            messagesModel.setAll(messageService.getUserMessasges(this.currentUser.getId(), recipientUser.getId()));
        }
    }
}
