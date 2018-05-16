package io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed;

import io.rezetopia.krito.rezetopiakrito.model.pojo.post.AttachmentResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.post.CommentResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/5/2018.
 */

public class VendorPostsResponse {

    @SerializedName("post_id")
    @Expose
    private int postId;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("attachment")
    @Expose
    private AttachmentResponse attachment;

    @SerializedName("comments")
    @Expose
    private CommentResponse[] comments;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("likes")
    @Expose
    private int[] likes;

    @SerializedName("vendor_name")
    @Expose
    private String vendorName;

    @SerializedName("vendor_id")
    @Expose
    private int vendorId;

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int userId) {
        this.vendorId = userId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String username) {
        this.vendorName = username;
    }

    public int[] getLikes() {
        return likes;
    }

    public void setLikes(int[] likes) {
        this.likes = likes;
    }

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
}
