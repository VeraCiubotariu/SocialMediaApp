package ir.map.gr222.sem7.repository.PagingRepository;

import ir.map.gr222.sem7.domain.Friendship;
import ir.map.gr222.sem7.domain.Tuple;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.FriendshipValidator;
import ir.map.gr222.sem7.repository.FriendshipDBRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDBPagingRepository extends FriendshipDBRepository implements PagingRepository<Tuple<Long, Long>, Friendship> {

    public FriendshipDBPagingRepository(String url, String username, String password, FriendshipValidator validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        List<Friendship> friendships = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships limit ? offset ? order by date");
        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, (pageable.getPageNumber() - 1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                Long id_left = resultSet.getLong("friend1_id");
                Long id_right = resultSet.getLong("friend2_id");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Friendship friendship = new Friendship(new Tuple<>(id_left, id_right), date);
                friendships.add(friendship);
            }
            return new PageImplementation<>(pageable, friendships.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<User> getAllFriends(Pageable pageable, Long userID) {
        List<User> friends = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select distinct friendships.friend2_id as id, u.first_name, u.last_name, u.username, u.password from friendships " +
                     "inner join public.users u on u.id = friendships.friend2_id " +
                     "where friendships.friend1_id =  " + userID + " " +
                     "union " +
                     "select distinct friendships.friend1_id as id, u.first_name, u.last_name, u.username, u.password from friendships " +
                     "inner join public.users u on u.id = friendships.friend1_id " +
                     "where friendships.friend2_id = " + userID +
                     " order by username limit ? offset ?");
        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, (pageable.getPageNumber()) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String usernameU = resultSet.getString("username");
                String passwordU = resultSet.getString("password");
                User user = new User(id, firstName, lastName, usernameU, passwordU);
                friends.add(user);
            }
            return new PageImplementation<>(pageable, friends.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<User> getNonFriendUsers(Pageable pageable, Long userID){
        List<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users where id != " + userID + " except (" +
                     "select distinct friendships.friend2_id as id, u.first_name, u.last_name, u.username, u.password from friendships " +
                     "inner join public.users u on u.id = friendships.friend2_id " +
                     "where friendships.friend1_id =  " + userID +
                     " union " +
                     "select distinct friendships.friend1_id as id, u.first_name, u.last_name, u.username, u.password from friendships " +
                     "inner join public.users u on u.id = friendships.friend1_id " +
                     "where friendships.friend2_id = " + userID + " ) order by username limit ? offset ?");
        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, (pageable.getPageNumber()) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String usernameU = resultSet.getString("username");
                String passwordU = resultSet.getString("password");
                User user = new User(id, firstName, lastName, usernameU, passwordU);
                users.add(user);
            }
            return new PageImplementation<>(pageable, users.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
