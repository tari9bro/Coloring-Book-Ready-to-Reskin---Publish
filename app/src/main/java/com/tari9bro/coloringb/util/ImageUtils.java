package com.tari9bro.coloringb.util;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.io.OutputStream;

public final class ImageUtils {
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    public static void saveImageToInternalStorage(Context context, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            // Permission already granted, show the save image material dialog
            showSaveDialog(context, bitmap);
        }
    }

    private static void showSaveDialog(Context context, Bitmap bitmap) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Save Image");
        builder.setMessage("Enter the file name:");

        final EditText input = new EditText(context);
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = input.getText().toString().trim();
                if (!TextUtils.isEmpty(fileName)) {
                    saveBitmapToInternalStorage(context, bitmap, fileName);
                    Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Please enter a file name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private static void saveBitmapToInternalStorage(Context context, Bitmap bitmap, String fileName) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (imageUri != null) {
            try {
                OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(context, "Image saved successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Failed to create image file", Toast.LENGTH_SHORT).show();
        }
    }






    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                  @NonNull int[] grantResults, Context context, Bitmap bitmap) {
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show the save image material dialog
                showSaveDialog(context, bitmap);
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

