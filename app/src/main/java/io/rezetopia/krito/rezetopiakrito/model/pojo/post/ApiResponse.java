package io.rezetopia.krito.rezetopiakrito.model.pojo.post;

import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.EventResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.GroupPostResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed.VendorPostsResponse;
import io.rezetopia.krito.rezetopiakrito.model.pojo.product.ProductResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mona Abdallh on 4/12/2018.
 */

public class ApiResponse {

    @SerializedName("next_cursor")
    @Expose
    private int nextCursor;

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("posts")
    @Expose
    private PostResponse[] posts;

    @SerializedName("products")
    @Expose
    private ProductResponse[] products;

    @SerializedName("events")
    @Expose
    private EventResponse[] events;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("group_posts")
    @Expose
    private GroupPostResponse[] groupPosts;


    @SerializedName("vendor_posts")
    @Expose
    private VendorPostsResponse[] vendorPosts;

    @SerializedName("now")
    @Expose
    private long now;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public EventResponse[] getEvents() {
        return events;
    }

    public void setEvents(EventResponse[] events) {
        this.events = events;
    }

    public GroupPostResponse[] getGroupPosts() {
        return groupPosts;
    }

    public void setGroupPosts(GroupPostResponse[] groupPosts) {
        this.groupPosts = groupPosts;
    }

    public VendorPostsResponse[] getVendorPosts() {
        return vendorPosts;
    }

    public void setVendorPosts(VendorPostsResponse[] vendorPosts) {
        this.vendorPosts = vendorPosts;
    }

    public ProductResponse[] getProducts() {
        return products;
    }

    public void setProducts(ProductResponse[] products) {
        this.products = products;
    }

    public int getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(int nextCursor) {
        this.nextCursor = nextCursor;
    }

    public PostResponse[] getPosts() {
        return posts;
    }

    public void setPosts(PostResponse[] posts) {
        this.posts = posts;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }
}
