package com.alexqueudotrafel.retrofitrealmsample.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by alexqueudotrafel on 16/03/16.
 */
public class Question extends RealmObject{

    @PrimaryKey
    @SerializedName(value = "question_id")
    private long id;
    @Required
    private String title;
    @Required
    private String link;
    @SerializedName(value="creation_date")
    private long unixDate;

    public Question() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getUnixDate() {
        return unixDate;
    }

    public void setUnixDate(long unixDate) {
        this.unixDate = unixDate;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
