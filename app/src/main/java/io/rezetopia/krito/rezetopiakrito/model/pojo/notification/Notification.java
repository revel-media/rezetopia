package io.rezetopia.krito.rezetopiakrito.model.pojo.notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 4/22/2018.
 */

public class Notification {
    @SerializedName("notification_id")
    @Expose
    private int id;

    @SerializedName("user_from_id")
    @Expose
    private int userFromId;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("seen")
    @Expose
    private boolean seen;

    @SerializedName("timestamp")
    @Expose
    private String createdAt;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("post_id")
    @Expose
    private int postId;

    @SerializedName("type")
    @Expose
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserFromId() {
        return userFromId;
    }

    public void setUserFromId(int userFromId) {
        this.userFromId = userFromId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
