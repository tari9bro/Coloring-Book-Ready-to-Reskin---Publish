package com.tari9bro.coloringb.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.tari9bro.coloringb.R;

public class Library {

    private static Library instance = null;

    private final AssetManager assets;
    private final String libraryFileRootFolder;
    private final JSONArray books;
    private JSONObject currentBook;
    private JSONObject currentPage;

    public JSONObject getCurrentBook() {
        return currentBook;
    }

    private int selectedColor = Color.BLUE; // of the color picker on the coloring activity


    private Library(final Context context) {
        this.assets = context.getAssets();
        libraryFileRootFolder = context.getString(R.string.library_root_folder) + File.separator;

        String libraryFilePath = libraryFileRootFolder + context.getString(R.string.library_file);
        // read json
        String json;
        try {
            InputStream is = assets.open(libraryFilePath);
            json = Settings.readText(is);
        } catch (IOException e) {
            throw new RuntimeException(e);

        }

        try {
            books = new JSONArray(json);
        } catch (JSONException e) {
            throw new RuntimeException("library json content problem: top level is not a json array", e);
        }
    }


    public static Library getInstance() {
        return Settings.verifyNotNull(instance);
    }

    public static void initialize(Context context) {
        if (instance != null) {
            throw new RuntimeException("Library initialize can only be called once.");
        }

        instance = new Library(context);
    }

    public int getNumberBooks() {
        return books.length();
    }

    private JSONObject getBook(int position) {
        try {
            return books.getJSONObject(position);
        } catch (JSONException e) {
            throw new RuntimeException("library json content problem: retrieving book", e);
        }
    }


    public void setCurrentBook(int position) {
        if (position < 0 || position >= books.length()) {
            throw new RuntimeException("Invalid book position.");

        }
        currentBook = getBook(position);
    }

    public String getStringFromCurrentBook(String name) {
        try {
            return currentBook.getString(name);
        } catch (JSONException e) {
            throw new RuntimeException("library json content problem: retrieving named value from book", e);
        }
    }


    public int getNumberPagesFromCurrentBook() {
        try {
            JSONArray pages = currentBook.getJSONArray("pages");
            return pages.length();
        } catch (JSONException e) {
            throw new RuntimeException("library json content problem: retrieving number of pages entries in book", e);
        }
    }


    private String getCurrentBookCoverFilePath() {
        return libraryFileRootFolder + getStringFromCurrentBook("folder") + File.separator + getStringFromCurrentBook("cover");
    }


    public Bitmap loadCurrentBookCoverBitmapDownscaled(int requiredWidth, int requiredHeight) {
        String pathName = getCurrentBookCoverFilePath();
        InputStreamProvider inputStreamProvider = getInputStreamProviderForAssetPath(pathName);
        Bitmap bitmap = Settings.decodeSampledBitmapFromStream(inputStreamProvider, requiredWidth, requiredHeight);
        if (bitmap == null) {
            throw new RuntimeException("Bitmap could not be loaded!");
        }
        return bitmap;
    }


    public void setCurrentPage(int position) {
        try {
            JSONArray pages = currentBook.getJSONArray("pages");
            if (position < 0 || position >= pages.length()) {
                throw new RuntimeException("Invalid page position.");
            }
            currentPage = pages.getJSONObject(position);
        } catch (JSONException e) {
            throw new RuntimeException("library json content problem: getting a certain page from a book", e);
        }
    }


    public String getImageName(String name) {
        try {
            return currentPage.getString(name);
        } catch (JSONException e) {
            throw new RuntimeException("library json content problem: retrieving named value from page", e);
        }
    }


    private String getCurrentPageFilePath() {
        return libraryFileRootFolder + getStringFromCurrentBook("folder") + File.separator + getImageName("file");
    }


    public Bitmap loadCurrentPageBitmap() {
        String pathName = getCurrentPageFilePath();
        Bitmap bitmap = BitmapFactory.decodeStream(getInputStreamProviderForAssetPath(pathName).getStream());
        if (bitmap == null) {
            throw new RuntimeException("Page bitmap could not be loaded!");
        }
        return bitmap;
    }


    public Bitmap loadCurrentPageBitmapDownscaled(int requiredWidth, int requiredHeight) {
        String pathName = getCurrentPageFilePath();
        InputStreamProvider inputStreamProvider = getInputStreamProviderForAssetPath(pathName);
        Bitmap bitmap = Settings.decodeSampledBitmapFromStream(inputStreamProvider, requiredWidth, requiredHeight);
        if (bitmap == null) {
            throw new RuntimeException("Bitmap could not be loaded!");
        }
        return bitmap;
    }

    private InputStreamProvider getInputStreamProviderForAssetPath(final String pathName) {
        return new InputStreamProvider() {

            @Override
            public InputStream getStream() {
                InputStream is;
                try {
                    is = assets.open(pathName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return is;
            }
        };
    }


    public void setColor(int color) {
        selectedColor = color;
    }


    public int getColor() {
        return selectedColor;
    }


}
