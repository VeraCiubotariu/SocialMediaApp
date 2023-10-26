package ir.map.gr222.domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;

    public Friendship(User u1, User u2) {
        Tuple<Long, Long> id = new Tuple<>(Long.min(u1.getId(), u2.getId()), Long.max(u1.getId(), u2.getId()));
        this.setId(id);

        this.date = LocalDateTime.now();
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "date=" + date +
                ", id=" + id +
                '}';
    }
}
