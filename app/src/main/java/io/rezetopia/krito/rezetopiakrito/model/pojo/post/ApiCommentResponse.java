package io.rezetopia.krito.rezetopiakrito.model.pojo.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Mona Abdallh on 5/7/2018.
 */

public class ApiCommentResponse implements Serializable{

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("comments")
    @Expose
    private CommentResponse[] comments;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public CommentResponse[] getComments() {
        return comments;
    }

    public void setComments(CommentResponse[] comments) {
        this.comments = comments;
    }
}
