package io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed;

import io.rezetopia.krito.rezetopiakrito.model.pojo.post.AttachmentResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.CommentResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/5/2018.
 */

public class GroupPostResponse {

    @SerializedName("group_id")
    @Expose
    private int id;

    @SerializedName("group_name")
    @Expose
    private String name;

    @SerializedName("post_id")
    @Expose
    private int postId;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("attachment")
    @Expose
    private AttachmentResponse attachment;

    @SerializedName("comment_size")
    @Expose
    private int commentSize;

    @SerializedName("comments")
    @Expose
    private CommentResponse[] comments;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("likes")
    @Expose
    private int[] likes;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("user_id")
    @Expose
    private String userId;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AttachmentResponse getAttachment() {
        return attachment;
    }

    public void setAttachment(AttachmentResponse attachment) {
        this.attachment = attachment;
    }

    public int getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(int commentSize) {
        this.commentSize = commentSize;
    }

    public CommentResponse[] getComments() {
        return comments;
    }

    public void setComments(CommentResponse[] comments) {
        this.comments = comments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int[] getLikes() {
        return likes;
    }

    public void setLikes(int[] likes) {
        this.likes = likes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
