package com.taba.apps.retirementapp.extra;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Date;

public class Tool {

    public static Transformation getImageTransformation() {
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(1)
                .cornerRadiusDp(30)
                .oval(false)
                .build();
        return transformation;
    }

    public static String getString(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static String getCurrentTimeStamp() {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp.toString()
                .replace(":", "")
                .replace("-", "")
                .replace(" ", "")
                .replace(".", "");

    }


}
