package com.tari9bro.coloringb.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tari9bro.coloringb.R;

public class BookAdapter extends BaseAdapter {

    private final Context context;
    private final int size; // it's the same for all elements
    private final Library library = Library.getInstance(); // just for convenience

    public BookAdapter(Context context) {
        this.context = context;
        this.size = context.getResources().getDimensionPixelSize(R.dimen.book_preview_width);
    }


   @Override
    public int getCount() {
       return library.getNumberBooks();
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // returns a single book preview

        // update book in library
        library.setCurrentBook(position);

        View view;
        if (convertView == null) {
            view = View.inflate(context, R.layout.element_book, null);
            view.setLayoutParams(new GridView.LayoutParams(size, size));
        } else {
            view = convertView;
        }

        // customize book view
        TextView categoryNameView = view.findViewById(R.id.bookNameTextView);
        categoryNameView.setText(library.getStringFromCurrentBook("name"));

        ImageView previewImageView = view.findViewById(R.id.bookPreviewImageView);
        Bitmap bitmap = library.loadCurrentBookCoverBitmapDownscaled(size, size);
        bitmap = Settings.replaceColorInBitmap(bitmap, Color.WHITE, Color.TRANSPARENT);
        previewImageView.setImageBitmap(bitmap);

        return view;
    }
}
