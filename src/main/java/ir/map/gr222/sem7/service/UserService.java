package ir.map.gr222.sem7.service;

import ir.map.gr222.sem7.domain.FriendRequest;
import ir.map.gr222.sem7.domain.Friendship;
import ir.map.gr222.sem7.domain.Tuple;
import ir.map.gr222.sem7.domain.User;
import ir.map.gr222.sem7.domain.exceptions.ServiceException;
import ir.map.gr222.sem7.domain.validators.ValidationException;
import ir.map.gr222.sem7.repository.FriendRequestDBRepository;
import ir.map.gr222.sem7.repository.FriendshipDBRepository;
import ir.map.gr222.sem7.repository.UserDBRepository;
import ir.map.gr222.sem7.utils.events.ChangeEventType;
import ir.map.gr222.sem7.utils.events.UserChangeEvent;

import java.time.Month;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import ir.map.gr222.sem7.utils.observer.Observable;
import ir.map.gr222.sem7.utils.observer.Observer;

public class UserService implements Observable<UserChangeEvent> {
    private final UserDBRepository userRepo;
    private final FriendshipDBRepository friendshipRepo;
    private final FriendRequestDBRepository friendRequestRepo;
    private List<Observer<UserChangeEvent>> observers=new ArrayList<>();

    public UserService(UserDBRepository repo, FriendshipDBRepository friendshipRepo, FriendRequestDBRepository friendRequestRepo){
        this.userRepo = repo;
        this.friendshipRepo = friendshipRepo;
        this.friendRequestRepo = friendRequestRepo;
    }

    /**
     * adds the user to the network
     *
     * @param u must not be null
     * @return null, if the user was added, u otherwise
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.
     */
    public Optional<User> addUser(User u) throws RuntimeException {
        Optional<User> user =  this.userRepo.save(u);
        if(user.isEmpty()){
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));
        }
        return user;
    }

    public Optional<User> findUserByUsername(String username){
        return this.userRepo.findOneByUsername(username);
    }

    /**
     * Checks if the login credentials are correct
     * @param username must not be null
     * @param password must not be null
     * @return the found user, if the credentials match
     * @throws IllegalArgumentException if the username or password is null
     * @throws ServiceException if the user with the given username doesn't exist
     *                          or if the password doesn't match
     */
    public User checkLogin(String username, String password){
        if(password == null){
            throw new IllegalArgumentException("password must not be null");
        }

        Optional<User> user = this.findUserByUsername(username);

        if(user.isPresent()){
            if(password.equals(user.get().getPassword())){
                return user.get();
            }

            else{
                throw new ServiceException("incorrect password");
            }
        }

        else{
            throw new ServiceException("user doesn't exist!");
        }
    }

    /**
     *
     * @param newUser must not be null
     * @return null, if the user was updated, u otherwise
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.
     */
    public Optional<User> updateUser(User newUser){
        Optional<User> oldUser = this.userRepo.findOne(newUser.getId());
        if(oldUser.isPresent()){
            Optional<User> res = userRepo.update(newUser);
            notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, newUser, oldUser.get()));
            return res;
        }
        return oldUser;
    }

    /**
     * removes the given user from the network
     * @param id must not be null
     * @return u or null if there is no entity with the given id
     * @throws IllegalArgumentException
     *       if the given id is null.
     */
    public User deleteUser(Long id){
        Optional<User> u = this.userRepo.findOne(id);

        if(u.isPresent()){
            User user = this.userRepo.delete(id).get();
            notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, user));
            return user;
        }

        return null;
    }

    /**
     * adds the given friend to the given user's friends list
     * @param userId must not be null
     * @param friendId must not be null
     * @return null, if the friend was added successfully
     *         friendId, otherwise
     * @throws IllegalArgumentException, if user or friend is null
     */
    public Long addFriend(Long userId, Long friendId){
        if(userId == null || friendId == null){
            throw new IllegalArgumentException("null user!");
        }

        Optional<User> user = this.userRepo.findOne(userId);
        Optional<User> friend = this.userRepo.findOne(friendId);
        Optional<Friendship> friendship = this.friendshipRepo.findOne(new Tuple<>(userId, friendId));

        if(user.isEmpty() || friend.isEmpty() || friendship.isPresent()){
            return friendId;
        }

        else {
            this.friendshipRepo.save(new Friendship(user.get(), friend.get()));
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));
            return null;
        }
    }

    /**
     * removes the given friend from the given user's friends list
     * @param userId must not be null
     * @param friendId must not be null
     * @return friend, if the friend was deleted successfully
     *         null, otherwise
     * @throws IllegalArgumentException if user or friend is null
     */
    public User deleteFriend(Long userId, Long friendId){
        if(userId == null || friendId == null){
            throw new IllegalArgumentException("null user!");
        }

        Optional<User> user = this.userRepo.findOne(userId);
        Optional<User> friend = this.userRepo.findOne(friendId);
        Optional<Friendship> friendship = this.friendshipRepo.findOne(new Tuple<>(userId, friendId));

        if(user.isEmpty() || friend.isEmpty() || friendship.isEmpty()){
            return null;
        }

        notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, null));
        friendshipRepo.delete(new Tuple<>(Long.min(userId, friendId), Long.max(userId, friendId)));
        return friend.get();
    }

    /**
     * returns all the users in the network
     * @return the list of users
     */
    public List<User> getAllUsers(){
        return this.userRepo.findAll();
    }

    /**
     *
     * @return the list of friendships in the network
     */
    public Iterable<Friendship> getAllFriendships(){
        return this.friendshipRepo.findAll();
    }

    /**
     *
     * @param user must not be null
     * @return the list of users which are not in the given user's friends list
     * @throws IllegalArgumentException if user is null
     */
    public Iterable<User> getNonFriendUsers(User user){
        if(user == null){
            throw new IllegalArgumentException("null user");
        }

        List<User> users = this.userRepo.findAll();
        users.remove(user);

        List<User> friends = this.getAllFriends(user);
        for(User friend:friends){
            users.remove(friend);
        }

        return users;
    }

    /**
     *
     * @param user must not be null
     * @return the list of the given user's friends
     * @throws IllegalArgumentException if user is null
     */
    public List<User> getAllFriends(User user){
        if(user == null){
            throw new IllegalArgumentException("null user");
        }

        return this.friendshipRepo.getAllFriends(user.getId());
    }

    /**
     * sends a friend request from the user with the given userId to the friend with the given friendId
     * @param userId must be not null
     * @param friendId must be not null
     * @return Optional.empty(), if the request was sent successfully
     *         Optional.of(friendId), if the user or friend doesn't exist in the database
     * @throws ServiceException if the request was already sent or rejected
     * @throws IllegalArgumentException if userId or friendId is null
     */
    public Optional<Long> sendFriendRequest(Long userId, Long friendId) {
        if(userId == null || friendId == null){
            throw new IllegalArgumentException("null user!");
        }

        Optional<User> user = this.userRepo.findOne(userId);
        Optional<User> friend = this.userRepo.findOne(friendId);
        Optional<FriendRequest> friendRequest = this.friendRequestRepo.findOne(new Tuple<>(userId, friendId));

        if(user.isEmpty() || friend.isEmpty()){
            return Optional.of(friendId);
        }

        if(friendRequest.isPresent() && Objects.equals(friendRequest.get().getStatus(), "rejected")){
            throw new ServiceException("request was rejected before");
        }

        else if(friendRequest.isPresent()){
            throw new ServiceException("request already sent");
        }

        else {
            this.friendRequestRepo.save(new FriendRequest(user.get(), friend.get()));
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));
            return Optional.empty();
        }
    }

    /**
     * accepts the given friend request only if it wasn't already accepted
     * @param userId must not be null
     * @param friendId must not be null
     * @return Optional.of(friendId), if the request was successfully accepted
     *         Optional.empty(), if the friendship already exists
     * @throws IllegalArgumentException if userId or friendId is null
     */
    public Optional<Long> acceptFriendRequest(Long userId, Long friendId) {
        if(userId == null || friendId == null){
            throw new IllegalArgumentException("null user!");
        }

        Optional<Friendship> friendship = this.friendshipRepo.findOne(new Tuple<>(userId, friendId));

        if(friendship.isEmpty()){
            this.addFriend(userId, friendId);
            this.friendRequestRepo.update(new FriendRequest(new Tuple<>(friendId, userId), "approved"));

            try{
                this.friendRequestRepo.update(new FriendRequest(new Tuple<>(userId, friendId), "approved"));
            } catch(Exception ignored){}

            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));
            return Optional.of(friendId);
        }

        return Optional.empty();
    }

    /**
     * rejects the given friend request
     * @param userId must not be null
     * @param friendId must not be null
     * @return Optional.of(friendId), if the request was successfully rejected
     *         Optional.empty(), if the friendship already exists
     * @throws IllegalArgumentException if userId or friendId is null
     */
    public Optional<Long> rejectFriendRequest(Long userId, Long friendId) {
        if(userId == null || friendId == null){
            throw new IllegalArgumentException("null user!");
        }

        Optional<Friendship> friendship = this.friendshipRepo.findOne(new Tuple<>(userId, friendId));

        if(friendship.isEmpty()){
            this.friendRequestRepo.update(new FriendRequest(new Tuple<>(friendId, userId), "rejected"));

            try{
                this.friendRequestRepo.update(new FriendRequest(new Tuple<>(userId, friendId), "rejected"));
            } catch(Exception ignored){}

            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));
            return Optional.of(friendId);
        }

        return Optional.empty();
    }

    /**
     *
     * @return all the friend requests in the database
     */
    public List<FriendRequest> getAllFriendRequests() {
        return this.friendRequestRepo.findAll();
    }

    /**
     *
     * @param user must be not null
     * @return all the pending friend requests addressed to the given user
     * @throws IllegalArgumentException if the given user is null
     */
    public List<User> getAllPendingUserRequests(User user) {
        if(user == null){
            throw new IllegalArgumentException("user must not be null!");
        }

        return this.friendRequestRepo.getAllUserPendingFriendRequests(user.getId());
    }

    /**
     * returns the user with the given ID
     * @param id must not be null
     * @return an Optional encapsulating the entity with the given id
     * @throws IllegalArgumentException if the given id is null
     */
    public Optional<User> getUser(Long id) throws IllegalArgumentException{
        return this.userRepo.findOne(id);
    }

    /**
     * returns the number of communities in the network
     * @return the number of communities
     */
    public int getCommunitiesNumber(){
        Map<Long, Integer> vertices = new HashMap<>();
        int[][] matAd = createGraph(vertices);
        int V = this.userRepo.size();

        boolean[] visited = new boolean[V];
        int[] distance = new int[V];
        int communities = 0;

        for(int node = 0;node<V;node++){
            if(!visited[node]){
                dfs(node, V, visited, matAd, distance);
                communities++;
            }
        }

        return communities;
    }

    /**
     *
     * @param vertices map of the user's id's and vertex number
     * @return the adjacency matrix of the network's graph
     */
    private int[][] createGraph(Map<Long, Integer> vertices){
        Iterable<User> users = userRepo.findAll();
        Iterable<Friendship> friendships = friendshipRepo.findAll();
        int V = this.userRepo.size();

        int i = 0;
        for(User user:users){
            vertices.put(user.getId(), i);
            i++;
        }

        int[][] matAd = new int[V][V];

        for(Friendship friendship:friendships){
            int x = vertices.get(friendship.getId().getLeft());
            int y = vertices.get(friendship.getId().getRight());
            matAd[x][y] = matAd[y][x] = 1;
        }

        return matAd;
    }

    /**
     * searches the given graph using Depth First Search
     * @param node the starting node
     * @param V the number of vertices
     * @param visited boolean array
     * @param matAd the adjacency matrix
     */
    private void dfs(int node, int V, boolean[] visited, int[][] matAd, int[] distance){
        visited[node] = true;
        for(int i=0;i<V;i++) {
            if (matAd[node][i] == 1 && !visited[i]) {
                distance[i] = distance[node] + 1;
                dfs(i, V, visited, matAd, distance);
            }
        }
    }

    /**
     *
     * @return the most active community
     */
    public List<User> mostActiveCommunity(){
        Map<Long, Integer> vertices = new HashMap<>();
        int[][] matAd = createGraph(vertices);
        int V = this.userRepo.size();

        boolean[] visited = new boolean[V];
        int[] distance = new int[V];
        int maxDistance = 0;
        int maxDistVertex = 0;

        for(int node = 0;node<V;node++){
            Arrays.fill(visited, false);
            dfs(node, V, visited, matAd, distance);

            for(int i=0;i<V;i++){
                if(distance[i] > maxDistance){
                    maxDistance = distance[i];
                    maxDistVertex = i;
                }
            }
        }

        List<Integer> comVert = new ArrayList<>();
        Arrays.fill(visited, false);
        getCommunity(maxDistVertex, V, visited, matAd, comVert);

        List<User> community = new ArrayList<>();

        comVert.forEach(code -> vertices.forEach((key, val) -> {
            if(Objects.equals(val, code)){
                userRepo.findOne(key).ifPresent(community::add);
            }
        }));

        return community;
    }

    /**
     * returns the community of the given user
     * @param node the given user vertex in the graph
     * @param V the number of vertices
     * @param visited boolean array
     * @param matAd the adjacency matrix of the graph
     * @param vertices the list containing the computed community
     */
    private void getCommunity(int node, int V, boolean[] visited, int[][] matAd, List<Integer> vertices){
        visited[node] = true;
        vertices.add(node);

        for(int i=0;i<V;i++) {
            if (matAd[node][i] == 1 && !visited[i]) {
                getCommunity(i, V, visited, matAd, vertices);
            }
        }
    }

    /**
     *
     * @param userID must not be null
     * @param monthIndex must be an integer between 1 and 12 (inclusive)
     * @return the list of friends of the given user made in the given month, each element
     *         of the list being formatted as 'friend_first_name |friend_last_name |friends_from (local-datetime)
     * @throws IllegalArgumentException if userID is null or the monthIndex isn't a valid one
     */
    public Stream<String> findAllFriendsByMonth(Long userID, int monthIndex){
        if(userID == null || !(monthIndex >= 1 && monthIndex <= 12)){
            throw new IllegalArgumentException("invalid data!");
        }

        Iterable<Friendship> friendships = this.friendshipRepo.findAll();

        return StreamSupport.stream(friendships.spliterator(), false)
                .filter(x -> (Objects.equals(x.getId().getLeft(), userID) ||
                        Objects.equals(x.getId().getRight(), userID)) &&
                        x.getFriendsFrom().getMonth() == Month.of(monthIndex))
                .map(x -> {
                    Long friendID = null;

                    if(Objects.equals(x.getId().getLeft(), userID)){
                        friendID = x.getId().getRight();
                    }

                    else if(Objects.equals(x.getId().getRight(), userID)){
                        friendID = x.getId().getLeft();
                    }

                    Optional<User> friend = this.userRepo.findOne(friendID);
                    return friend.get().getFirstName() + " |" + friend.get().getLastName() + " |" +
                            x.getFriendsFrom().toString();
                });
    }

    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
