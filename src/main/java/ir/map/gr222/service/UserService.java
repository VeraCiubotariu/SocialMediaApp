package ir.map.gr222.service;

import ir.map.gr222.domain.Friendship;
import ir.map.gr222.domain.Tuple;
import ir.map.gr222.domain.User;
import ir.map.gr222.domain.validators.UserValidator;
import ir.map.gr222.domain.validators.ValidationException;
import ir.map.gr222.repository.InMemoryRepository;

import java.util.*;

public class UserService {
    private InMemoryRepository<Long, User> userRepo;
    private InMemoryRepository<Tuple<Long, Long>, Friendship> friendshipRepo;
    private UserValidator validator = new UserValidator();

    public UserService(InMemoryRepository<Long, User> repo, InMemoryRepository<Tuple<Long, Long>, Friendship> friendshipRepo){
        this.userRepo = repo;
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * adds the user to the network
     * @param u must not be null
     * @return null, if the user was added, u otherwise
     * @throws ValidationException
     *              if the entity is not valid
     * @throws IllegalArgumentException
     *              if the given entity is null.
     */
    public User addUser(User u) {
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
        User u = this.userRepo.delete(id);

        if(u != null){
            List<User> friends = u.getFriends();
            while(!friends.isEmpty()){
                this.deleteFriend(u, friends.get(0));
            }
        }

        return u;
    }

    /**
     * adds the given friend to the given user's friends list
     * @param user must not be null
     * @param friend must not be null
     * @return null, if the friend was added successfully
     *         friend, otherwise
     * @throws IllegalArgumentException, if user or friend is null
     * @throws ValidationException, if either user is not valid
     */
    public User addFriend(User user, User friend){
        if(user == null){
            throw new IllegalArgumentException("null user!");
        }

        if(this.userRepo.findOne(user.getId()) == null || this.userRepo.findOne(friend.getId()) == null){
            return friend;
        }

        if(friend.addFriend(user) == null && user.addFriend(friend) == null){
            this.friendshipRepo.save(new Friendship(user, friend));
            return null;
        }

        return friend;
    }

    /**
     * removes the given friend from the given user's friends list
     * @param user must not be null
     * @param friend must not be null
     * @return friend, if the friend was deleted successfully
     *         null, otherwise
     * @throws IllegalArgumentException if user or friend is null
     * @throws ValidationException if either user is not valid
     */
    public User deleteFriend(User user, User friend){
        if(user == null){
            throw new IllegalArgumentException("null user!");
        }

        if(this.userRepo.findOne(user.getId()) == null || this.userRepo.findOne(friend.getId()) == null){
            return null;
        }

        friendshipRepo.delete(new Tuple<Long, Long>(Long.min(user.getId(), friend.getId()), Long.max(user.getId(), friend.getId())));

        friend.deleteFriend(user);
        return user.deleteFriend(friend);
    }

    /**
     * returns all the users in the network
     * @return the list of users
     */
    public Iterable<User> getAllUsers(){
        return this.userRepo.findAll();
    }

    public User getUser(Long id){
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
            if(!visited[node]){
                dfs(node, V, visited, matAd, distance);
                if(distance[node] > maxDistance){
                    maxDistance = distance[node];
                    maxDistVertex = node;
                }
            }
        }

        List<Integer> comVert = new ArrayList<>();
        Arrays.fill(visited, false);
        getCommunity(maxDistVertex, V, visited, matAd, comVert);

        List<User> community = new ArrayList<>();
        for(Integer code:comVert){
            for(Map.Entry<Long, Integer> entry:vertices.entrySet()){
                if(Objects.equals(entry.getValue(), code)){
                    community.add(userRepo.findOne(entry.getKey()));
                }
            }
        }

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
}
