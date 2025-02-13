package com.tari9bro.coloringb.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;

import java.util.concurrent.TimeUnit;

import com.tari9bro.coloringb.R;

public class Ads implements MaxRewardedAdListener {

    private MaxInterstitialAd interstitialAd;
    private int retry = 0;

    PreferencesHelper pref;
    public Ads(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;

        pref = new PreferencesHelper(activity);
        //AppLovinSdk.getInstance(context).setMediationProvider();
    }
    private MaxRewardedAd rewardedAd;
    private int           retryAttempt;
    private MaxAdView adView;
    private final Activity activity;
    private final Context context;

    Settings settings;

    public void loadBanner() {
        // Create an ad request.
        adView = new MaxAdView(activity.getResources().getString(R.string.bannerAd), context);
       // adView.setExtraParameter( "15", "120" );
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdLoaded(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdHidden(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdClicked(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {

            }

            @Override
            public void onAdDisplayFailed(@NonNull MaxAd ad, @NonNull MaxError error) {

            }
        });

        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = activity.getResources().getDimensionPixelSize(R.dimen.banner_height);
        adView.setLayoutParams(new FrameLayout.LayoutParams(width, height, Gravity.BOTTOM));
        adView.setBackgroundColor(Color.WHITE);

        LinearLayout layout = activity.findViewById(R.id.adLayout);
        layout.addView(adView);
        adView.loadAd();

    }
    public  void LoadInterstitialAd( ) {
        interstitialAd = new MaxInterstitialAd(activity.getResources().getString(R.string.Interstitial_id), activity);
        MaxAdListener adListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdDisplayed(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdHidden(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdClicked(@NonNull MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
                retry++;
                long delay = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retry)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        interstitialAd.loadAd();
                    }
                }, delay);

            }

            @Override
            public void onAdDisplayFailed(@NonNull MaxAd ad, @NonNull MaxError error) {

            }
        };
        interstitialAd.setListener(adListener);
        interstitialAd.loadAd();
    }

    public  void playInterstitialAd() {
        if (interstitialAd.isReady()) {
            interstitialAd.showAd();
        }
    }



    void playRewarded(){
        if ( rewardedAd.isReady() )
        {
            rewardedAd.showAd();
        }
    }
    public void loadRewarded() {
        rewardedAd = MaxRewardedAd.getInstance( activity.getString(R.string.Video_id), activity );
        rewardedAd.setListener( this );

        rewardedAd.loadAd();
    }

    // MAX Ad Listener
    @Override
    public void onAdLoaded(@NonNull final MaxAd maxAd) {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0;
    }

    @Override
    public void onAdLoadFailed(@NonNull final String adUnitId, @NonNull final MaxError error) {
        // Rewarded ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed( new Runnable()
        {
            @Override
            public void run()
            {
                rewardedAd.loadAd();
            }
        }, delayMillis );
    }

    @Override
    public void onAdDisplayFailed(@NonNull final MaxAd maxAd, @NonNull final MaxError error) {
        // Rewarded ad failed to display. We recommend loading the next ad
        rewardedAd.loadAd();
    }

    @Override
    public void onAdDisplayed(@NonNull final MaxAd maxAd) {}

    @Override
    public void onAdClicked(@NonNull final MaxAd maxAd) {}

    @Override
    public void onAdHidden(@NonNull final MaxAd maxAd) {
        // rewarded ad is hidden. Pre-load the next ad
        rewardedAd.loadAd();
    }




    @Override
    public void onUserRewarded(@NonNull final MaxAd ad, @NonNull final MaxReward reward)
    {

     pref.SaveInt("balance", 10 + pref.LoadInt("balance"));
       // reward.getAmount()
   // reward.getLabel() ;
    }
}
