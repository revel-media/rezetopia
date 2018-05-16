package io.rezetopia.krito.rezetopiakrito.model.pojo.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/5/2018.
 */

public class SearchVendorResponse {

    @SerializedName("vendor_id")
    @Expose
    private int vendorId;

    @SerializedName("vendor_name")
    @Expose
    private String vendorName;

    @SerializedName("description_name")
    @Expose
    private String vendorDescription;


    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVendorDescription() {
        return vendorDescription;
    }

    public void setVendorDescription(String vendorDescription) {
        this.vendorDescription = vendorDescription;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }
}
