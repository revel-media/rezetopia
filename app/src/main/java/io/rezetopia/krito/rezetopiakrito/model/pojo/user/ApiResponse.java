package io.rezetopia.krito.rezetopiakrito.model.pojo.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by amr on 4/19/2018.
 */

public class ApiResponse{

    @SerializedName("users")
    @Expose
    private User[] users;

    @SerializedName("error")
    @Expose
    private boolean error;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }
}
