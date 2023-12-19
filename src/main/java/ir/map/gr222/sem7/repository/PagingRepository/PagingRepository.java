package ir.map.gr222.sem7.repository.PagingRepository;


import ir.map.gr222.sem7.domain.Entity;
import ir.map.gr222.sem7.repository.Repository;

public interface PagingRepository<ID,
        E extends Entity<ID>>
        extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);
}
