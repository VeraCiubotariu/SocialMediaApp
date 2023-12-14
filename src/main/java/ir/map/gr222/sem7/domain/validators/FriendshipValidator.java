package ir.map.gr222.sem7.domain.validators;

import ir.map.gr222.sem7.domain.Entity;
import ir.map.gr222.sem7.domain.Friendship;
import ir.map.gr222.sem7.domain.Tuple;

import java.util.Objects;

public class FriendshipValidator implements Validator<Entity<Tuple<Long,Long>>> {
    @Override
    public void validate(Entity<Tuple<Long,Long>> entity) throws ValidationException {
        if(Objects.equals(entity.getId().getLeft(), entity.getId().getRight())){
            throw new ValidationException("can't befriend self!");
        }
    }
}
