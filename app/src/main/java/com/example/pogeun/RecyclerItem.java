package com.example.pogeun;

import android.graphics.drawable.Drawable;

public class RecyclerItem {
    private Drawable iconDrawable;
    private String titleStr;
    private String singerStr;

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }
    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }
    public void setSingerStr(String singerStr) {
        this.singerStr = singerStr;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }
    public String getTitleStr() {
        return titleStr;
    }
    public String getSingerStr() {
        return singerStr;
    }
}
