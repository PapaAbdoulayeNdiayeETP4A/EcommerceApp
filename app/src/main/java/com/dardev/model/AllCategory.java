package com.dardev.model;

public class AllCategory
{
    private String title;
    private int images;

    public AllCategory(String title, int images)
    {
        this.title = title;
        this.images = images;
    }

    public String getTitle()
    {
        return title;
    }

    public int getImages()
    {
        return images;
    }
}