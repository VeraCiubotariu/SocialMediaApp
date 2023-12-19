package ir.map.gr222.sem7.repository.PagingRepository;

import ir.map.gr222.sem7.domain.FriendRequest;
import ir.map.gr222.sem7.domain.Tuple;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.FriendshipValidator;
import ir.map.gr222.sem7.repository.FriendRequestDBRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDBPagingRepository extends FriendRequestDBRepository implements PagingRepository<Tuple<Long, Long>, FriendRequest> {
    public FriendRequestDBPagingRepository(String url, String username, String password, FriendshipValidator validator) {
        super(url, username, password, validator);
    }

    @Override
    public Page<FriendRequest> findAll(Pageable pageable) {
        List<FriendRequest> friendRequests = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friend_requests limit ? offset ? order by status");
        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, (pageable.getPageNumber() - 1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                Long id_left = resultSet.getLong("friend1_id");
                Long id_right = resultSet.getLong("friend2_id");
                String status = resultSet.getString("status");
                FriendRequest friendRequest = new FriendRequest(new Tuple<>(id_left, id_right), status);
                friendRequests.add(friendRequest);
            }
            return new PageImplementation<>(pageable, friendRequests.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Page<User> getAllUserPendingFriendRequests(Pageable pageable, Long userID){
        List<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select f.friend1_id as id, u.first_name, u.last_name, u.username, u.password from friend_requests as f " +
                     "inner join users u on f.friend1_id = u.id " +
                     "where f.friend2_id = " + userID + " and f.status = 'pending' " +
                     "order by u.username " +
                     "limit ? offset ?");
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
