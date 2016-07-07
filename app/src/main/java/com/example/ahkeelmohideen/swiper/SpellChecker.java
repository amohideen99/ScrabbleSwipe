package com.example.ahkeelmohideen.swiper;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class SpellChecker extends Fragment {

    View v;
    TextView textView;
    EditText editText;

    char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    int[] points = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        v = inflater.inflate(R.layout.spell_checker, container, false);

        textView = (TextView) v.findViewById(R.id.textview2);
        editText = (EditText) v.findViewById(R.id.editText);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {


                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    String enteredWord = editText.getText().toString().replaceAll("\\s+", "");

                    if (checkWord(enteredWord)) {
                        textView.setText("Points Worth: " + calcPoints(enteredWord));
                        textView.setTextColor(Color.GREEN);

                    } else {
                        textView.setText("Not a Word!");
                        textView.setTextColor(Color.RED);
                    }


                    return true;
                }
                return false;
            }
        });


        return v;
    }

    public boolean checkWord(String string) {

        String line = "";

        try {
            BufferedReader reader;

            AssetManager assetManager = getActivity().getAssets();
            InputStream stream;

            stream = assetManager.open("web2.txt");
            reader = new BufferedReader(new InputStreamReader(stream));

            while ((line = reader.readLine()) != null) {
                if (string.equalsIgnoreCase(line)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public int calcPoints(String string) {

        char[] word = string.toCharArray();
        int score = 0;

        for (int i = 0; i < word.length; i++) {

            score += points[numInAlphabet(word[i])];
        }

        return score;

    }

    public int numInAlphabet(char c) {

        for (int i = 0; i < letters.length; i++) {

            if (c == letters[i])
                return i;
        }

        return -1;
    }
}