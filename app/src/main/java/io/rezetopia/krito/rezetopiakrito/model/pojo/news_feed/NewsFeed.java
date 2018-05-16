package io.rezetopia.krito.rezetopiakrito.model.pojo.news_feed;

import java.util.ArrayList;

/**
 * Created by Mona Abdallh on 5/12/2018.
 */

public class NewsFeed {

    ArrayList<NewsFeedItem> items;
    int nextCursor;
    long now;

    public ArrayList<NewsFeedItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<NewsFeedItem> items) {
        this.items = items;
    }

    public int getNextCursor() {
        return nextCursor;
    }

    public void setNextCursor(int nextCursor) {
        this.nextCursor = nextCursor;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }
}
