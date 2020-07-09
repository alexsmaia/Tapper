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

import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

import pt.alexandremaia.myapplication.R;

public class Pad2Fragment extends Fragment {

    // Global Variables
    private SoundPool sp;
    private int sound01, sound02, sound03, sound04, sound05, sound06, sound07, sound08, sound09;
    private ArrayList<Integer> sounds = new ArrayList<Integer>();

    // Buttons
    ImageView sound01PadBtn, sound02PadBtn, sound03PadBtn,
            sound04PadBtn, sound05PadBtn, sound06PadBtn,
            sound07PadBtn, sound08PadBtn, sound09PadBtn;
    private ArrayList<ImageView> padSoundsBtns = new ArrayList<ImageView>();

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_pad2, container, false);

        // Set Sounds
        sp = new SoundPool.Builder().setMaxStreams(10).build();
        sound01 = sp.load(getContext(), R.raw.hang_drum_1,1);
        sound02 = sp.load(getContext(), R.raw.hang_drum_2,1);
        sound03 = sp.load(getContext(), R.raw.hang_drum_3,1);
        sound04 = sp.load(getContext(), R.raw.hang_drum_4,1);
        sound05 = sp.load(getContext(), R.raw.hang_drum_5,1);
        sound06 = sp.load(getContext(), R.raw.hang_drum_6,1);
        sound07 = sp.load(getContext(), R.raw.hang_drum_7,1);
        sound08 = sp.load(getContext(), R.raw.hang_drum_8,1);
        sound09 = sp.load(getContext(), R.raw.hang_drum_9,1);
        // Add Sounds to ARRAYLIST
        sounds.addAll(Arrays.asList(sound01, sound02, sound03, sound04, sound05, sound06, sound07, sound08, sound09));

        // Get Change Instrument BTNS
        sound01PadBtn = (ImageView) root.findViewById(R.id.sound01_pad_btn);
        sound02PadBtn = (ImageView) root.findViewById(R.id.sound02_pad_btn);
        sound03PadBtn = (ImageView) root.findViewById(R.id.sound03_pad_btn);
        sound04PadBtn = (ImageView) root.findViewById(R.id.sound04_pad_btn);
        sound05PadBtn = (ImageView) root.findViewById(R.id.sound05_pad_btn);
        sound06PadBtn = (ImageView) root.findViewById(R.id.sound06_pad_btn);
        sound07PadBtn = (ImageView) root.findViewById(R.id.sound07_pad_btn);
        sound08PadBtn = (ImageView) root.findViewById(R.id.sound08_pad_btn);
        sound09PadBtn = (ImageView) root.findViewById(R.id.sound09_pad_btn);
        // Add BTNS to ARRAYLIST
        padSoundsBtns.addAll(Arrays.asList(sound01PadBtn, sound02PadBtn, sound03PadBtn,
                sound04PadBtn, sound05PadBtn, sound06PadBtn,
                sound07PadBtn, sound08PadBtn, sound09PadBtn));

        // Set On Click Listener & Default Sound
        for (int i = 0; i < padSoundsBtns.size(); i++) {
            final int newInt = i;
            padSoundsBtns.get(i).setSoundEffectsEnabled(false);
            padSoundsBtns.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSound(sounds.get(newInt));
                }
            });
        }

        return root;

    }

    /**
     *
     * Play Sound
     *
     * @param sound
     */
    private void playSound(int sound) {
        sp.play(sound,1.0f, 1.0f, 0,0,1f);
    }


}