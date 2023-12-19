package ir.map.gr222.sem7.repository.PagingRepository;
import java.util.stream.Stream;

public interface Page<E> {
    Pageable getPageable();

    Pageable nextPageable();

    Stream<E> getContent();


}
