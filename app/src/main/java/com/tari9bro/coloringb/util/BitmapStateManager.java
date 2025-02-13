package com.tari9bro.coloringb.util;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class BitmapStateManager {
    private final List<Bitmap> bitmapList;
    private int currentIndex;
    private final ColoringView coloringView;

    public BitmapStateManager(ColoringView coloringView) {
        bitmapList = new ArrayList<>();
        currentIndex = -1;
        this.coloringView = coloringView;
    }

    public void saveBitmap(Bitmap bitmap) {
        // Remove any bitmaps ahead of the current position in the list
        if (currentIndex < bitmapList.size() - 1) {
            bitmapList.subList(currentIndex + 1, bitmapList.size()).clear();
        }

        // Add the new bitmap to the list
        Bitmap bitmapCopy = Bitmap.createBitmap(bitmap);
        bitmapList.add(bitmapCopy);
        currentIndex++;
    }

    public void restoreBitmap(boolean moveForward) {
        if (moveForward && currentIndex < bitmapList.size() - 1) {
            currentIndex++;
        } else if (!moveForward && currentIndex > 0) {
            currentIndex--;
        }

        if (currentIndex >= 0 && currentIndex < bitmapList.size()) {
            Bitmap restoredBitmap = bitmapList.get(currentIndex);
            coloringView.setBitmap(restoredBitmap); // Update the ColoringView with the restored bitmap
            coloringView.invalidate(); // Trigger a redraw of the ColoringView
        }
    }
}
