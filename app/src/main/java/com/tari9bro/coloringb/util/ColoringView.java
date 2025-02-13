package com.tari9bro.coloringb.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ColoringView extends View {
    // HashMap to store previous bitmap states
    public ColoringView(Context context, AttributeSet attrs) {
        super(context, attrs);
        savedBitmaps = new ArrayList<>();
        currentBitmapIndex = -1;
    }
    private final List<Bitmap> savedBitmaps;
    private int currentBitmapIndex;

    private Bitmap bitmap;
    private Vector2D offset;
    private byte[] fill_mask;
    private int[] bitmap_pixels;

    private boolean modified = false;

    @Override
    public boolean onTouchEvent (MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // get event position and correct for bitmap offsets
            Vector2D p = new Vector2D((int) event.getX() - offset.x,(int) event.getY() - offset.y);
            // test if within bitmap
            if (p.x >= 0 && p.x < bitmap.getWidth() && p.y >= 0 && p.y < bitmap.getHeight()) {
                // go for the coloring
                color(p);
                // Perform coloring and save the bitmap state
                saveBitmapState();
            }
            // because of lint (accessibility in custom views)
            performClick();
        }
        return true;
    }

    @Override
    public boolean performClick() {

        return super.performClick();
    }
    @Override
    protected void onDraw (@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // until we have a bitmap
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, offset.x, offset.y, null);
        }
    }





    public void setBitmap(Bitmap bitmap) {

        float width_scale_factor = (float) bitmap.getWidth() / getWidth();
        float height_scale_factor = (float) bitmap.getHeight() / getHeight();

        float scale_factor = Math.max(width_scale_factor, height_scale_factor);

        int width = (int) Math.floor(bitmap.getWidth() / scale_factor);
        int height = (int) Math.floor(bitmap.getHeight() / scale_factor);

        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

        offset = new Vector2D((int) Math.floor((getWidth() - width) / 2), (int) Math.floor((getHeight() - height) / 2));

        int n = width * height;
        bitmap_pixels = new int[n];
        this.bitmap.getPixels(bitmap_pixels, 0, width, 0, 0, width, height);

        fill_mask = new byte[n];
        for (int i = 0; i < n; i++) {
            // just test for the second byte (is faster)
            if (((bitmap_pixels[i] >> 16) & 0xff) == 255) {
                fill_mask[i] = 1;
            }
        }

        invalidate();
    }


    public Bitmap getBitmap() {
        return this.bitmap;
    }


    private void color(Vector2D p) {
        // get selected coloring color
        int color = Library.getInstance().getColor();

        // get size of bitmap
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (fill_mask[p.x + p.y * width] != 0 && bitmap_pixels[p.x + p.y * width] != color) {
            // copy fill_mask to temporary fill_mask
            byte[] temporary_mask = Arrays.copyOf(fill_mask, fill_mask.length);

            // perform fill
            long t0 = System.nanoTime();
            FloodFill.advanced_fill(p, temporary_mask, bitmap_pixels, width, height, color);
            // FloodFill.simple_fill(p, temporary_mask, bitmap_pixels, width, height, color);
            long t1 = System.nanoTime();

            // update bitmap
            long t2 = System.nanoTime();
            bitmap.setPixels(bitmap_pixels, 0, width, 0, 0, width, height);
            long t3 = System.nanoTime();

//            Log.v("COL", String.format("fill algorithm: %.4fms", (t1 - t0) / 1e6));
//            Log.v("COL", String.format("copy pixels:    %.4fms", (t3 - t2) / 1e6));
//            Log.v("COL", String.format("width %d, height %d", width, height));

            // set modified flag
            this.modified = true;
          //  saveBitmapState();
            // invalidate
            invalidate();
        }
    }
    public void saveBitmapState() {
        // Clear any bitmaps ahead of the current position
        if (currentBitmapIndex < savedBitmaps.size() - 1) {
            savedBitmaps.subList(currentBitmapIndex + 1, savedBitmaps.size()).clear();
        }

        // Create a copy of the current bitmap and add it to the list
        Bitmap currentBitmap = Bitmap.createBitmap(getBitmap());

       // Bitmap currentBitmap = Bitmap.createBitmap(savedBitmaps.get(currentBitmapIndex));
        savedBitmaps.add(currentBitmap);
        currentBitmapIndex++;
    }
    public void Backward() {
        if (currentBitmapIndex > 0) {
            currentBitmapIndex--;
            bitmap = savedBitmaps.get(currentBitmapIndex);
            setBitmap(bitmap);
            invalidate();
        }
    }

    public void Forward() {
        if (currentBitmapIndex < savedBitmaps.size() - 1) {
            currentBitmapIndex++;
            bitmap = savedBitmaps.get(currentBitmapIndex);
            setBitmap(bitmap);
            invalidate();
        }
    }

    public void setInitialBitmap(Bitmap initialBitmap) {
        if (savedBitmaps.isEmpty()) {
            Bitmap initialCopy = Bitmap.createBitmap(initialBitmap);
            savedBitmaps.add(initialCopy);
        }
    }
    public void resetSavedBitmaps() {
        savedBitmaps.clear();
        currentBitmapIndex = 0;
    }


    public boolean isModified() {
        return this.modified;
    }


}