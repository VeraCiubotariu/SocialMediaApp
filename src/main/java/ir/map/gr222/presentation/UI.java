package ir.map.gr222.presentation;

import ir.map.gr222.domain.User;
import ir.map.gr222.repository.Repository;
import ir.map.gr222.service.UserService;

import java.security.Provider;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UI {
    private UserService serv;
    private Scanner sc = new Scanner(System.in);

    public UI(UserService service){
        this.serv = service;
    }

    public void runUI(){
        boolean running = true;

        while(running){
            System.out.println("1. Add user\n2. Delete user\n3. Add friend\n4. Delete friend\n5. Show all users\n6. Show the number of communities\n7. Show the most active community\ne. Exit\n");
            System.out.println("Input your command: ");
            char cmd = sc.next().charAt(0);
            sc.nextLine();

            switch(cmd){
                case 'e':
                    running = false;
                    break;
                case '1':
                    this.addUser();
                    break;
                case '2':
                    this.removeUser();
                    break;
                case '3':
                    this.addFriend();
                    break;
                case '4':
                    this.removeFriend();
                    break;
                case '5':
                    this.printUsers();
                    break;
                case '6':
                    this.numberOfCommunitites();
                    break;
                case '7':
                    this.mostActiveCommunity();
                    break;
                default:
                    System.out.println("invalid command!");
                    break;
            }
        }
    }

    private void mostActiveCommunity() {
        List<User> community = this.serv.mostActiveCommunity();
        System.out.println("The users of the most active community are:");
        community.forEach(System.out::println);
    }

    private void numberOfCommunitites() {
        int communities = this.serv.getCommunitiesNumber();
        System.out.println("There are a total of " + communities + " communities.");
    }

    private void addUser(){
        System.out.println("Input the user's first name: ");
        String firstName = sc.nextLine();

        System.out.println("input the user's last name: ");
        String lastName = sc.nextLine();

        User user = new User(firstName, lastName);
        try{
            if(this.serv.addUser(user).isEmpty()){
                System.out.println("user added successfully!");
            }

            else{
                System.out.println("error in adding user!");
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private void removeUser(){
        System.out.println("Input the user's ID: ");
        Long id = sc.nextLong();

        try{
            if(this.serv.deleteUser(id)!=null){
                System.out.println("User deleted successfully!");
            }

            else{
                System.out.println("Couldn't delete user!");
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }

    }

    private void addFriend(){
        System.out.println("Input the user's ID: ");
        Long userId = sc.nextLong();

        System.out.println("Input the friend's ID: ");
        Long friendId = sc.nextLong();

        try {
            if (this.serv.addFriend(userId, friendId) == null) {
                System.out.println("Friend added successfully!");
            } else {
                System.out.println("Couldn't add friend!");
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private void removeFriend(){
        System.out.println("Input the user's ID: ");
        Long userId = sc.nextLong();

        System.out.println("Input the friend's ID: ");
        Long friendId = sc.nextLong();

        try{

            if(this.serv.deleteFriend(userId, friendId)!=null){
                System.out.println("Friend deleted successfully!");
            }

            else{
                System.out.println("Couldn't delete friend!");
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private void printUsers() {
        Iterable<User> users = this.serv.getAllUsers();
        users.forEach(System.out::println);
    }
}
