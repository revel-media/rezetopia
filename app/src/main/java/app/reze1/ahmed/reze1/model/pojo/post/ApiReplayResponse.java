package app.reze1.ahmed.reze1.model.pojo.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/7/2018.
 */

public class ApiReplayResponse {

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("replies")
    @Expose
    private CommentReplyResponse[] replies;


    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public CommentReplyResponse[] getReplies() {
        return replies;
    }

    public void setReplies(CommentReplyResponse[] replies) {
        this.replies = replies;
    }
}
