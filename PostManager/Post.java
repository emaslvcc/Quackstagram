package PostManager;

public class Post {

  private String postId;
  private String userId;
  private String caption;
  private int likeCount;
  private int commentCount;

  // Constructor
  public Post(
    String postId,
    String userId,
    String caption,
    int likeCount,
    int commentCount
  ) {
    this.postId = postId;
    this.userId = userId;
    this.caption = caption;
    this.likeCount = likeCount;
    this.commentCount = commentCount;
  }

  // Getters and setters
  public String getPostId() {
    return postId;
  }

  public String getUserId() {
    return userId;
  }

  public String getCaption() {
    return caption;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public int getCommentCount() {
    return commentCount;
  }
}
