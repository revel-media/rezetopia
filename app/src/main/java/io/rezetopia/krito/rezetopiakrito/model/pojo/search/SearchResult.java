package io.rezetopia.krito.rezetopiakrito.model.pojo.search;

import java.util.ArrayList;

/**
 * Created by Mona Abdallh on 5/16/2018.
 */

public class SearchResult {

    ArrayList<SearchItem> searchItems;
    String cursor;


    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public ArrayList<SearchItem> getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(ArrayList<SearchItem> searchItems) {
        this.searchItems = searchItems;
    }
}
