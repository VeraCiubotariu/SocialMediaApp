package ir.map.gr222.domain;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private final List<User> friends = new ArrayList<>();

    private static long generatedId = 0;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.setId(generatedId++);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriends() {
        return friends;
    }

    /**
     * adds the given user to this user's friends list
     * @param u must not be null
     * @return null, if the friend was added
     *         u, if the friend was already added
     * @throws IllegalArgumentException, if u is null
     */
    public User addFriend(User u){
        if(u == null){
            throw new IllegalArgumentException("null user!");
        }

        if(this.friends.contains(u)){
            return u;
        }

        else{
            this.friends.add(u);
            return null;
        }
    }

    /**
     * removes the given user from this user's friends list
     * @param u must not be null
     * @return u, if the user was deleted successfully from the list
     *         null, otherwise
     * @throws IllegalArgumentException, if u is null
     */
    public User deleteFriend(User u){
        if(u == null){
            throw new IllegalArgumentException("null user!");
        }

        if(!this.friends.contains(u)){
            return null;
        }

        else{
            this.friends.remove(u);
            return u;
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
            //    ", friends=" + friends.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User that)) return false;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}