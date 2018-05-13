package app.reze1.ahmed.reze1.model.pojo.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by amr on 4/19/2018.
 */

public class ApiResponse{

    @SerializedName("users")
    @Expose
    private ArrayList<User>users;

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
