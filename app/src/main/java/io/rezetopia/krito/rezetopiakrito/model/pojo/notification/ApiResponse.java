package io.rezetopia.krito.rezetopiakrito.model.pojo.notification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Mona Abdallh on 4/22/2018.
 */

public class ApiResponse {

    @SerializedName("notification")
    @Expose
    private ArrayList<Notification> notifications;

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }
}
