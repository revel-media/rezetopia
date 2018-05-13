package app.reze1.ahmed.reze1.model.pojo.store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/8/2018.
 */

public class StoreResponse {


    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("ownerId")
    @Expose
    private int ownerId;

    @SerializedName("imageUrl")
    @Expose
    private String imageUrl ;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("locationLang")
    @Expose
    private double locationLang;

    @SerializedName("locationAttid")
    @Expose
    private double locationAttid;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("rate")
    @Expose
    private double rate;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLocationLang() {
        return locationLang;
    }

    public void setLocationLang(double locationLang) {
        this.locationLang = locationLang;
    }

    public double getLocationAttid() {
        return locationAttid;
    }

    public void setLocationAttid(double locationAttid) {
        this.locationAttid = locationAttid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
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
}
