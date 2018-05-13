package app.reze1.ahmed.reze1.model.pojo.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/5/2018.
 */

public class EventSearchResponse {

    @SerializedName("event_id")
    @Expose
    private int eventId;

    @SerializedName("event_name")
    @Expose
    private String eventName;

    @SerializedName("event_description")
    @Expose
    private String eventDescription;

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
}
