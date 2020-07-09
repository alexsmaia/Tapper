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
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import pt.alexandremaia.myapplication.ObjectSerializer;
import pt.alexandremaia.myapplication.R;

public class PadFragment extends Fragment {

    // Handler
    private HandlerThread handlerThread = new HandlerThread("HandlerThread");
    private Handler threadHandler;

    // Share Preferences Variables
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TAP_TIMINGS = "tapTimings";
    private static final String TAP_TIMINGS_REDUCED = "tapTimingsReduced";
    private static final String SELECTED_BTN = "selectedBtn";
    private static final String BADGE_TAP = "badgeTap";

    // Global Variables
    private SoundPool sp;
    private int sound01, sound02, sound03, sound04, sound05, sound06;
    private ArrayList<Integer> sounds = new ArrayList<Integer>();
    private int selectedSound, selectedInt;
    private int waitingTime = 1800;
    private int tapsCount = 0;
    private long startTime = 0;
    private long difference;
    private boolean rhythmCreate = false;
    private boolean playingChain = false;
    private Random random = new Random();
    private int badgeTap;

    // Global Arraylists
    private ArrayList<Integer> tapTimingsMoment = new ArrayList<Integer>();
    private ArrayList<Integer> tapTimings = new ArrayList<Integer>();
    private ArrayList<Integer> tapTimingsReduced = new ArrayList<Integer>();
    private ArrayList<Integer> markovTimings = new ArrayList<Integer>();

    // Buttons
    ImageButton sound01SelectBtn, sound02SelectBtn, sound03SelectBtn,
            sound04SelectBtn, sound05SelectBtn, sound06SelectBtn;
    private ArrayList<ImageButton> instrumentsBtns = new ArrayList<ImageButton>();
    ImageButton padBtn;
    ImageButton selectedBtn;

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_pad, container, false);

        // Disable defaul button sound
        padBtn = (ImageButton) root.findViewById(R.id.pad_btn);
        padBtn.setSoundEffectsEnabled(false);

        // Set Sounds
        sp = new SoundPool.Builder().setMaxStreams(10).build();
        sound01 = sp.load(getContext(), R.raw.drum,1);
        sound02 = sp.load(getContext(), R.raw.chimes,1);
        sound03 = sp.load(getContext(), R.raw.harmonica,1);
        sound04 = sp.load(getContext(), R.raw.maracas,1);
        sound05 = sp.load(getContext(), R.raw.piano,1);
        sound06 = sp.load(getContext(), R.raw.xylophone,1);
        // Add Sounds to ARRAYLIST
        sounds.addAll(Arrays.asList(sound01, sound02, sound03, sound04, sound05, sound06));

        // Get Change Instrument BTNS
        sound01SelectBtn = (ImageButton) root.findViewById(R.id.sound01_select_btn);
        sound02SelectBtn = (ImageButton) root.findViewById(R.id.sound02_select_btn);
        sound03SelectBtn = (ImageButton) root.findViewById(R.id.sound03_select_btn);
        sound04SelectBtn = (ImageButton) root.findViewById(R.id.sound04_select_btn);
        sound05SelectBtn = (ImageButton) root.findViewById(R.id.sound05_select_btn);
        sound06SelectBtn = (ImageButton) root.findViewById(R.id.sound06_select_btn);
        // Add BTNS to ARRAYLIST
        instrumentsBtns.addAll(Arrays.asList(sound01SelectBtn, sound02SelectBtn, sound03SelectBtn, sound04SelectBtn, sound05SelectBtn, sound06SelectBtn));
        // Get Default Sound
        sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        selectedInt = sharedPreferences.getInt(SELECTED_BTN, 1);
        // Set On Click Listener & Default Sound
        for (int i = 0; i < instrumentsBtns.size(); i++) {
            final int finalInt = i;
            if (selectedInt == i + 1) {
                changeSound(instrumentsBtns.get(finalInt), sounds.get(finalInt), finalInt + 1);
            }
            instrumentsBtns.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!playingChain && !rhythmCreate) {
                        changeSound(instrumentsBtns.get(finalInt), sounds.get(finalInt), finalInt + 1);
                    }
                }
            });
        }

        // Get Stored Data
        try {
            tapTimings = (ArrayList<Integer>) ObjectSerializer.deserialize(sharedPreferences.getString(TAP_TIMINGS, ObjectSerializer.serialize(new ArrayList<Integer>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            tapTimingsReduced = (ArrayList<Integer>) ObjectSerializer.deserialize(sharedPreferences.getString(TAP_TIMINGS_REDUCED, ObjectSerializer.serialize(new ArrayList<Integer>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        badgeTap = sharedPreferences.getInt(BADGE_TAP, 0);

        // Set Tap BTN Touch Listner
        padBtn.setOnTouchListener(new Button.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (!playingChain) {
                    getMotionEvent(view, motionEvent);
                    return false;
                }
                return true;
            }
        });

        // Start Handler
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper());

        return root;

    }

    /**
     *
     * Check if Taping is over
     */
    private void checkTapsOver(View view) {

        if (startTime > 0 && rhythmCreate) {
            // If Times Up
            if (System.currentTimeMillis() - startTime >= waitingTime) {

                // Disable Pad
                // padBtn.setEnabled(false);

                // Set Rhythm Creating
                rhythmCreate = false;

                // Reset Start Time
                startTime = 0;

                // Get Number of Taps
                tapsCount = tapTimingsMoment.size();

                // Check Taps Count
                if (tapsCount > 0) {
                    // Create Markov Chain
                    createMarkovChain();
                    //
                    // Play New Markov Chain
                    playChain(view, markovTimings);
                } else {
                    playSound();
                }

                // Update Badge tap
                badgeTap++;

                // Save Arrays
                try {
                    sharedPreferences.edit().putString(TAP_TIMINGS, ObjectSerializer.serialize(tapTimings)).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sharedPreferences.edit().putString(TAP_TIMINGS_REDUCED, ObjectSerializer.serialize(tapTimingsReduced)).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sharedPreferences.edit().putInt(BADGE_TAP, badgeTap).apply();


            }
        }
    }

    /**
     *
     * Create a Markov Chain Sequence with input sequence
     */
    private void createMarkovChain() {
        // Get First Timing
        int selected_timing = tapTimingsMoment.get(0);
        // Add first element to Chain
        markovTimings.add(selected_timing);
        // Create New Markov Chain with same number of taps
        for (int i = 1; i < tapsCount + 1; i++) {
            // Get Next Markov Chain Element
            int newTimming = newRamdomTiming(selected_timing);
            // Add new Element to Chain
            markovTimings.add(newTimming);
            // Change Selected Timming
            selected_timing = newTimming;
        }
    }

    /**
     *
     * Find Sequences whith selected time and return one of them randomly
     *
     * @param selectedTime to find
     * @return ramdom timming from arraylist
     */
    private Integer newRamdomTiming(int selectedTime) {
        // Set Probability Array List
        ArrayList<Integer> probabilityTimings = new ArrayList<Integer>();
        // Itenerates Array to Create Probability Timings Array
        for (int i = 0; i < tapTimingsReduced.size(); i++) {
            if (tapTimingsReduced.get(i) == selectedTime) {
                probabilityTimings.add(tapTimingsReduced.get(i+1));
            }
        }
        // Get Random Value in scope
        int randomArrayValue = random.nextInt(tapTimingsReduced.size());
        // Return New Timing
        return tapTimings.get(randomArrayValue);
    }

    /**
     *
     * Update Rhythm with timing between taps
     */
    private void updateRhythm() {
        if (!playingChain) {
            // If first tap
            if (startTime == 0) {
                // Set Rhythm Create
                rhythmCreate = true;
                // Set StartTime
                startTime = System.currentTimeMillis();
                // Disable Instrument Change
                for (ImageButton disInstru : instrumentsBtns) {
                    // disInstru.setEnabled(false);
                }
            } else {
                // Get difference between taps
                difference = System.currentTimeMillis() - startTime;
                // Add Time Difference
                tapTimingsMoment.add((int) difference);
                tapTimings.add((int) difference);
                // Add Reduce time diference
                long differenceSec = difference / 60;
                tapTimingsReduced.add((int) differenceSec);
                // Reset StartTime
                startTime = System.currentTimeMillis();
            }
        }
    }

    /**
     *
     * Play timing sequence from arraylist
     *
     * @param chain
     */
    private void playChain(View view, @NonNull ArrayList<Integer> chain) {

        //Set Playing Chain
        playingChain = true;

        // Set Total Time
        int totalTime = 0;
        // Iterate Markov Chain
        for(final int chainElem : chain) {
            // Add time
            totalTime += chainElem;
            // Play Sound After item Time
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    playSound();
                }
            }, totalTime);
        }
        SystemClock.sleep(totalTime + 1000);
        // Enable BTNS
        for(ImageButton disInstru : instrumentsBtns) {
            // disInstru.setEnabled(true);
        }

        //ImageButton sound01SelectBtn2 = (ImageButton) view.findViewById(R.id.sound01_select_btn);
        //sound01SelectBtn2.setEnabled(true);
        //padBtn.setEnabled(true);

        // Set Rhythm Creat & Playing Chain
        rhythmCreate = false;
        playingChain = false;

        // Clear ArrayLists
        tapTimingsMoment.clear();
        markovTimings.clear();

    }

    /**
     *
     * @param newSelectedBtn
     * @param newSound
     * @param newSoundInt
     */
    private void changeSound(ImageButton newSelectedBtn, int newSound, int newSoundInt) {
        if (selectedBtn != null) {
            if (selectedBtn != newSelectedBtn) {
                selectedBtn.setSelected(false);
                selectedBtn = newSelectedBtn;
            }
        } else {
            selectedBtn = newSelectedBtn;
        }
        selectedBtn.setSelected(true);
        selectedSound = newSound;
        // Save new sound
        sharedPreferences.edit().putInt(SELECTED_BTN, newSoundInt).apply();
    }

    /**
     *
     * Play Selected Sound
     */
    private void playSound() {
        sp.play(selectedSound,1.0f, 1.0f, 0,0,1f);
    }

    /**
     *
     * @param motionEvent
     */
    private void getMotionEvent(View view, @NonNull MotionEvent motionEvent) {
        final int actionPeformed = motionEvent.getAction();
        switch(actionPeformed){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (!playingChain) {
                    playSound();
                    updateRhythm();
                    // Set New Thread
                    threadHandler.post(new timeCheck(view));
                }
                break;
        }
    }

    /**
     *
     */
    private class timeCheck implements Runnable {
        private final View view;

        public timeCheck(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            SystemClock.sleep(waitingTime);
            if(getView() != null) {
                checkTapsOver(view);
            }
        }
    }

}