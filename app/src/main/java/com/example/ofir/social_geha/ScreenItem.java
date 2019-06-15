package com.example.ofir.social_geha;

public class ScreenItem {

    String Title, Description;
    int ScreenImg;

    public ScreenItem(String title, String description, int screenImg) {
        Title = title;
        Description = description;
        ScreenImg = screenImg;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        Title = title;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        Description = description;
    }

    /**
     * @param screenImg
     */
    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }

    /**
     * @return Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * @return Description
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @return Screen Image
     */
    public int getScreenImg() {
        return ScreenImg;
    }
}
