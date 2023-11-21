package ir.map.gr222.sem7.repository;

import ir.map.gr222.sem7.domain.Friendship;
import ir.map.gr222.sem7.domain.Tuple;
import ir.map.gr222.sem7.domain.validators.FriendshipValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FriendshipDBRepository implements Repository<Tuple<Long, Long>, Friendship> {
    private final String url;
    private final String username;
    private final String password;
    private final FriendshipValidator validator;

    public FriendshipDBRepository(String url, String username, String password, FriendshipValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<Friendship> findOne(Tuple<Long, Long> id) {
        if(id == null){
            throw  new IllegalArgumentException("ID must not be null!");
        }

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from friendships where friend1_id = ? and friend2_id = ?")
        ){
            long min = Long.min(id.getLeft(), id.getRight());
            long max = Long.max(id.getLeft(), id.getRight());

            statement.setInt(1, Math.toIntExact(min));
            statement.setInt(2, Math.toIntExact(max));

            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Timestamp date = resultSet.getTimestamp("date");
                Friendship friendship = new Friendship(id, date.toLocalDateTime());

                return Optional.of(friendship);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id_left = resultSet.getLong("friend1_id");
                Long id_right = resultSet.getLong("friend2_id");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();
                Friendship friendship = new Friendship(new Tuple<>(id_left, id_right), date);
                friendships.add(friendship);
            }
            return friendships;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> save(Friendship entity) {
        if(entity == null){
            throw new IllegalArgumentException("entity must not be null!");
        }

        validator.validate(entity);

        String insertSQL = "insert into friendships(friend1_id, friend2_id, date) values (?, ?, ?)";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(insertSQL)){
            statement.setInt(1, Math.toIntExact(entity.getId().getLeft()));
            statement.setInt(2, Math.toIntExact(entity.getId().getRight()));
            statement.setTimestamp(3, Timestamp.valueOf(entity.getFriendsFrom()));

            int response = statement.executeUpdate();
            return response == 0? Optional.of(entity) :Optional.empty();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(Tuple<Long, Long> id) {
        if(id == null){
            throw  new IllegalArgumentException("ID must not be null!");
        }

        Optional<Friendship> friendship = this.findOne(id);
        if(friendship.isEmpty()){
            return Optional.empty();
        }

        String deleteSQL = "delete from friendships where friend1_id = ? and friend2_id = ?";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(deleteSQL)){
            statement.setInt(1, Math.toIntExact(id.getLeft()));
            statement.setInt(2, Math.toIntExact(id.getRight()));

            int response = statement.executeUpdate();
            return response == 0? Optional.empty() : friendship;
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> update(Friendship entity) {
        if(entity == null){
            throw new IllegalArgumentException("entity must not be null!");
        }

        validator.validate(entity);

        String deleteSQL = "update friendships set date = ? where friend1_id = ? and friend2_id = ?";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(deleteSQL)){
            statement.setTimestamp(1, Timestamp.valueOf(entity.getFriendsFrom()));
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
             PreparedStatement statement = connection.prepareStatement("select count(*) as nr_friendships from friendships");
             ResultSet resultSet = statement.executeQuery()
        ) {

            resultSet.next();
            return resultSet.getInt("nr_friendships");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
