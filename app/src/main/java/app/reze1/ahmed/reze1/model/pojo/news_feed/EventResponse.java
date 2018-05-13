package app.reze1.ahmed.reze1.model.pojo.news_feed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/5/2018.
 */

public class EventResponse {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("occur_date")
    @Expose
    private String occurDate;

    @SerializedName("owner_id")
    @Expose
    private int ownerId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("timestamp_after_three_months")
    @Expose
    private String timeAfterThreeMonths;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOccurDate() {
        return occurDate;
    }

    public void setOccurDate(String occurDate) {
        this.occurDate = occurDate;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeAfterThreeMonths() {
        return timeAfterThreeMonths;
    }

    public void setTimeAfterThreeMonths(String timeAfterThreeMonths) {
        this.timeAfterThreeMonths = timeAfterThreeMonths;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
