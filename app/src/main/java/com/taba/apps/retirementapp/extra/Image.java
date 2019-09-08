package com.taba.apps.retirementapp.extra;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class Image {

    /*
     * Width to which an uploaded image will be transformed
     * */
    public static final int WIDTH = 640;
    /*
     * Height to which an uploaded image will be transformed
     * */
    public static final int HEIGHT = 480;
    /*
     * Orientation to which an uploaded image will be transformed
     * */
    public static final int ORIENTATION = 0;

    public static Bitmap resize(Bitmap bitmap) {

        int currentWidth = bitmap.getWidth();
        int currentHeight = bitmap.getHeight();
        float scaleWidth = ((float) WIDTH) / currentWidth;
        float scaleHeight = ((float) HEIGHT) / currentHeight;

        Matrix matrix = new Matrix();
        matrix.postRotate(ORIENTATION);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resized = Bitmap.createBitmap(bitmap, 0, 0, currentWidth, currentHeight, matrix, true);

        return resized;
    }
}
