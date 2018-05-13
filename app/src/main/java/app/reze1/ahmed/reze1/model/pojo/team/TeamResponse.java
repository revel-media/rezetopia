package app.reze1.ahmed.reze1.model.pojo.team;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/3/2018.
 */

public class TeamResponse {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("owner_id")
    @Expose
    private int ownerId;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("points")
    @Expose
    private int points;

    @SerializedName("freezed_points")
    @Expose
    private int freezedPoints;

    @SerializedName("timestamp_after_six_month")
    @Expose
    private String timestampAfterSixMonth;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getFreezedPoints() {
        return freezedPoints;
    }

    public void setFreezedPoints(int freezedPoints) {
        this.freezedPoints = freezedPoints;
    }

    public String getTimestampAfterSixMonth() {
        return timestampAfterSixMonth;
    }

    public void setTimestampAfterSixMonth(String timestampAfterSixMonth) {
        this.timestampAfterSixMonth = timestampAfterSixMonth;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
