package io.rezetopia.krito.rezetopiakrito.model.pojo.store;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 5/8/2018.
 */

public class ApiResponse {

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("stores")
    @Expose
    private StoreResponse[] stores;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public StoreResponse[] getStores() {
        return stores;
    }

    public void setStores(StoreResponse[] stores) {
        this.stores = stores;
    }
}
