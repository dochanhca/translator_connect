package com.example.translateconnector.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

/**
 * Created by ton on 4/2/18.
 */

public class ImagesManager {

    private static final String TAG = ImagesManager.class.getSimpleName();
    static final Rect EMPTY_RECT = new Rect();
    static int mMaxTextureSize;
    public static int MAX_IMAGE_W = 1024;
    public static int MAX_IMAGE_H = 1024;

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static File createImageFileFromURI(Context context, Uri uri) {
        InputStream in;
        OutputStream out;
        File photoFile = null;
        try {
            photoFile = ImagesManager.createImageFile();
            in = context.getContentResolver().openInputStream(uri);
            out = new FileOutputStream(photoFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();

        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException");
        }
        return photoFile;
    }

    /**
     * Decode bitmap from stream using sampling to get bitmap with the requested limit.
     */
    public static Bitmap decodeSampledBitmap(Context context, Uri uri, int reqWidth, int reqHeight) {

        if (uri.getAuthority() != null) {
            String imagePath = ImageFilePath.getPath(context, uri);
            return decodeBitmapFromFile(imagePath, reqWidth, reqHeight);
        }

        try {
            ContentResolver resolver = context.getContentResolver();

            // First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = decodeImageForOption(resolver, uri);

            // Calculate inSampleSize
            options.inSampleSize = Math.max(
                    calculateInSampleSizeByReqestedSize(options.outWidth, options.outHeight, reqWidth, reqHeight),
                    calculateInSampleSizeByMaxTextureSize(options.outWidth, options.outHeight));

            // Decode bitmap with inSampleSize se
            Bitmap bitmap = decodeImage(resolver, uri, options);
            Log.e(TAG, "Image decoded: " + bitmap.getWidth() + "-" + bitmap.getHeight());

            // Rotate bitmap
            File file = getFileFromUri(context, uri);
            if (file.exists()) {
                return rotateBitmapByExif(bitmap, file.getAbsolutePath());
            } else {
                return bitmap;
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load sampled bitmap: " + uri + "\r\n" + e.getMessage(), e);
        }
    }

    public static Bitmap decodeBitmapFromFile(String imgPath, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap decodeSampledBitmap = null;
        boolean isSuccess = false;
        while (!isSuccess) {
            try {
                isSuccess = true;
                decodeSampledBitmap = BitmapFactory.decodeFile(imgPath, options);
            } catch (OutOfMemoryError ex) {
                Log.e(TAG, "BitmapLoadUtils decode OutOfMemoryError");
                options.inSampleSize = options.inSampleSize * 2;
                isSuccess = false;
            }

        }
        return rotateBitmapByExif(decodeSampledBitmap, imgPath);
    }

    private static Bitmap rotateBitmapByExif(Bitmap bitmap, String imgPath) {
        ExifInterface exif = getExif(imgPath);
        if (exif == null) {
            return bitmap;
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        int rotationInDegrees = exifToDegrees(exifOrientation);
        return rotate(bitmap, rotationInDegrees);
    }

    private static ExifInterface getExif(String path) {
        try {
            return new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int width, int height) {
        int w = options.outWidth;
        int h = options.outHeight;
        int inSampleSize = 1;
        if (w > width || h > height) {
            int halfW = w / 2;
            int halfH = h / 2;
            while ((halfH / inSampleSize) > height
                    && (halfW / inSampleSize) > width) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degrees);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
                // if out of memory, return original bitmap
            }
        }
        return bitmap;
    }

    public static InputStream convertToInputStream(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);

        byte[] imageInByte = stream.toByteArray();
        System.out.println("........length......" + imageInByte);

        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        return bis;
    }


    /**
     * Calculate the largest inSampleSize value that is a power of 2 and keeps both
     * height and width larger than the requested height and width.
     */
    private static int calculateInSampleSizeByReqestedSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            while ((height / 2 / inSampleSize) > reqHeight && (width / 2 / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Calculate the largest inSampleSize value that is a power of 2 and keeps both
     * height and width smaller than max texture size allowed for the device.
     */
    private static int calculateInSampleSizeByMaxTextureSize(int width, int height) {
        int inSampleSize = 1;
        if (mMaxTextureSize == 0) {
            mMaxTextureSize = getMaxTextureSize();
        }
        if (mMaxTextureSize > 0) {
            while ((height / inSampleSize) > mMaxTextureSize || (width / inSampleSize) > mMaxTextureSize) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Get the max size of bitmap allowed to be rendered on the device.<br>
     * http://stackoverflow.com/questions/7428996/hw-accelerated-activity-how-to-get-opengl-texture-size-limit.
     */
    private static int getMaxTextureSize() {
        // Safe minimum default size
        final int IMAGE_MAX_BITMAP_DIMENSION = 2048;

        try {
            // Get EGL Display
            EGL10 egl = (EGL10) EGLContext.getEGL();
            EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

            // Initialise
            int[] version = new int[2];
            egl.eglInitialize(display, version);

            // Query total number of configurations
            int[] totalConfigurations = new int[1];
            egl.eglGetConfigs(display, null, 0, totalConfigurations);

            // Query actual list configurations
            EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
            egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

            int[] textureSize = new int[1];
            int maximumTextureSize = 0;

            // Iterate through all the configurations to located the maximum texture size
            for (int i = 0; i < totalConfigurations[0]; i++) {
                // Only need to check for width since opengl textures are always squared
                egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, textureSize);

                // Keep track of the maximum texture size
                if (maximumTextureSize < textureSize[0]) {
                    maximumTextureSize = textureSize[0];
                }
            }

            // Release
            egl.eglTerminate(display);

            // Return largest texture size found, or default
            return Math.max(maximumTextureSize, IMAGE_MAX_BITMAP_DIMENSION);
        } catch (Exception e) {
            return IMAGE_MAX_BITMAP_DIMENSION;
        }
    }

    /**
     * Decode image from uri using given "inSampleSize", but if failed due to out-of-memory then raise
     * the inSampleSize until success.
     */
    private static Bitmap decodeImage(ContentResolver resolver, Uri uri, BitmapFactory.Options options) throws FileNotFoundException {
        do {
            InputStream stream = null;
            try {
                stream = resolver.openInputStream(uri);
                return BitmapFactory.decodeStream(stream, EMPTY_RECT, options);
            } catch (OutOfMemoryError e) {
                options.inSampleSize *= 2;
            } finally {
                closeSafe(stream);
            }
        } while (options.inSampleSize <= 512);
        throw new RuntimeException("Failed to decode image: " + uri);
    }

    /**
     * Close the given closeable object (Stream) in a safe way: check if it is null and catch-log
     * exception thrown.
     *
     * @param closeable the closable object to close
     */
    private static void closeSafe(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Decode image from uri using "inJustDecodeBounds" to get the image dimensions.
     */
    private static BitmapFactory.Options decodeImageForOption(ContentResolver resolver, Uri uri) throws FileNotFoundException {
        InputStream stream = null;
        try {
            stream = resolver.openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, EMPTY_RECT, options);
            options.inJustDecodeBounds = false;
            return options;
        } finally {
            closeSafe(stream);
        }
    }

    /**
     * @param source
     * @return bitmap scaled with max size is full xxhdpi
     */
    public static Bitmap createScaledBitmap(Bitmap source) {
        int dstWidth = source.getWidth();
        int dstHeight = source.getHeight();
        while (dstWidth > MAX_IMAGE_W || dstHeight > MAX_IMAGE_H) {
            dstWidth = (int) (dstWidth * 0.75);
            dstHeight = (int) (dstWidth * 0.75);
        }
        return Bitmap.createScaledBitmap(source, dstWidth, dstHeight, false);
    }

    /**
     * Get {@link File} object for the given Android URI.<br>
     * Use content resolver to get real path if direct path doesn't return valid file.
     */
    public static File getFileFromUri(Context context, Uri uri) {

        // first try by direct path
        File file = new File(uri.getPath());
        if (file.exists()) {
            return file;
        }

        // try reading real path from content resolver (gallery images)
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String realPath = cursor.getString(column_index);
            file = new File(realPath);
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return file;
    }

    /**
     * @param imageFile Convert from file to bitmap, resize & rotate bitmap
     * @return content of bitmap
     */
    public static byte[] getStreamByteFromImage(final File imageFile) {

        Bitmap photoBitmap = decodeBitmapFromFile(imageFile.getPath(), MAX_IMAGE_W, MAX_IMAGE_H);
        if (photoBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);

            return stream.toByteArray();
        }
        return null;
    }

    /**
     *
     * Convert from Uri to bitmap, resize & rotate bitmap
     * @return content of bitmap
     */
    public static byte[] getStreamByteFromImage(Context context, final Uri uri) {

        Bitmap photoBitmap = decodeSampledBitmap(context, uri, MAX_IMAGE_W, MAX_IMAGE_H);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);

        return stream.toByteArray();
    }

    public static byte[] getCertificateStreams(final File imageFile) {
        Bitmap photoBitmap = decodeBitmapFromFile(imageFile.getPath(), 720, 1024);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

        return stream.toByteArray();
    }

    public static void downloadImageFromUrl(Activity activity, String uRl) {
        DownloadManager mgr = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        StringBuilder fileName = new StringBuilder("image_")
                .append(System.currentTimeMillis())
                .append(".jpg");

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle(fileName.toString())
                .setDescription("iMOk Translator.")
                .setMimeType(getMimeFromFileName(fileName.toString()))
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                        fileName.toString());

        mgr.enqueue(request);
    }

    private static String getMimeFromFileName(String fileName) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(fileName);
        return map.getMimeTypeFromExtension(ext);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
