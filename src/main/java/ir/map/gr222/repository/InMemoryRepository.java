package ir.map.gr222.repository;

import ir.map.gr222.domain.validators.ValidationException;
import ir.map.gr222.domain.validators.Validator;
import ir.map.gr222.domain.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {
    private Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return Optional.of(entity);
        }
        else entities.put(entity.getId(),entity);
        return Optional.empty();
    }

    @Override
    public Optional<E> delete(ID id) {
        if(id == null){
            throw new IllegalArgumentException("entity must not be null!");
        }

        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) throws ValidationException {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return Optional.empty();
        }
        return Optional.of(entity);

    }

    public int size(){
        return this.entities.size();
    }

}
