package ir.map.gr222.sem7.domain;

public class FriendRequest extends Entity<Tuple<Long,Long>> {
    private String status;

    public FriendRequest(User u1, User u2) {
        Tuple<Long, Long> id = new Tuple<>(u1.getId(), u2.getId());
        this.setId(id);

        this.status = "pending";
    }

    public FriendRequest(User u1, User u2, String status) {
        Tuple<Long, Long> id = new Tuple<>(u1.getId(), u2.getId());
        this.setId(id);

        this.status = status;
    }

    public FriendRequest(Long u1ID, Long u2ID, String status) {
        Tuple<Long, Long> id = new Tuple<>(u1ID, u2ID);
        this.setId(id);

        this.status = status;
    }

    public FriendRequest(Tuple<Long,Long> id, String status) {
        this.setId(id);
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
