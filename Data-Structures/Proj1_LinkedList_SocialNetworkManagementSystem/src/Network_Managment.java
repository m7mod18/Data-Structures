package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Network_Managment {
    private UserList users;

    public Network_Managment() {
        users = new UserList();
    }

    public void addUser(User user) {
        System.out.println("Add Test: " + user.getAge());
        users.addUserSorted(user);
    }

    public void deleteUser(String userId) {
        users.removeUser(userId);
    }

    public User findUserById(String userId) {
        UserNode currentNode = users.getHead();
        if (currentNode == null) return null;

        do {
            if (currentNode.getData().getUserId().equals(userId)) {
                return currentNode.getData();
            }
            currentNode = currentNode.getNext();
        } while (currentNode != users.getHead());

        return null;
    }

    public void readUsers(File file) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); 
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length != 3) {
                    System.out.println("Invalid user data format: " + line);
                    continue;
                }

                String userId = parts[0].trim();
                String name = parts[1].trim();
                int age = Integer.parseInt(parts[2].trim());
                User user = new User(userId, name, age);
                addUser(user);
                System.out.println("Read: " + user.getName());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: User data file not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }
    }

    public void readFriendships(File file) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] friendshipData = line.split(",");
                if (friendshipData.length < 2) {
                    System.out.println("Invalid friendship data format: " + line);
                    continue;
                }

                String userId = friendshipData[0].trim();
                User user = findUserById(userId);

                if (user != null) {
                    for (int i = 1; i < friendshipData.length; i++) {
                        String friendId = friendshipData[i].trim();
                        User friend = findUserById(friendId);
                        if (friend != null) {
                            user.addFriend(friend);
                        }
                    }
                } else {
                    System.out.println("User not found: " + userId);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: Friendships file not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error reading friendships data: " + e.getMessage());
        }
    }
    public void readPosts(File file) {
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); 
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                List<String> partsList = new ArrayList<>();
                boolean insideQuotes = false;
                StringBuilder current = new StringBuilder();

                for (char c : line.toCharArray()) {
                    if (c == '"') {
                        insideQuotes = !insideQuotes;
                    } else if (c == ',' && !insideQuotes) {
                        partsList.add(current.toString().trim());
                        current.setLength(0);
                    } else {
                        current.append(c);
                    }
                }
                partsList.add(current.toString().trim()); 

                String[] parts = partsList.toArray(new String[0]);
                if (parts.length < 5) {
                    System.out.println("Invalid post format: " + line);
                    continue;
                }

                String postId = parts[0];
                String creatorId = parts[1];
                String content = parts[2];
                String dateStr = parts[3];

                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);

                Post post = new Post(postId, creatorId, content, date);

                for (int i = 4; i < parts.length; i++) {
                    String sharedUserId = parts[i];
                    post.shareWith(sharedUserId);
                }

                User creator = findUserById(creatorId);
                if (creator != null) {
                    creator.getPostsCreated().addPost(post);

                    for (int i = 4; i < parts.length; i++) {
                        User sharedUser = findUserById(parts[i]);
                        if (sharedUser != null) {
                            sharedUser.getPostsSharedWith().addPost(post);
                        }
                    }
                } else {
                    System.out.println("Creator not found: " + creatorId);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading posts: " + e.getMessage());
        }
    }


    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("dd.MM.yyyy").parse(dateStr);
        } catch (Exception e) {
            System.out.println("Invalid date: " + dateStr);
            return null;
        }
    }

    public boolean userExists(String userId) {
        return findUserById(userId) != null;
    }

    public ObservableList<User> getUsers() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        List<User> usersList = users.getUsers();  
        userList.addAll(usersList);
        return userList;
    }

    public void setUsers(UserList users) {
        this.users = users;
    }

    public UserNode getUsersHead() {
        return users.getHead();
    }
    public void saveUsersToFile(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("User ID,Name,Age");
            for (User user : getUsers()) {
                writer.printf("%s,%s,%d%n", user.getUserId(), user.getName(), user.getAge());
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
    public void saveFriendshipsToFile(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("User ID,Friend IDs");
            for (User user : getUsers()) {
                StringBuilder line = new StringBuilder(user.getUserId());
                for (User friend : user.getFriends().getUsers()) {
                    line.append(",").append(friend.getUserId());
                }
                writer.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error saving friendships: " + e.getMessage());
        }
    }
    public void savePostsToFile(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("Post ID,Creator ID,Content,Creation Date,Shared With");

            for (User user : getUsers()) {
                PostNode current = user.getPostsCreated().getHead();
                if (current != null) {
                    do {
                        Post post = current.getData();
                        StringBuilder sharedWith = new StringBuilder();
                        for (String id : post.getSharedWith().toJavaList()) {
                            sharedWith.append(",").append(id);
                        }
                        writer.printf("%s,%s,\"%s\",%s%s%n",
                                post.getPostId(),
                                post.getCreatorId(),
                                new SimpleDateFormat("yyyy-MM-dd").format(post.getCreationDate()),
                                sharedWith.toString()
                        );
                        current = current.getNext();
                    } while (current != user.getPostsCreated().getHead());
                }
            }

        } catch (IOException e) {
            System.out.println("Error saving posts: " + e.getMessage());
        }
    }

}
