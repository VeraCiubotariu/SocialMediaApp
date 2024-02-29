package ir.map.gr222.sem7.repository.PagingRepository;

import ir.map.gr222.sem7.domain.Message;
import ir.map.gr222.sem7.domain.validators.MessageValidator;
import ir.map.gr222.sem7.repository.MessageDBRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDBPagingRepository extends MessageDBRepository {

    public MessageDBPagingRepository(String url, String username, String password, MessageValidator messageValidator) {
        super(url, username, password, messageValidator);
    }

    public Page<Message> findAllByUser(Pageable pageable, Long userFromID, Long userToID){
        List<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from messages m " +
                     "inner join sent_messages sm on m.id = sm.message_id " +
                     "where m.user_from = ? and sm.recipient_id = ? " +
                     "or m.user_from = ? and sm.recipient_id = ? " +
                     "order by m.date_sent desc limit ? offset ?");
        ) {
            statement.setInt(1, Math.toIntExact(userFromID));
            statement.setInt(2, Math.toIntExact(userToID));
            statement.setInt(3, Math.toIntExact(userToID));
            statement.setInt(4, Math.toIntExact(userFromID));
            statement.setInt(5, pageable.getPageSize());
            statement.setInt(6, (pageable.getPageNumber()) * pageable.getPageSize());
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

            List<Message> result = new ArrayList<>();
            for(int i = messages.size()-1;i>=0;i--){
                result.add(messages.get(i));
            }

            return new PageImplementation<>(pageable, result.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
