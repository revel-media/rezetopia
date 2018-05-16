package io.rezetopia.krito.rezetopiakrito.model.pojo.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by amr on 4/15/2018.
 */

public class PhotoResponse {
    @SerializedName("user_id")
    @Expose
    private String user_id;

    @SerializedName("image_url")
    @Expose
    private String image_url;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


}
