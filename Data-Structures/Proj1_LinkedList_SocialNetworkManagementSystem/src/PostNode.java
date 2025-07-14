package application;

class PostNode {
    private Post data;
    private PostNode next;

    public PostNode(Post data) {
        this.data = data;
        this.next = null;
    }

    public Post getData() {
        return data;
    }

    public void setData(Post data) {
        this.data = data;
    }

    public PostNode getNext() {
        return next;
    }

    public void setNext(PostNode next) {
        this.next = next;
    }
}
