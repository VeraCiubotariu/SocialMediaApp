import ir.map.gr222.domain.Friendship;
import ir.map.gr222.domain.Tuple;
import ir.map.gr222.domain.User;
import ir.map.gr222.domain.validators.FriendshipValidator;
import ir.map.gr222.domain.validators.UserValidator;
import ir.map.gr222.domain.validators.ValidationException;
import ir.map.gr222.repository.InMemoryRepository;
import ir.map.gr222.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UserServiceTests {
    private InMemoryRepository<Long, User> userRepo=new InMemoryRepository<>(new UserValidator());
    private InMemoryRepository<Tuple<Long, Long>, Friendship> friendshipRepo = new InMemoryRepository<>(new FriendshipValidator());
    private UserService serv = new UserService(userRepo, friendshipRepo);
    private final User u1=new User("u1FirstName", "u1LastName");
    private final User u2=new User("u2FirstName", "u2LastName");
    private final User u3=new User("u3FirstName", "u3LastName");
    private final User u4=new User("u4FirstName", "u4LastName");
    private final User u5=new User("u5FirstName", "u5LastName");
    private final User u6=new User("u6FirstName", "u6LastName");
    private final User u7=new User("u7FirstName", "u7LastName");
    private final User u8 = new User("u8FirstName", "u8LastName");
    private final User invalidUser = new User("", "");

    @Test
    public void addUser(){
        serv.addUser(u1);
        serv.addUser(u2);
        serv.addUser(u3);

        Assertions.assertEquals(3, this.userRepo.size());

        Assertions.assertThrows(IllegalArgumentException.class, () ->{
            this.serv.addUser(null);
        });

        Assertions.assertThrows(ValidationException.class, () -> {
            this.serv.addUser(invalidUser);
        });

        Assertions.assertEquals(u1, this.serv.addUser(u1).get());
    }

    @Test
    public void deleteUser(){
        serv.addUser(u1);
        serv.addUser(u2);
        serv.addUser(u3);
        serv.addUser(u4);
        serv.addUser(u5);
        serv.addUser(u6);
        serv.addUser(u7);

        serv.addFriend(u1.getId(), u5.getId());
        serv.addFriend(u2.getId(), u5.getId());
        serv.addFriend(u1.getId(), u7.getId());

        Assertions.assertEquals(7, this.userRepo.size());
        Assertions.assertEquals(2, u5.getFriends().size());
        Assertions.assertEquals(1, u7.getFriends().size());

        Assertions.assertEquals(u1, serv.deleteUser(u1.getId()));

        Assertions.assertEquals(6, this.userRepo.size());
        Assertions.assertEquals(1, u5.getFriends().size());
        Assertions.assertEquals(0, u7.getFriends().size());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.serv.deleteUser(null);
        });

        Assertions.assertNull(this.serv.deleteUser(99L));
    }

    @Test
    public void addFriend(){
        serv.addUser(u1);
        serv.addUser(u2);
        serv.addUser(u3);

        serv.addFriend(u1.getId(), u2.getId());
        serv.addFriend(u3.getId(), u2.getId());

        Assertions.assertEquals(1, u1.getFriends().size());
        Assertions.assertEquals(2, u2.getFriends().size());
        Assertions.assertEquals(1, u3.getFriends().size());
        Assertions.assertEquals(2, friendshipRepo.size());

        Assertions.assertEquals(u1.getId(), serv.addFriend(u2.getId(), u1.getId()));
        Assertions.assertEquals(99L, serv.addFriend(u1.getId(), 99L));

        Assertions.assertEquals(2, friendshipRepo.size());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            serv.addFriend(u1.getId(), null);
        });

    }

    @Test
    public void getUsers(){
        serv.addUser(u1);
        serv.addUser(u2);
        serv.addUser(u3);

        AtomicInteger usersNumber = new AtomicInteger();
        serv.getAllUsers().forEach(x -> {
            usersNumber.addAndGet(1);});
        Assertions.assertEquals(3, usersNumber.get());

        Assertions.assertSame(u1, serv.getUser(u1.getId()).get());
        Assertions.assertTrue(serv.getUser(90129L).isEmpty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           serv.getUser(null);
        });
    }

    @Test
    public void deleteFriend(){
        serv.addUser(u1);
        serv.addUser(u2);
        serv.addUser(u3);

        serv.addFriend(u1.getId(), u2.getId());
        serv.addFriend(u3.getId(), u2.getId());

        serv.deleteFriend(u2.getId(), u1.getId());
        Assertions.assertEquals(1, u3.getFriends().size());

        Assertions.assertNull(serv.deleteFriend(99L, u1.getId()));
        Assertions.assertEquals(1, friendshipRepo.size());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           serv.deleteFriend(null, u1.getId());
        });
    }

    @Test
    public void communityMethods(){
        serv.addUser(u1);
        serv.addUser(u2);
        serv.addUser(u3);
        serv.addUser(u4);
        serv.addUser(u5);
        serv.addUser(u6);
        serv.addUser(u7);
        serv.addUser(u8);

        Assertions.assertEquals(8, serv.getCommunitiesNumber());
        Assertions.assertEquals(1, serv.mostActiveCommunity().size());

        serv.addFriend(u1.getId(), u7.getId());
        Assertions.assertEquals(7, serv.getCommunitiesNumber());

        serv.addFriend(u6.getId(), u7.getId());
        serv.addFriend(u5.getId(), u7.getId());
        Assertions.assertEquals(5, serv.getCommunitiesNumber());

        serv.addFriend(u2.getId(), u3.getId());

        Assertions.assertEquals(4, serv.getCommunitiesNumber());
        List<User> community = serv.mostActiveCommunity();
        Assertions.assertEquals(4, community.size());
        Assertions.assertTrue(community.contains(u7));

        serv.addFriend(u4.getId(), u3.getId());
        serv.addFriend(u4.getId(), u8.getId());

        Assertions.assertEquals(2, serv.getCommunitiesNumber());
        community = serv.mostActiveCommunity();
        Assertions.assertEquals(4, community.size());
        Assertions.assertTrue(community.contains(u8));
    }
}
