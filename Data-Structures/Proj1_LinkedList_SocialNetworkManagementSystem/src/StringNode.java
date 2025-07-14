package application;

public class StringNode {
    private String data;
    private StringNode next;

    public StringNode(String data) {
        this.data = data;
        this.next = null;
    }

    public String getData() {
        return data;
    }

    public StringNode getNext() {
        return next;
    }

    public void setNext(StringNode next) {
        this.next = next;
    }
}
