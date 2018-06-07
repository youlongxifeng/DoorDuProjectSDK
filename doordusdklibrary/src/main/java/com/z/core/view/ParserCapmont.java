package com.z.core.view;

import android.graphics.Bitmap.Config;
import android.widget.ImageView.ScaleType;

public class ParserCapmont {

    public int width;
    public int height;
    public ScaleType type;
    public int minWidth;
    public int minHeight;
    public  Config mDecodeConfig;

    public ParserCapmont(int w,int h)
    {
        this(w, h, 0, 0, ScaleType.FIT_CENTER);
    }

    public ParserCapmont(int w,int h, int mw, int mh,ScaleType t)
    {
        this.width = w;
        this.height = h;
        this.minWidth = mw;
        this.minHeight = mh;
        this.type = t;
    }

    public boolean fixMinWidth(int w)
    {
        return minWidth!=0 && width <= minWidth && w < minWidth;
    }

    public boolean fixMinHeigth(int h)
    {
        return minHeight != 0 && height <= minHeight && h < minHeight;
    }
}
