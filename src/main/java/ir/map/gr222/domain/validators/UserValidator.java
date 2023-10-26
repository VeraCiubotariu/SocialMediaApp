package ir.map.gr222.domain.validators;


import ir.map.gr222.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        //TODO: implement method validate
        if(entity.getFirstName().isEmpty()){
            throw new ValidationException("invalid first name!");
        }

        if(entity.getLastName().isEmpty()){
            throw new ValidationException("invalid last name!");
        }
    }
}

