package ir.map.gr222.sem7.gui;

import ir.map.gr222.sem7.domain.PasswordEncryption;
import ir.map.gr222.sem7.domain.validators.FriendshipValidator;
import ir.map.gr222.sem7.domain.validators.MessageValidator;
import ir.map.gr222.sem7.domain.validators.UserValidator;
import ir.map.gr222.sem7.repository.*;
import ir.map.gr222.sem7.repository.PagingRepository.FriendRequestDBPagingRepository;
import ir.map.gr222.sem7.repository.PagingRepository.FriendshipDBPagingRepository;
import ir.map.gr222.sem7.repository.PagingRepository.MessageDBPagingRepository;
import ir.map.gr222.sem7.repository.PagingRepository.UserDBPagingRepository;
import ir.map.gr222.sem7.service.MessageService;
import ir.map.gr222.sem7.service.UserService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    String url = "jdbc:postgresql://localhost:5432/socialnetwork";
    String username = "postgres";
    String password = "Geani19011978";

    UserDBPagingRepository userDBRepository = new UserDBPagingRepository(url, username, password, new UserValidator(), new PasswordEncryption());
    MessageDBPagingRepository messageDBRepository = new MessageDBPagingRepository(url, username, password, new MessageValidator());
    FriendshipDBPagingRepository friendshipDBRepository = new FriendshipDBPagingRepository(url, username, password, new FriendshipValidator());
    FriendRequestDBPagingRepository friendRequestDBRepository = new FriendRequestDBPagingRepository(url, username, password, new FriendshipValidator());
    UserService userService = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
    MessageService messageService = new MessageService(userDBRepository, messageDBRepository);

    @Override
    public void start(Stage stage) throws IOException {

        initView(stage);
        stage.setWidth(570);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/ir/map/gr222/sem7/gui/views/login-view.fxml"));
        AnchorPane userLayout = loginLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        LoginController loginController = loginLoader.getController();
        loginController.setService(userService, primaryStage, messageService);

        System.out.println("Am afisat!");

    }
}