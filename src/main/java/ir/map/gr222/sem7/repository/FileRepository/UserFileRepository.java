package ir.map.gr222.sem7.repository.FileRepository;


import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.validators.Validator;
import ir.map.gr222.sem7.repository.FileRepository.AbstractFileRepository;

import java.util.List;

public class UserFileRepository extends AbstractFileRepository<Long, User> {

    public UserFileRepository(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    @Override
    public User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1),attributes.get(2), attributes.get(3), attributes.get(4));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }


    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
