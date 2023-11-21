package ir.map.gr222.sem7.repository;

import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.UserValidator;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDBRepository implements Repository<Long, User> {

    private final String url;
    private final String username;
    private final String password;
    private final UserValidator validator;

    public UserDBRepository(String url, String username, String password, UserValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Optional<User> findOne(Long longID) {
        if(longID == null){
            throw  new IllegalArgumentException("ID must not be null!");
        }

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users where id = ?")
            ){
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User u = new User(firstName, lastName);
                u.setId(longID);
                return Optional.of(u);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users order by first_name, last_name");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                User user=new User(firstName,lastName);
                user.setId(id);
                users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> save(User entity) {
        if(entity == null){
            throw new IllegalArgumentException("entity must not be null!");
        }

        validator.validate(entity);

        String insertSQL = "insert into users(first_name, last_name) values (?, ?)";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(insertSQL)){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());

            int response = statement.executeUpdate();
            return response == 0? Optional.of(entity) :Optional.empty();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long longID) {
        if(longID == null){
            throw  new IllegalArgumentException("ID must not be null!");
        }

        Optional<User> u = this.findOne(longID);
        if(u.isEmpty()){
            return Optional.empty();
        }

        String deleteSQL = "delete from users where id = ?";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(deleteSQL)){
            statement.setInt(1, Math.toIntExact(longID));

            int response = statement.executeUpdate();
            return response == 0? Optional.empty() : u;
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> update(User entity) {
        if(entity == null){
            throw new IllegalArgumentException("entity must not be null!");
        }

        validator.validate(entity);

        String deleteSQL = "update users set first_name = ?, last_name = ? where id = ?";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(deleteSQL)){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setInt(3, Math.toIntExact(entity.getId()));

            int response = statement.executeUpdate();
            return response == 0? Optional.empty() : Optional.of(entity);
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int size(){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count(*) as nr_users from users");
             ResultSet resultSet = statement.executeQuery()
        ) {

            resultSet.next();
            return resultSet.getInt("nr_users");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
