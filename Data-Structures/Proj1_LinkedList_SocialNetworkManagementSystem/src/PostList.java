package application;

class PostList {
    private PostNode head;
    private PostNode tail;

    public PostList() {
        head = null;
        tail = null;
    }

    public void addPost(Post post) {
        PostNode newNode = new PostNode(post);

        if (head == null) {
            head = newNode;
            tail = newNode;
            newNode.setNext(head); 
        } else {
            tail.setNext(newNode);
            newNode.setNext(head);
            tail = newNode;
        }
    }

    public void deletePost(String postId) {
        if (head == null) return;

        PostNode current = head;
        PostNode prev = tail;

        do {
            if (current.getData().getPostId().equals(postId)) {
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

    public void displayPosts() {
        if (head == null) {
            System.out.println("No posts.");
            return;
        }

        PostNode current = head;
        do {
            Post p = current.getData();
            System.out.println("Post ID: " + p.getPostId() + ", Content: " + p.getContent() + ", Date: " + p.getCreationDate());
            current = current.getNext();
        } while (current != head);
    }

    public PostNode getHead() {
        return head;
    }

    public PostNode getTail() {
        return tail;
    }
    public int countPosts() {
        int count = 0;
        if (head == null) return count;

        PostNode current = head;
        do {
            count++;
            current = current.getNext();
        } while (current != head);

        return count;
    }

}
