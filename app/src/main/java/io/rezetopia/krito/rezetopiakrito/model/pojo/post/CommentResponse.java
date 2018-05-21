package io.rezetopia.krito.rezetopiakrito.model.pojo.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mona Abdallh on 4/11/2018.
 */

public class CommentResponse implements Serializable{

    @SerializedName("commentId")
    @Expose
    private int commentId;

    @SerializedName("commenterId")
    @Expose
    private int commenterId;

    @SerializedName("commentText")
    @Expose
    private String commentText;

    @SerializedName("replies")
    @Expose
    private CommentReplyResponse[] replies;

    @SerializedName("replay_size")
    @Expose
    private int replaySize;

    @SerializedName("commenterName")
    @Expose
    private String commenterName;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("likes")
    @Expose
    private int[] likes;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("pending")
    @Expose
    private boolean pending;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public int getReplaySize() {
        return replaySize;
    }

    public void setReplaySize(int replaySize) {
        this.replaySize = replaySize;
    }

    public int[] getLikes() {
        return likes;
    }

    public void setLikes(int[] likes) {
        this.likes = likes;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(int commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public CommentReplyResponse[] getReplies() {
        return replies;
    }

    public void setReplies(CommentReplyResponse[] replies) {
        this.replies = replies;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
