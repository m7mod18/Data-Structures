package application;

public class ProductLinkedList {
    private PNode head;

    public ProductLinkedList() {
        head = null;
    }

    public void addProduct(Product product) {
        PNode newNode = new PNode(product);
        if (head == null) {
            head = newNode;
        } else {
            PNode current = head;
            while (current.getNext() != null)
                current = current.getNext();
            current.setNext(newNode);
        }
    }

    public Product getProductByID(String productID) {
        PNode current = head;
        while (current != null) {
            if (current.getProduct().getProductID().equalsIgnoreCase(productID))
                return current.getProduct();
            current = current.getNext();
        }
        return null;
    }

    public void removeProductByID(String productID) {
        PNode current = head, prev = null;
        while (current != null && !current.getProduct().getProductID().equalsIgnoreCase(productID)) {
            prev = current;
            current = current.getNext();
        }

        if (current == null) return;

        if (prev == null)
            head = head.getNext();
        else
            prev.setNext(current.getNext());
    }

    public void printAllProducts() {
        PNode current = head;
        while (current != null) {
            Product p = current.getProduct();
            System.out.println("  - " + p.getProductID() + ": " + p.getName() + " [" + p.getStatus() + "]");
            current = current.getNext();
        }
    }

    public boolean isEmpty() {
        return head == null;
    }
    public void clear() {
        head = null;
    }
    public int count() {
        int count = 0;
        PNode current = head;
        while (current != null) {
            count++;
            current = current.getNext();
        }
        return count;
    }

    public PNode getHead() {
        return head;
    }
} 