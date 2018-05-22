package io.rezetopia.krito.rezetopiakrito.model.pojo.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mona Abdallh on 4/11/2018.
 */

public class AttachmentResponse implements Serializable {

    @SerializedName("videos")
    @Expose
    private MediaResponse[] videos;

    @SerializedName("images")
    @Expose
    private MediaResponse[] images;


    public MediaResponse[] getVideos() {
        return videos;
    }

    public void setVideos(MediaResponse[] videos) {
        this.videos = videos;
    }

    public MediaResponse[] getImages() {
        return images;
    }

    public void setImages(MediaResponse[] images) {
        this.images = images;
    }
}
