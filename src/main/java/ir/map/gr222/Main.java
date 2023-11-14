package ir.map.gr222;


import ir.map.gr222.domain.Friendship;
import ir.map.gr222.domain.Tuple;
import ir.map.gr222.domain.User;
import ir.map.gr222.domain.validators.FriendshipValidator;
import ir.map.gr222.domain.validators.UserValidator;
import ir.map.gr222.presentation.UI;
import ir.map.gr222.repository.FriendshipDBRepository;
import ir.map.gr222.repository.InMemoryRepository;
import ir.map.gr222.repository.UserDBRepository;
import ir.map.gr222.service.UserService;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Geani19011978", new UserValidator());
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "Geani19011978", new FriendshipValidator());
        UserService service = new UserService(userDBRepository, friendshipDBRepository);

        UI ui = new UI(service);
        ui.runUI();
    }
}
