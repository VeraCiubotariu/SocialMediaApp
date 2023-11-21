package ir.map.gr222.sem7.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}