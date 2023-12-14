package ir.map.gr222.sem7.domain.validators;

import ir.map.gr222.sem7.domain.Message;

import java.util.Objects;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity.getMessage().isEmpty()){
            throw new ValidationException("invalid message");
        }

        /*if(entity.getTo().contains(entity.getFrom())){
            throw new ValidationException("invalid recipient");
        }*/
    }
}
