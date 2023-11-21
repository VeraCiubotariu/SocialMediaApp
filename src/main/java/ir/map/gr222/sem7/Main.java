package ir.map.gr222.sem7;


import ir.map.gr222.sem7.domain.validators.FriendshipValidator;
import ir.map.gr222.sem7.domain.validators.UserValidator;
import ir.map.gr222.sem7.presentation.UI;
import ir.map.gr222.sem7.repository.FriendshipDBRepository;
import ir.map.gr222.sem7.repository.UserDBRepository;
import ir.map.gr222.sem7.service.UserService;

public class Main {

    public static void main(String[] args) {

        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Geani19011978", new UserValidator());
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Geani19011978", new FriendshipValidator());
        UserService service = new UserService(userDBRepository, friendshipDBRepository);

        UI ui = new UI(service);
        ui.runUI();
    }
}
