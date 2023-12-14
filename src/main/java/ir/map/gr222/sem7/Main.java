package ir.map.gr222.sem7;


import ir.map.gr222.sem7.domain.validators.FriendshipValidator;
import ir.map.gr222.sem7.domain.validators.UserValidator;
import ir.map.gr222.sem7.presentation.UI;
import ir.map.gr222.sem7.repository.FriendRequestDBRepository;
import ir.map.gr222.sem7.repository.FriendshipDBRepository;
import ir.map.gr222.sem7.repository.Repository;
import ir.map.gr222.sem7.repository.UserDBRepository;
import ir.map.gr222.sem7.service.UserService;

public class Main {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "Geani19011978";

        UserDBRepository userDBRepository = new UserDBRepository(url, username, password, new UserValidator());
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository(url, username, password, new FriendshipValidator());
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository(url, username, password, new FriendshipValidator());
        UserService service = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);

        UI ui = new UI(service);
        ui.runUI();
    }
}
