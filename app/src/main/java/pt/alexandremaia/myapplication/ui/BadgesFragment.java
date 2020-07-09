/*
 * Copyright 2020 Alexandre Maia. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.alexandremaia.myapplication.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;

import pt.alexandremaia.myapplication.R;

public class BadgesFragment extends Fragment {

    // Share Preferences Variables
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String BADGE_TAP = "badgeTap";
    private static final String BADGE_BACK = "badgeBack";
    private static final String BADGE_BACK_DATE = "badgeBackDate";

    // Global Variables
    private int badgeTap, badgeBack;
    private long badgeBackDate;

    // Set Badges Values
    private int badge_tap_bronze_int = 20;
    private int badge_tap_silver_int = 80;
    private int badge_tap_gold_int = 200;
    private int badge_back_bronze_int = 5;
    private int badge_back_silver_int = 15;
    private int badge_back_gold_int = 40;

    private long mill24h = 86400000;

    // Badges ImageViews
    ImageView badge_tap_bronze, badge_tap_silver, badge_tap_gold, badge_back_bronze, badge_back_silver, badge_back_gold;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        // Inflate View
        View root = inflater.inflate(R.layout.fragment_badges, container, false);

        // Get ImageViews
        badge_tap_bronze = (ImageView) root.findViewById(R.id.badge_tap_bronze);
        badge_tap_silver = (ImageView) root.findViewById(R.id.badge_tap_silver);
        badge_tap_gold = (ImageView) root.findViewById(R.id.badge_tap_gold);
        badge_back_bronze = (ImageView) root.findViewById(R.id.badge_back_bronze);
        badge_back_silver = (ImageView) root.findViewById(R.id.badge_back_silver);
        badge_back_gold = (ImageView) root.findViewById(R.id.badge_back_gold);

        // Get Current Date
        long nowMills = System.currentTimeMillis();

        // Get Badgets Data
        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        badgeTap = sharedPreferences.getInt(BADGE_TAP, 0);
        badgeBack = sharedPreferences.getInt(BADGE_BACK, 1);
        badgeBackDate = sharedPreferences.getLong(BADGE_BACK_DATE, 0);

        // badgeTap = 200;
        // badgeBack = 200;

        // Check if new date
        if (nowMills - mill24h > badgeBackDate) {
            // Add + to Badge Back
            badgeBack++;
            // Save Badge Back
            sharedPreferences.edit().putInt(BADGE_BACK, badgeBack).apply();
            sharedPreferences.edit().putLong(BADGE_BACK_DATE, nowMills).apply();
        }

        // Set Badges
        setBadges();

        return root;

    }

    private void setBadges() {
        // Tap Badges
        if (badgeTap >= badge_tap_bronze_int) {
            ImageViewCompat.setImageTintList(badge_tap_bronze, ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.bronze)));
        }
        if (badgeTap >= badge_tap_silver_int) {
            ImageViewCompat.setImageTintList(badge_tap_silver, ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.silver)));
        }
        if (badgeTap >= badge_tap_gold_int) {
            ImageViewCompat.setImageTintList(badge_tap_gold, ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.gold)));
        }
        // Back Badges
        if (badgeBack >= badge_back_bronze_int) {
            ImageViewCompat.setImageTintList(badge_back_bronze, ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.bronze)));
        }
        if (badgeBack >= badge_back_silver_int) {
            ImageViewCompat.setImageTintList(badge_back_silver, ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.silver)));
        }
        if (badgeBack >= badge_back_gold_int) {
            ImageViewCompat.setImageTintList(badge_back_gold, ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.gold)));
        }
    }


}