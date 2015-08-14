package io.indico.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by chris on 2/20/15.
 */
public class BitmapUtils {
    /**
     * SCALE DOWN A BITMAP BEFORE LOADING
     */
    public static String loadScaledBitmap(Context context, Uri uri, int width, int height) {
        if (width == 0 || height == 0)
            Log.e("BitmapHelper", "Height or Width is 0! " + width + "x" + height);
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(streamFromUri(context, uri), null, bmOptions);

        // Determine how much to scale down the image
        int scaleFactor = calculateInSampleSize(bmOptions, width, height);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return toBase64(BitmapFactory.decodeStream(streamFromUri(context, uri), null, bmOptions));
    }

    private static java.io.InputStream streamFromUri(Context context, Uri uri) {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Rect getRectangle(Map<String, List<Double>> res) {
        List<Double> topLeft = res.get("top_left_corner");
        List<Double> bottomRight = res.get("bottom_right_corner");

        int top = topLeft.get(1).intValue();
        int left = topLeft.get(0).intValue();
        int bottom = bottomRight.get(1).intValue();
        int right = bottomRight.get(0).intValue();

        return new Rect(left, top, right, bottom);
    }

    public static String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    // Bitmap Loading Helpers
    private static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0)
            Log.e("BitmapHelpers", "calculateInSampleSize was given 0 width or height");
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = (int) Math.ceil((float) height / (float) reqHeight);
            } else {
                inSampleSize = (int) Math.ceil((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    public static Bitmap loadBitmap(Context context, Uri uri) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(streamFromUri(context, uri), null, bmOptions);
    }


    public static Bitmap rotateBitmapIfNecessary(Bitmap source, Uri file) {
        int orientation = 0;
        try {
            switch (new ExifInterface(file.getPath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    orientation = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    orientation = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    orientation = 270;
                    break;
                // etc.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
