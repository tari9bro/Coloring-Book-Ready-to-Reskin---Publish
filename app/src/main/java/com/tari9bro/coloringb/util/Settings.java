package com.tari9bro.coloringb.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.github.hariprasanths.floatingtoast.FloatingToast;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tari9bro.coloringb.R;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.flag.BubbleFlag;
import com.skydoves.colorpickerview.flag.FlagMode;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.sliders.AlphaSlideBar;
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar;

import org.imaginativeworld.oopsnointernet.dialogs.pendulum.DialogPropertiesPendulum;
import org.imaginativeworld.oopsnointernet.dialogs.pendulum.NoInternetDialogPendulum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import timber.log.Timber;

public final class Settings implements View.OnClickListener {

    private final Activity activity;
    private final Context context;
    private final Ads ads;
    private ColorPickerView colorPickerView;
    private final PreferencesHelper pref;
    private final Handler handler;

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.share) {

            shareTheApp();
        }
        if (view.getId()==R.id.apps) {

            moreApps();
        }
        if (view.getId()==R.id.exit) {

            exitTheApp();
        }

    }

    public Settings(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        this.ads = new Ads(activity, context);
        this.pref = new PreferencesHelper(activity);
        this.handler = new Handler();
    }

    public Handler getHandler() {
        return handler;
    }

    public void changeColorDialog(MenuItem pickerItem) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View customView = activity.getLayoutInflater().inflate(R.layout.palette_view, null);
        setupColorPickerView(customView, pickerItem);
        builder.setView(customView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void setupColorPickerView(View customView, MenuItem pickerItem) {
        colorPickerView = customView.findViewById(R.id.colorPickerView);
        BubbleFlag bubbleFlag = new BubbleFlag(context);
        bubbleFlag.setFlagMode(FlagMode.FADE);
        colorPickerView.setFlagView(bubbleFlag);
        colorPickerView.setColorListener((ColorEnvelopeListener) (envelope, fromUser) -> {
            Timber.d("color: %s", envelope.getHexCode());
            TextView textView = customView.findViewById(R.id.textView);
            textView.setText("#" + envelope.getHexCode());
            AlphaTileView alphaTileView = customView.findViewById(R.id.alphaTileView);
            alphaTileView.setPaintColor(envelope.getColor());
            Library.getInstance().setColor(envelope.getColor());
            setMenuItemIconColor(pickerItem, envelope.getColor());
        });

        AlphaSlideBar alphaSlideBar = customView.findViewById(R.id.alphaSlideBar);
        colorPickerView.attachAlphaSlider(alphaSlideBar);

        BrightnessSlideBar brightnessSlideBar = customView.findViewById(R.id.brightnessSlide);
        colorPickerView.attachBrightnessSlider(brightnessSlideBar);
        colorPickerView.setLifecycleOwner((LifecycleOwner) activity);
    }

    public void setMenuItemIconColor(MenuItem menuItem, int color) {
        Drawable iconDrawable = menuItem.getIcon();
        if (iconDrawable != null) {
            Drawable wrappedDrawable = DrawableCompat.wrap(iconDrawable);
            DrawableCompat.setTint(wrappedDrawable, color);
            menuItem.setIcon(wrappedDrawable);
        }
    }

    public void settingsDialog() {
        View dialogView = activity.getLayoutInflater().inflate(R.layout.settings, null);
        setupSettingsDialogButtons(dialogView);
        new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create()
                .show();
    }

    private void setupSettingsDialogButtons(View dialogView) {
        dialogView.findViewById(R.id.share).setOnClickListener(this);
        dialogView.findViewById(R.id.apps).setOnClickListener(this);
        dialogView.findViewById(R.id.exit).setOnClickListener(this);
    }

    public void changeBooksDialog() {
        View dialogView = activity.getLayoutInflater().inflate(R.layout.book, null);
        setupBooksGridView(dialogView);
        new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create()
                .show();
    }

    private void setupBooksGridView(View dialogView) {
        GridView gridView = dialogView.findViewById(R.id.bookSelectionGridView);
        gridView.setAdapter(new BookAdapter(context));
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Library.getInstance().setCurrentBook(position);
            activity.recreate();
        });
    }

    public void exitTheApp() {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.exit_dialog_title)
                .setIcon(R.drawable.ic_exit)
                .setMessage(R.string.exit_dialog_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> activity.finish())
                .setNegativeButton(R.string.no, (dialogInterface, i) -> showToast(activity.getString(R.string.cancel_exit)))
                .show();
    }

    public void noInternetDialog(Lifecycle lifecycle) {
        NoInternetDialogPendulum.Builder builder = new NoInternetDialogPendulum.Builder(activity, lifecycle);
        DialogPropertiesPendulum properties = builder.getDialogProperties();
        setupNoInternetDialogProperties(properties);
        builder.build();
    }

    private void setupNoInternetDialogProperties(DialogPropertiesPendulum properties) {
        properties.setConnectionCallback(hasActiveConnection -> {
            // Handle active connection
        });
        properties.setCancelable(false);
        properties.setNoInternetConnectionTitle(activity.getString(R.string.n1));
        properties.setNoInternetConnectionMessage(activity.getString(R.string.n2));
        properties.setShowInternetOnButtons(true);
        properties.setPleaseTurnOnText(activity.getString(R.string.n3));
        properties.setWifiOnButtonText("Wifi");
        properties.setMobileDataOnButtonText("Mobile data");
        properties.setOnAirplaneModeTitle(activity.getString(R.string.n1));
        properties.setOnAirplaneModeMessage(activity.getString(R.string.n4));
        properties.setPleaseTurnOffText(activity.getString(R.string.n5));
        properties.setAirplaneModeOffButtonText(activity.getString(R.string.n6));
        properties.setShowAirplaneModeOffButtons(true);
    }

    public void moreApps() {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.developer_search))));
        } catch (android.content.ActivityNotFoundException exception) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.developer_id))));
        }
    }

    public void shareTheApp() {
        String url = activity.getString(R.string.app_link);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.share_dialog_msg));
        intent.putExtra(Intent.EXTRA_TEXT, url);
        activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_dialog_title)));
    }

    public void showToast(String text) {
        FloatingToast.makeToast(activity, text, FloatingToast.LENGTH_MEDIUM)
                .setFadeOutDuration(FloatingToast.FADE_DURATION_LONG)
                .setTextSizeInDp(25)
                .setGravity(FloatingToast.GRAVITY_MID_TOP)
                .setFloatDistance(FloatingToast.DISTANCE_LONG)
                .setTextColor(Color.parseColor("#E1B530"))
                .show();
    }

    public static String readText(InputStream is) throws IOException {
        return readText(is, "UTF-8");
    }

    public static String readText(InputStream is, String charSetName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, charSetName))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    public static void writeText(OutputStream os, String content) throws IOException {
        writeText(os, content, "UTF-8");
    }

    public static void writeText(OutputStream os, String content, String charSetName) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(os, charSetName)) {
            writer.write(content);
            writer.flush();
        }
    }

    public static void removeOnGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener listener) {
        view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getDrawable(Resources resources, int id) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ?
                resources.getDrawable(id) : resources.getDrawable(id, null);
    }

    public static void setBackground(View view, Drawable drawable) {
        view.setBackground(drawable);
    }

    public static Bitmap replaceColorInBitmap(Bitmap sourceBitmap, int sourceColor, int targetColor) {
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        int[] pixels = new int[width * height];
        sourceBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] == sourceColor) {
                pixels[i] = targetColor;
            }
        }

        Bitmap targetBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        targetBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return targetBitmap;
    }

    public static <T> T verifyNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static int calculateInSampleSize(int availableWidth, int availableHeight, int requiredWidth, int requiredHeight) {
        int inSampleSize = 1;

        if (availableHeight > requiredHeight || availableWidth > requiredWidth) {
            final int halfHeight = availableHeight / 2;
            final int halfWidth = availableWidth / 2;

            while ((halfHeight / inSampleSize) >= requiredHeight && (halfWidth / inSampleSize) >= requiredWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromStream(InputStreamProvider inputStreamProvider, int requiredWidth, int requiredHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStreamProvider.getStream(), null, options);

        options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, requiredWidth, requiredHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(inputStreamProvider.getStream(), null, options);
    }
}
