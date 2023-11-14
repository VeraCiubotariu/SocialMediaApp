package ir.map.gr222.service;

import ir.map.gr222.domain.Friendship;
import ir.map.gr222.domain.Tuple;
import ir.map.gr222.domain.User;
import ir.map.gr222.domain.validators.ValidationException;
import ir.map.gr222.repository.Repository;

import java.time.Month;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UserService {
    private final Repository<Long, User> userRepo;
    private final Repository<Tuple<Long, Long>, Friendship> friendshipRepo;

    public UserService(Repository<Long, User> repo, Repository<Tuple<Long, Long>, Friendship> friendshipRepo){
        this.userRepo = repo;
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * adds the user to the network
     *
     * @param u must not be null
     * @return null, if the user was added, u otherwise
     * @throws ValidationException      if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.
     */
    public Optional<User> addUser(User u) {
        return this.userRepo.save(u);
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

        // for InMemoryRepo, where we also save the friends list for each user
    /*    if(u.isPresent()){
            List<User> friends = u.get().getFriends();
            while(!friends.isEmpty()){
                this.deleteFriend(u.get().getId(), friends.get(0).getId());
            }

        //    friends.forEach(x -> deleteFriend(u.get().getId(), x.getId()));

            return this.userRepo.delete(id).get();
        }*/

        if(u.isPresent()){
            this.deleteFriends(id);
            return this.userRepo.delete(id).get();
        }

        return null;
    }

    private void deleteFriends(Long id) {
        Iterable<Friendship> friendships = this.friendshipRepo.findAll();

        for(Friendship friendship:friendships){
            if(Objects.equals(friendship.getId().getLeft(), id) || Objects.equals(friendship.getId().getRight(), id)){
                this.friendshipRepo.delete(friendship.getId());
            }
        }
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

        if(user.isEmpty() || friend.isEmpty()){
            return friendId;
        }

        if(friend.get().addFriend(user.get()) == null && user.get().addFriend(friend.get()) == null){
            this.friendshipRepo.save(new Friendship(user.get(), friend.get()));
            return null;
        }

        return friendId;
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

        if(user.isEmpty() || friend.isEmpty()){
            return null;
        }

        friendshipRepo.delete(new Tuple<>(Long.min(userId, friendId), Long.max(userId, friendId)));

        friend.get().deleteFriend(user.get());
        return user.get().deleteFriend(friend.get());
    }

    /**
     * returns all the users in the network
     * @return the list of users
     */
    public Iterable<User> getAllUsers(){
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
}
