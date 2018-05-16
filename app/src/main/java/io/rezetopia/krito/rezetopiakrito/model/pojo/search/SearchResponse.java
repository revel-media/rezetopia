package io.rezetopia.krito.rezetopiakrito.model.pojo.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 4/16/2018.
 */

public class SearchResponse {

    @SerializedName("users")
    @Expose
    private SearchUserResponse[] users;

    @SerializedName("groups")
    @Expose
    private SearchGroupsResponse[] groups;

    @SerializedName("vendors")
    @Expose
    private SearchVendorResponse[] vendors;

    @SerializedName("teams")
    @Expose
    private TeamSearchResponse[] teams;

    @SerializedName("event")
    @Expose
    private EventSearchResponse[] events;


    public SearchVendorResponse[] getVendors() {
        return vendors;
    }

    public void setVendors(SearchVendorResponse[] vendors) {
        this.vendors = vendors;
    }

    public TeamSearchResponse[] getTeams() {
        return teams;
    }

    public void setTeams(TeamSearchResponse[] teams) {
        this.teams = teams;
    }

    public EventSearchResponse[] getEvents() {
        return events;
    }

    public void setEvents(EventSearchResponse[] events) {
        this.events = events;
    }

    public SearchUserResponse[] getUsers() {
        return users;
    }

    public void setUsers(SearchUserResponse[] users) {
        this.users = users;
    }

    public SearchGroupsResponse[] getGroups() {
        return groups;
    }

    public void setGroups(SearchGroupsResponse[] groups) {
        this.groups = groups;
    }
}
