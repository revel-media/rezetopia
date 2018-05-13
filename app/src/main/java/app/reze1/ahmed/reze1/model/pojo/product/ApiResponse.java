package app.reze1.ahmed.reze1.model.pojo.product;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 4/29/2018.
 */

public class ApiResponse {

    @SerializedName("products")
    @Expose
    private ProductResponse[] products;

    @SerializedName("error")
    @Expose
    private boolean error;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ProductResponse[] getProducts() {
        return products;
    }

    public void setProducts(ProductResponse[] products) {
        this.products = products;
    }
}
