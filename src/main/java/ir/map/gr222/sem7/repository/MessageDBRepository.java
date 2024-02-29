package ir.map.gr222.sem7.repository;

import ir.map.gr222.sem7.domain.Message;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.MessageValidator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDBRepository implements Repository<Long, Message> {
    protected String url;
    protected String username;
    protected String password;
    private final MessageValidator messageValidator;

    public MessageDBRepository(String url, String username, String password, MessageValidator messageValidator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.messageValidator = messageValidator;
    }

    @Override
    public Optional<Message> findOne(Long longID) {
        if(longID == null){
            throw  new IllegalArgumentException("ID must not be null!");
        }

        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from messages where id = ?")
        ){
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Long from = resultSet.getLong("user_from");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date_sent").toLocalDateTime();

                Message m = new Message(longID, from, List.of(), message, date);

                PreparedStatement recipientsStatement = connection.prepareStatement("select s.recipient_id from messages m " +
                        "inner join sent_messages s on m.id = s.message_id " +
                        "where m.id = ?");
                recipientsStatement.setInt(1, Math.toIntExact(longID));
                ResultSet recipientResultSet = recipientsStatement.executeQuery();

                while(recipientResultSet.next()){
                    Long to = resultSet.getLong("recipient_id");
                    m.addRecipient(to);
                }

                return Optional.of(m);
            }

        } catch(SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from messages order by id");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Message m = this.findOne(resultSet.getLong("id")).get();
                messages.add(m);

            }
            return messages;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> findAllByUser(Long userFromID, Long userToID){
        List<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from messages m " +
                     "inner join sent_messages sm on m.id = sm.message_id " +
                     "where m.user_from = ? and sm.recipient_id = ? " +
                     "or m.user_from = ? and sm.recipient_id = ? " +
                     "order by m.date_sent");
        ) {
            statement.setInt(1, Math.toIntExact(userFromID));
            statement.setInt(2, Math.toIntExact(userToID));
            statement.setInt(3, Math.toIntExact(userToID));
            statement.setInt(4, Math.toIntExact(userFromID));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next())
            {
            //    Message m = this.findOne(resultSet.getLong("id")).get();
                Long from = resultSet.getLong("user_from");
                Long to = resultSet.getLong("recipient_id");
                String message = resultSet.getString("message");
                LocalDateTime date = resultSet.getTimestamp("date_sent").toLocalDateTime();
                Message m = new Message(from, List.of(to), message, date);
                messages.add(m);

            }
            return messages;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        if(entity == null){
            throw new IllegalArgumentException("entity must not be null!");
        }

        messageValidator.validate(entity);

        String insertSQL = "insert into messages(user_from, message, date_sent) values (?, ?, ?)";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(insertSQL)){
            statement.setInt(1, Math.toIntExact(entity.getFrom()));
            statement.setString(2, entity.getMessage());
            statement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            int response = statement.executeUpdate();

            PreparedStatement getIdStatement = con.prepareStatement("select id from messages order by id desc limit 1");
            ResultSet idResultSet = getIdStatement.executeQuery();

            Long id = 0L;
            if(idResultSet.next()){
                id = idResultSet.getLong("id");
            }


            insertSQL = "insert into sent_messages(message_id, recipient_id) values (?,?)";

            for(Long u: entity.getTo()){
                PreparedStatement newStatement = con.prepareStatement(insertSQL);
                newStatement.setInt(1, Math.toIntExact(id));
                newStatement.setInt(2, Math.toIntExact(u));

                int newResponse = newStatement.executeUpdate();
                if(newResponse == 0){
                    return Optional.of(entity);
                }
            }

            return response == 0? Optional.of(entity) :Optional.empty();
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> delete(Long longID) {
        if(longID == null){
            throw  new IllegalArgumentException("ID must not be null!");
        }

        Optional<Message> m = this.findOne(longID);
        if(m.isEmpty()){
            return Optional.empty();
        }

        String deleteSQL = "delete from messages where id = ?";
        try(Connection con = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = con.prepareStatement(deleteSQL)){
            statement.setInt(1, Math.toIntExact(longID));

            int response = statement.executeUpdate();
            return response == 0? Optional.empty() : m;
        } catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    @Override
    public int size() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count(*) as nr from messages");
             ResultSet resultSet = statement.executeQuery()
        ) {

            resultSet.next();
            return resultSet.getInt("nr");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
