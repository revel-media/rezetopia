package app.reze1.ahmed.reze1.model.pojo.store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/8/2018.
 */

public class RequestApiResponse  {

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("requests")
    @Expose
    private RequestResponse[] requests;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public RequestResponse[] getRequests() {
        return requests;
    }

    public void setRequests(RequestResponse[] requests) {
        this.requests = requests;
    }
}
