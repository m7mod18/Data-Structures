package application;

import java.util.Date;

public class Post implements Comparable<Post> {
    private String postId;
    private String creatorId;
    private String content;
    private Date creationDate;
    private SharedWithList sharedWith;

    public Post(String postId, String creatorId, String content, Date creationDate) {
        this.postId = postId;
        this.creatorId = creatorId;
        this.content = content;
        this.creationDate = creationDate;
        this.sharedWith = new SharedWithList(); 
    }

    public String getPostId() {
        return postId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getContent() {
        return content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public SharedWithList getSharedWith() {
        return sharedWith;
    }

    public void shareWith(String userId) {
        sharedWith.add(userId);
    }

    @Override
    public int compareTo(Post other) {
        return this.postId.compareTo(other.postId);
    }
}
