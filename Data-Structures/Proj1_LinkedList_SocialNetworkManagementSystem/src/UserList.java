package application;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Callback;

class UserList {
    private UserNode head;  
    private UserNode tail; 

    public UserList() {
        head = null;
        tail = null;
    }

    public void addUserSorted(User user) {
        UserNode newNode = new UserNode(user);

        if (head == null) {
            head = tail = newNode;
            newNode.setNext(head);
            return;
        }
        UserNode current = head;
        UserNode prev = tail;

        do {
            if (user.getName().compareToIgnoreCase(current.getData().getName()) < 0) {
                newNode.setNext(current);
                prev.setNext(newNode);

                if (current == head) {
                    head = newNode;
                }
                return;
            }
            prev = current;
            current = current.getNext();
        } while (current != head);

        newNode.setNext(head);
        tail.setNext(newNode);
        tail = newNode;
    }


    public void removeUser(String userId) {
        if (head == null) return;

        UserNode current = head;
        UserNode prev = tail;

        do {
            if (current.getData().getUserId().equals(userId)) {
                if (current == head) {
                    if (head == tail) {
                        head = tail = null; 
                    } else {
                        head = head.getNext();
                        tail.setNext(head); 
                    }
                } else if (current == tail) {
                    tail = prev;
                    tail.setNext(head);
                } else {
                    prev.setNext(current.getNext());
                }
                return;
            }
            prev = current;
            current = current.getNext();
        } while (current != head);
    }

    public void displayUsers() {
        if (head == null) {
            System.out.println("No users in the list.");
            return;
        }

        UserNode current = head;
        do {
            System.out.println("User ID: " + current.getData().getUserId() + ", Name: " + current.getData().getName());
            current = current.getNext();
        } while (current != head);
    }

    public User findUserById(String userId) {
        if (head == null) return null;

        UserNode current = head;
        do {
            if (current.getData().getUserId().equals(userId)) {
                return current.getData();
            }
            current = current.getNext();
        } while (current != head);

        return null;
    }

    public UserNode getHead() {
        return head;
    }

    public UserNode getTail() {
        return tail;
    }

    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();

        if (head == null) return userList;

        UserNode current = head;
        do {
            userList.add(current.getData());
            current = current.getNext();
        } while (current != head);

        return userList;
    }

}
