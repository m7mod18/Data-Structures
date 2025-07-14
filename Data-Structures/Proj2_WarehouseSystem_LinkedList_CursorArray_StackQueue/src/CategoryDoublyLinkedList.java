package application;

public class CategoryDoublyLinkedList {

    private CatNode head;

    public CategoryDoublyLinkedList() {
        head = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void addCategory(Category category) {
        CatNode newNode = new CatNode(category);

        if (head == null) {
            head = newNode;
        } else {
            CatNode current = head;
            while (current.getNext() != null)
                current = current.getNext();

            current.setNext(newNode);
            newNode.setPrev(current);
        }
    }

    public void printAllCategories() {
        CatNode current = head;
        while (current != null) {
            System.out.println("[" + current.getCategory().getCategoryID() + "] " + current.getCategory().getName());
            current = current.getNext();
        }
    }

    public CatNode getHeadNode() {
        return head;
    }

    public void removeByID(String id) {
        if (head == null) return;

        CatNode current = head;
        CatNode prev = null;

        while (current != null && !current.getCategory().getCategoryID().equalsIgnoreCase(id)) {
            prev = current;
            current = current.getNext();
        }

        if (current == null) return;

        if (prev == null) {
            head = head.getNext();
            if (head != null) head.setPrev(null);
        } else {
            prev.setNext(current.getNext());
            if (current.getNext() != null) {
                current.getNext().setPrev(prev);
            }
        }
    }

    public Category searchByID(String id) {
        CatNode current = head;
        while (current != null) {
            if (current.getCategory().getCategoryID().equalsIgnoreCase(id)) {
                return current.getCategory();
            }
            current = current.getNext();
        }
        return null;
    }

    public Category searchByName(String name) {
        CatNode current = head;
        while (current != null) {
            if (current.getCategory().getName().equalsIgnoreCase(name)) {
                return current.getCategory();
            }
            current = current.getNext();
        }
        return null;
    }   
} 