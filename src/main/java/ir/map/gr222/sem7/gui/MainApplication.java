package ir.map.gr222.sem7.gui;

import ir.map.gr222.sem7.domain.validators.FriendshipValidator;
import ir.map.gr222.sem7.domain.validators.UserValidator;
import ir.map.gr222.sem7.repository.FriendshipDBRepository;
import ir.map.gr222.sem7.repository.UserDBRepository;
import ir.map.gr222.sem7.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Geani19011978", new UserValidator());
    FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Geani19011978", new FriendshipValidator());
    UserService userService = new UserService(userDBRepository, friendshipDBRepository);

    @Override
    public void start(Stage stage) throws IOException {

        initView(stage);
        stage.setWidth(700);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("/ir/map/gr222/sem7/gui/user-view.fxml"));
        AnchorPane userLayout = userLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        UserController userController = userLoader.getController();
        userController.setService(userService);

    }
}