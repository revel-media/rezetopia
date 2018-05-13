package app.reze1.ahmed.reze1.model.pojo.group;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/1/2018.
 */

public class GroupResponse {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("owner_id")
    @Expose
    private int owner_id;

    @SerializedName("privacy_type")
    @Expose
    private String privacy;

    @SerializedName("post_status")
    @Expose
    private String postStatus;


    @SerializedName("join_fees")
    @Expose
    private float joinFees;


    @SerializedName("description")
    @Expose
    private String description;


    @SerializedName("image_url")
    @Expose
    private String ImageUrl;


    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("timestamp")
    @Expose
    private String timestamp;


    @SerializedName("owner_name")
    @Expose
    private String ownerName;

    @SerializedName("members_count")
    @Expose
    private int memberCount;

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(String postStatus) {
        this.postStatus = postStatus;
    }

    public float getJoinFees() {
        return joinFees;
    }

    public void setJoinFees(float joinFees) {
        this.joinFees = joinFees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
