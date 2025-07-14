package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class User implements Comparable<User> {
    private String userId;
    private String name;
    private int age;  
    private UserList friends;  
    private PostList postsCreated;  
    private PostList postsSharedWith;  

    public User(String userId, String name, int age) { 
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.friends = new UserList();  
        this.postsCreated = new PostList();  
        this.postsSharedWith = new PostList();  
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public UserList getFriends() {
        return friends;
    }

    public PostList getPostsCreated() {
        return postsCreated;
    }

    public PostList getPostsSharedWith() {
        return postsSharedWith;
    }

    public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void addFriend(User friend) {
        this.friends.addUserSorted(friend); 
    }
	public void removeFriendById(String friendId) {
	    this.friends.removeUser(friendId);
	}

    @Override
    public int compareTo(User other) {
        return this.userId.compareTo(other.userId);   
    }
}