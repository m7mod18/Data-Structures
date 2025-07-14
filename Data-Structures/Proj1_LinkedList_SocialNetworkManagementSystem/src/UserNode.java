package application;



class UserNode {
    private User data;      
    private UserNode next;  

    public UserNode(User data) {
        this.data = data;
        this.next = null;   
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public UserNode getNext() {
        return next;
    }

    public void setNext(UserNode next) {
        this.next = next;
    }
}
