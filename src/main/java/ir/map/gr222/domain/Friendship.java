package ir.map.gr222.domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime friendsFrom;

    public Friendship(User u1, User u2) {
        Tuple<Long, Long> id = new Tuple<>(Long.min(u1.getId(), u2.getId()), Long.max(u1.getId(), u2.getId()));
        this.setId(id);

        this.friendsFrom = LocalDateTime.now();
    }

    public Friendship(Tuple<Long, Long> id, LocalDateTime date) {
        this.setId(id);
        this.friendsFrom = date;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getFriendsFrom() {
        return friendsFrom;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "date=" + friendsFrom +
                ", id=" + id +
                '}';
    }
}
