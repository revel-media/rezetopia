package io.rezetopia.krito.rezetopiakrito.model.pojo.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 4/16/2018.
 */

public class SearchUserResponse {

    @SerializedName("userId")
    @Expose
    private int userId;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    @SerializedName("uesrmail")
    @Expose
    private String uesrmail;

    public String getUesrmail() {
        return uesrmail;
    }

    public void setUesrmail(String uesrmail) {
        this.uesrmail = uesrmail;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
