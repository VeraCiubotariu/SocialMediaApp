package ir.map.gr222;


import ir.map.gr222.domain.Friendship;
import ir.map.gr222.domain.Tuple;
import ir.map.gr222.domain.User;
import ir.map.gr222.domain.validators.FriendshipValidator;
import ir.map.gr222.domain.validators.UserValidator;
import ir.map.gr222.presentation.UI;
import ir.map.gr222.repository.InMemoryRepository;
import ir.map.gr222.service.UserService;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        InMemoryRepository<Long, User> userRepo=new InMemoryRepository<>(new UserValidator());
        InMemoryRepository<Tuple<Long, Long>, Friendship> friendshipRepo = new InMemoryRepository<>(new FriendshipValidator());
        UserService serv = new UserService(userRepo, friendshipRepo);

    /*    User u1=new User("u1FirstName", "u1LastName");
        User u2=new User("u2FirstName", "u2LastName");
        User u3=new User("u3FirstName", "u3LastName");
        User u4=new User("u4FirstName", "u4LastName");
        User u5=new User("u5FirstName", "u5LastName");
        User u6=new User("u6FirstName", "u6LastName");
        User u7=new User("u7FirstName", "u7LastName");

        serv.addUser(u1);
        serv.addUser(u2);
        serv.addUser(u3);
        serv.addUser(u4);
        serv.addUser(u5);
        serv.addUser(u6);
        serv.addUser(u7);
        serv.deleteUser(2L);
        serv.addFriend(0L, 4L);
        serv.addFriend(1L, 4L);
        serv.addFriend(0L, 6L);
    //    serv.deleteFriend(4L,0L);
    //    serv.deleteUser(0L);

        Iterable<User> users = serv.getAllUsers();
        users.forEach(System.out::println);

        Iterable<Friendship> friends = friendshipRepo.findAll();
        friends.forEach(System.out::println);

        System.out.println("Numar comunitati: " + serv.getCommunitiesNumber());
        List<User> community = serv.mostActiveCommunity();
        community.forEach(System.out::println);*/

        UI ui = new UI(serv);
        ui.runUI();
    }
}
