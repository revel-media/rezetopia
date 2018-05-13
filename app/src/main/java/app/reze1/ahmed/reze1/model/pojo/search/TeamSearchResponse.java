package app.reze1.ahmed.reze1.model.pojo.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/5/2018.
 */

public class TeamSearchResponse {

    @SerializedName("team_id")
    @Expose
    private int teamId;

    @SerializedName("team_name")
    @Expose
    private String teamName;

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
