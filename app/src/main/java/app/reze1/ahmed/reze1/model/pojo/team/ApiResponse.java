package app.reze1.ahmed.reze1.model.pojo.team;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/3/2018.
 */

public class ApiResponse {

    @SerializedName("teams")
    @Expose
    private TeamResponse[] teams;

    @SerializedName("error")
    @Expose
    private Boolean error;

    public TeamResponse[] getTeams() {
        return teams;
    }

    public void setTeams(TeamResponse[] teams) {
        this.teams = teams;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }
}
