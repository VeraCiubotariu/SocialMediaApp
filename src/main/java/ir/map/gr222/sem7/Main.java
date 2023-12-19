package ir.map.gr222.sem7;


import ir.map.gr222.sem7.domain.PasswordEncryption;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.FriendshipValidator;
import ir.map.gr222.sem7.domain.validators.UserValidator;
import ir.map.gr222.sem7.repository.FriendRequestDBRepository;
import ir.map.gr222.sem7.repository.FriendshipDBRepository;
import ir.map.gr222.sem7.repository.PagingRepository.*;
import ir.map.gr222.sem7.repository.UserDBRepository;
import ir.map.gr222.sem7.service.UserService;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "Geani19011978";

        UserDBPagingRepository userDBRepository = new UserDBPagingRepository(url, username, password, new UserValidator(), new PasswordEncryption());
        FriendshipDBPagingRepository friendshipDBRepository = new FriendshipDBPagingRepository(url, username, password, new FriendshipValidator());
        FriendRequestDBPagingRepository friendRequestDBRepository = new FriendRequestDBPagingRepository(url, username, password, new FriendshipValidator());
        UserService service = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);

        /*UI ui = new UI(service);
        ui.runUI();*/

       /* UserDBPagingRepository userDBPagingRepository = new UserDBPagingRepository(url, username, password, new UserValidator());
        Pageable pageable = new PageableImplementation(3, 5);
        Page<User> page = userDBPagingRepository.findAll(pageable);

        page.getContent().forEach(System.out::println);*/

        PasswordEncryption passwordEncryption = new PasswordEncryption();
        System.out.println(passwordEncryption.encrypt("a"));
    }
}
