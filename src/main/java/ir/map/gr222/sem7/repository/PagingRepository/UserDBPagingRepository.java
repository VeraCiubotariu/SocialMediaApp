package ir.map.gr222.sem7.repository.PagingRepository;

import ir.map.gr222.sem7.domain.PasswordEncryption;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.UserValidator;
import ir.map.gr222.sem7.repository.UserDBRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDBPagingRepository extends UserDBRepository implements PagingRepository<Long, User> {

    public UserDBPagingRepository(String url, String username, String password, UserValidator validator, PasswordEncryption passwordEncryption) {
        super(url, username, password, validator, passwordEncryption);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        List<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users order by username limit ? offset ?");
        ) {
            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, (pageable.getPageNumber()) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String usUsername = resultSet.getString("username");
                String usPassword = resultSet.getString("password");
                User user = new User(firstName, lastName, usUsername, usPassword);
                user.setId(id);
                users.add(user);

            }
            return new PageImplementation<>(pageable, users.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
