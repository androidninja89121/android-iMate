package com.tieyouin.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Morning on 11/25/2015.
 */

@SuppressWarnings("deprecation")
public class BitmapUtils {

    private static float BITMAP_SCALE = 0.5f;

    public static Bitmap getRoundedTopCornersBitmap(Context context, Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = convertPixelsToDips(context, pixels);

        // final Rect topRightRect = new Rect(bitmap.getWidth() / 2, 0,
        // bitmap.getWidth(), bitmap.getHeight() / 2);
        final Rect bottomRect = new Rect(0, bitmap.getHeight() / 2,
                bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        // Fill in upper right corner
        // canvas.drawRect(topRightRect, paint);
        // Fill in bottom corners
        canvas.drawRect(bottomRect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedBottomCornersBitmap(Context context,
                                                       Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = convertDipsToPixels(context, pixels);

        final Rect topRect = new Rect(0, 0, bitmap.getWidth(),
                bitmap.getHeight() / 2);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawRect(topRect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Context context, Bitmap bitmap,
                                                int roundDips) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = convertDipsToPixels(context, roundDips);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Bitmap ì�„ ë§Œë“¤ì–´ ë¦¬í„´í•œë‹¤.
     */
    public static Bitmap create_image(Context context, int id) {

        Bitmap bitmap = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true; // declare as purgeable to disk
            bitmap = BitmapFactory.decodeResource(context.getResources(), id,
                    options);
        } catch (RuntimeException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return bitmap;
    }

    public static int convertDipsToPixels(Context context, int dips) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dips * scale + 0.5f);
    }

    public static int convertPixelsToDips(Context context, int pixs) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pixs / scale + 0.5f);
    }

    public static Bitmap blur(Context context, Bitmap image, int blurR) {

        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height,
                false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs,
                Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(blurR);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        inputBitmap.recycle();
        inputBitmap = null;

        tmpIn = null;
        tmpOut = null;

        rs.finish();
        rs.destroy();
        rs = null;

        return outputBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    public static Bitmap getCroppedImage(Bitmap source) {

        float width = source.getWidth();
        float height = source.getHeight();

        float CROP_SIZE = Math.min(width, height);

        float cropWindowX = width / 2 - CROP_SIZE / 2;
        float cropWindowY = height / 2 - CROP_SIZE / 2;

        // Crop the subset from the original Bitmap.
        Bitmap croppedBitmap = Bitmap.createBitmap(source, (int) cropWindowX,
                (int) cropWindowY, (int) CROP_SIZE, (int) CROP_SIZE);

        return croppedBitmap;

    }

    public static boolean saveOutput(File file, Bitmap bitmap) {

        try {

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return true;

        } catch (Exception ex) {

            ex.printStackTrace();
            return false;
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) {

        Bitmap returnedBitmap = null;

        try {

            InputStream in = context.getContentResolver().openInputStream(uri);

            BitmapFactory.Options bitOpt = new BitmapFactory.Options();
            bitOpt.inJustDecodeBounds = true;

            // get width and height of bitmap
            BitmapFactory.decodeStream(in, null, bitOpt);
            in.close();

            int inSampleSize = BitmapUtils.calculateInSampleSize(bitOpt,
                    Constant.PROFILE_IMAGE_SIZE, Constant.PROFILE_IMAGE_SIZE);

            bitOpt = new BitmapFactory.Options();
            bitOpt.inSampleSize = inSampleSize;

            // get bitmap
            in = context.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, bitOpt);

            in.close();

            ExifInterface ei = new ExifInterface(uri.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    returnedBitmap = BitmapUtils.rotateImage(bitmap, 90);
                    // Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    returnedBitmap = BitmapUtils.rotateImage(bitmap, 180);
                    // Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    returnedBitmap = BitmapUtils.rotateImage(bitmap, 270);
                    // Free up the memory
                    bitmap.recycle();
                    bitmap = null;
                    break;

                default:
                    returnedBitmap = bitmap;
            }

        } catch (Exception e) {
            // TODO: handle exception
        }

        return returnedBitmap;
    }

    public static File getOutputMediaFile(Context context) {

        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory() + "/android/data/"
                        + context.getPackageName() + "/temp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "temp.png");

        return mediaFile;
    }

    public static String getRealPathFromURI(Context context, Uri contentURI) {

        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null,
                null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}

