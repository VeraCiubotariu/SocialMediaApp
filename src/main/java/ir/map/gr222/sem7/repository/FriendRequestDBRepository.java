package ir.map.gr222.sem7.repository;

import ir.map.gr222.sem7.domain.FriendRequest;
import ir.map.gr222.sem7.domain.Friendship;
import ir.map.gr222.sem7.domain.Tuple;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.FriendshipValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class FriendRequestDBRepository implements Repository<Tuple<Long, Long>, FriendRequest> {
    private final String url;
    private final String username;
    private final String password;
    private final FriendshipValidator validator;

    public FriendRequestDBRepository(String url, String username, String password, FriendshipValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<FriendRequest> findOne(Tuple<Long, Long> id) {
        if(id == null){
            throw  new IllegalArgumentException("ID must not be null!");
        }

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friend_requests where friend1_id = ? and friend2_id = ?")
        ){
            statement.setInt(1, Math.toIntExact(id.getLeft()));
            statement.setInt(2, Math.toIntExact(id.getRight()));

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String status = resultSet.getString("status");
                FriendRequest friendRequest = new FriendRequest(id, status);

                return Optional.of(friendRequest);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<FriendRequest> findAll() {
        List<FriendRequest> friendRequests = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friend_requests order by status");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id_left = resultSet.getLong("friend1_id");
                Long id_right = resultSet.getLong("friend2_id");
                String status = resultSet.getString("status");
                FriendRequest friendRequest = new FriendRequest(new Tuple<>(id_left, id_right), status);
                friendRequests.add(friendRequest);
            }
            return friendRequests;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUserPendingFriendRequests(Long userID){
        List<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select f.friend1_id as id, u.first_name, u.last_name, u.username, u.password from friend_requests as f " +
                     "inner join users u on f.friend1_id = u.id " +
                     "where f.friend2_id = " + userID + " and f.status = 'pending' " +
                     "order by u.username;");
             ResultSet resultSet = statement.executeQuery()
        ) {

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
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> save(FriendRequest entity) {
        if(entity == null){
            throw new IllegalArgumentException("entity must not be null!");
        }

        validator.validate(entity);

        String insertSQL = "insert into friend_requests(friend1_id, friend2_id, status) values (?, ?, ?)";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(insertSQL)){
            statement.setInt(1, Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(2, Math.toIntExact(entity.getId().getRight()));
            statement.setString(3, entity.getStatus());

            int response = statement.executeUpdate();
            return response == 0? Optional.of(entity) :Optional.empty();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> delete(Tuple<Long, Long> id) {
        if(id == null){
            throw  new IllegalArgumentException("ID must not be null!");
        }

        Optional<FriendRequest> friendRequest = this.findOne(id);
        if(friendRequest.isEmpty()){
            return Optional.empty();
        }

        String deleteSQL = "delete from friend_requests where friend1_id = ? and friend2_id = ?";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(deleteSQL)){
            statement.setInt(1, Math.toIntExact(id.getLeft()));
            statement.setInt(2, Math.toIntExact(id.getRight()));

            int response = statement.executeUpdate();
            return response == 0? Optional.empty() : friendRequest;
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<FriendRequest> update(FriendRequest entity) {
        if(entity == null){
            throw new IllegalArgumentException("entity must not be null!");
        }

        validator.validate(entity);

        String deleteSQL = "update friend_requests set status = ? where friend1_id = ? and friend2_id = ?";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(deleteSQL)){
            statement.setString(1, entity.getStatus());
            statement.setInt(2, Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(3, Math.toIntExact(entity.getId().getRight()));

            int response = statement.executeUpdate();
            return response == 0? Optional.empty() : Optional.of(entity);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count(*) as nr from friend_requests");
             ResultSet resultSet = statement.executeQuery()
        ) {

            resultSet.next();
            return resultSet.getInt("nr");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
