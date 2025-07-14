package application;

import java.util.ArrayList;
import java.util.List;

public class SharedWithList {
    private StringNode head;
    private StringNode tail;

    public SharedWithList() {
        head = null;
        tail = null;
    }

    public void add(String userId) {
        StringNode newNode = new StringNode(userId);
        if (head == null) {
            head = tail = newNode;
            newNode.setNext(head); 
        } else {
            tail.setNext(newNode);
            newNode.setNext(head);
            tail = newNode;
        }
    }

    public boolean contains(String userId) {
        if (head == null) return false;

        StringNode current = head;
        do {
            if (current.getData().equals(userId)) {
                return true;
            }
            current = current.getNext();
        } while (current != head);

        return false;
    }

    public String toStringList() {
        if (head == null) return "";

        StringBuilder result = new StringBuilder();
        StringNode current = head;
        do {
            result.append(current.getData()).append(", ");
            current = current.getNext();
        } while (current != head);

        return result.substring(0, result.length() - 2); 
    }

    public StringNode getHead() {
        return head;
    }

    public StringNode getTail() {
        return tail;
    }
    public List<String> toJavaList() {
        List<String> list = new ArrayList<>();
        StringNode current = head;

        if (current == null) return list;

        do {
            list.add(current.getData());
            current = current.getNext();
        } while (current != head);

        return list;
    }
    public void removeUser(String userId) {
        if (head == null) return;

        StringNode current = head;
        StringNode prev = tail;
        boolean found = false;

        do {
            if (current.getData().equals(userId)) {
                found = true;
                if (current == head && current == tail) {
                    head = tail = null;
                } else if (current == head) {
                    head = head.getNext();
                    tail.setNext(head);
                } else if (current == tail) {
                    prev.setNext(head);
                    tail = prev;
                } else {
                    prev.setNext(current.getNext());
                }
                break;
            }
            prev = current;
            current = current.getNext();
        } while (current != head);

        if (!found) {
            System.out.println("User ID not found in shared list.");
        }
    }

}
