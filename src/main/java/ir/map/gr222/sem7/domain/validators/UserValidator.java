package ir.map.gr222.sem7.domain.validators;


import ir.map.gr222.sem7.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if(entity.getFirstName().isEmpty()){
            throw new ValidationException("invalid first name!");
        }

        if(entity.getLastName().isEmpty()){
            throw new ValidationException("invalid last name!");
        }

        if(entity.getUsername().isEmpty()){
            throw new ValidationException("invalid username!");
        }

        if(entity.getPassword().isEmpty()){
            throw new ValidationException("invalid password!");
        }
    }
}

