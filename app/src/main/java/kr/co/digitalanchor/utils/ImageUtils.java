package kr.co.digitalanchor.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Thomas on 2015-07-29.
 */
public class ImageUtils {

     public static String saveBitmap(Context context, String fileName, Drawable drawable) {

        Bitmap bitmap = null;

         FileOutputStream fileOutputStream = null;


        try {


            int width = drawable.getIntrinsicWidth();

            int height = drawable.getIntrinsicHeight();

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);

            drawable.setBounds(0, 0, width, height);

            drawable.draw(canvas);

            fileOutputStream = new FileOutputStream(context.getFileStreamPath(fileName).getPath());

            boolean result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

            if (result) {

                return context.getFileStreamPath(fileName).getPath();

            } else {

                return null;
            }

        } catch (Exception e) {

            return null;

        } finally {

            if (fileOutputStream != null) {

                try {

                    fileOutputStream.close();

                } catch (IOException e) {

                    fileOutputStream = null;
                }
            }

            if (bitmap != null) {

                if (!bitmap.isRecycled()) {

                    bitmap.recycle();
                }

                bitmap = null;
            }
        }
    }
}
