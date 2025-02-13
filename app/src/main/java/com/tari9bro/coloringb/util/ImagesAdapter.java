package com.tari9bro.coloringb.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tari9bro.coloringb.R;

/**
 * Page selection adapter providing the coloring pages from the current coloring book in a grid view.
 */
public final class ImagesAdapter extends BaseAdapter {

    private final Context context;
    private final int size; // it's the same same for all elements
    private final Library library = Library.getInstance(); // just convenience

    public ImagesAdapter(Context context) {
        this.context = context;
        this.size = context.getResources().getDimensionPixelSize(R.dimen.page_preview_width);
    }

    @Override
   public int getCount() {
       return library.getNumberPagesFromCurrentBook();
   }



    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // returns a single page preview

        // update page in library

           // library.setCurrentBook(1);

        library.setCurrentPage(position);

        View view;
        if (convertView == null) {
            view = View.inflate(context, R.layout.element__page, null);
          // view.setLayoutParams(new GridView.LayoutParams(size, size));
            view.setLayoutParams(new RecyclerView.LayoutParams(size, size));
        } else {
            view = convertView;
        }

        // customize page view
        TextView categoryNameView = view.findViewById(R.id.pageNameTextView);
        categoryNameView.setText(library.getImageName("name"));

        ImageView previewImageView = view.findViewById(R.id.pagePreviewImageView);
        Bitmap bitmap = library.loadCurrentPageBitmapDownscaled(size, size);
        bitmap = Settings.replaceColorInBitmap(bitmap, Color.WHITE, Color.TRANSPARENT);
        previewImageView.setImageBitmap(bitmap);
        return view;
    }
}
