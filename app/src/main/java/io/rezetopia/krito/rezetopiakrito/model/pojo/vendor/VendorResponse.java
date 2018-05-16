package io.rezetopia.krito.rezetopiakrito.model.pojo.vendor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 4/29/2018.
 */

public class VendorResponse {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("location_lang")
    @Expose
    private float locationLang;

    @SerializedName("location_attid")
    @Expose
    private float locationAttid;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("name")
    @Expose
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getLocationLang() {
        return locationLang;
    }

    public void setLocationLang(float locationLang) {
        this.locationLang = locationLang;
    }

    public float getLocationAttid() {
        return locationAttid;
    }

    public void setLocationAttid(float locationAttid) {
        this.locationAttid = locationAttid;
    }

    public String getDescriptin() {
        return description;
    }

    public void setDescriptin(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
