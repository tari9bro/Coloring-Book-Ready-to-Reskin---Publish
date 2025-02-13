package com.tari9bro.coloringb;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tari9bro.coloringb.util.Ads;
import com.tari9bro.coloringb.util.ColoringView;
import com.tari9bro.coloringb.util.ImageUtils;
import com.tari9bro.coloringb.util.ImagesAdapter;
import com.tari9bro.coloringb.util.Library;
import com.tari9bro.coloringb.util.Settings;
import com.tari9bro.coloringb.util.PreferencesHelper;

public class MainActivity extends AppCompatActivity {
     Settings settings;

    PreferencesHelper pref;
    ColoringView coloringView;
    GridView gridView;
    MenuItem pickerItem, booksItem,backItem,saveItem, Backward,Forward;
    Ads ads;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ImageUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, this, coloringView.getBitmap());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.Forward) {

            coloringView.Forward();
            return true;
        }
        if (item.getItemId() == R.id.Backward) {

            coloringView.Backward();
            return true;
        }
        if (item.getItemId() == R.id.pickerItem){

            settings.changeColorDialog(pickerItem);
            return true;
         }
        if (item.getItemId() == R.id.backItem) {

                deactivateColoringView();
            return true;
        }
        if (item.getItemId() == R.id.Books) {
                settings.changeBooksDialog();
                return true;
        }
        if (item.getItemId() == R.id.setting) {

            settings.settingsDialog();
            return true;
        }
        if (item.getItemId() == R.id.saveItem) {


            ImageUtils.saveImageToInternalStorage(this, coloringView.getBitmap());
            return true;
        }
                return super.onOptionsItemSelected(item);
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        pickerItem = menu.findItem(R.id.pickerItem);
        booksItem = menu.findItem(R.id.Books);
        backItem = menu.findItem(R.id.backItem);
        saveItem = menu.findItem(R.id.saveItem);
        Backward = menu.findItem(R.id.Backward);
        Forward = menu.findItem(R.id.Forward);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if ( Library.getInstance().getCurrentBook()==null){
        Library.getInstance().setCurrentBook(1);}
        settings = new Settings(MainActivity.this,this);
         ads = new Ads(MainActivity.this,this);
        ads.loadBanner();
        ads.LoadInterstitialAd();
        ads.loadRewarded();
         coloringView = findViewById(R.id.coloringView);


        //findViewById(R.id.floatingActionButton).setOnClickListener(clickListenerHelper);
       // findViewById(R.id.changeApi).setOnClickListener(clickListenerHelper);
        // populate the grid view using a PageSelectionAdapter
        gridView = findViewById(R.id.pageSelectionGridView);
       gridView.setAdapter(new ImagesAdapter(this));

        // on item click listener for grid view, start coloring
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            // current page in library
            Library.getInstance().setCurrentPage(position);

            ads.playInterstitialAd();
            activateColoringView();
        });

        pref = new PreferencesHelper(MainActivity.this);
        settings = new Settings(MainActivity.this,this);



    }

    private void activateColoringView() {
        coloringView.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
        booksItem.setVisible(false);
        backItem.setVisible(true);
        pickerItem.setVisible(true);
        saveItem.setVisible(true);
        Backward.setVisible(true);
        Forward.setVisible(true);
        settings.setMenuItemIconColor(pickerItem,Library.getInstance().getColor());
      //  settings.setMenuItemIconColor(pickerItem,pref.LoadInt("color"));
        ViewTreeObserver vto = coloringView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Settings.removeOnGlobalLayoutListener(coloringView, this);

                // load and set the coloring page bitmap after the ColoringView has been laid out and knows its size
                Bitmap bitmap = Library.getInstance().loadCurrentPageBitmap();
                coloringView.resetSavedBitmaps();
                coloringView.setInitialBitmap(bitmap);
                coloringView.setBitmap(bitmap);
                coloringView.invalidate();
            }
        });
        booksItem.setVisible(false);
    }
    private void deactivateColoringView() {
        gridView.setVisibility(View.VISIBLE);
        coloringView.setVisibility(View.GONE);
        booksItem.setVisible(true);
        backItem.setVisible(false);
        pickerItem.setVisible(false);
        saveItem.setVisible(false);
        Backward.setVisible(false);
        Forward.setVisible(false);
    }






}
