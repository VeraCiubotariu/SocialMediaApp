package ir.map.gr222.sem7.domain.validators;

import ir.map.gr222.sem7.domain.Friendship;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(Objects.equals(entity.getId().getLeft(), entity.getId().getRight())){
            throw new ValidationException("can't befriend self!");
        }
    }
}
