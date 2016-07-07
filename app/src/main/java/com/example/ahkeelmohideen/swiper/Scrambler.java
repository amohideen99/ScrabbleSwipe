package com.example.ahkeelmohideen.swiper;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Scrambler extends Fragment {

    View v;
    TextView out;
    TextView combos;
    EditText field;
    //char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
   // int[] points = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        v = inflater.inflate(R.layout.scrambler, container, false);




        field = (EditText) v.findViewById(R.id.editText);
        out = (TextView) v.findViewById(R.id.textview2);
        combos = (TextView) v.findViewById(R.id.combos);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressBar.setMax(235887);
        progressBar.setVisibility(View.GONE);


        field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {

                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                    progressBar.setVisibility(View.VISIBLE);

                    String enteredWord = field.getText().toString().replaceAll("\\s+", "");

                    new LongOperation().execute(enteredWord);

                    return true;
                }
                return false;
            }
        });

        return v;
    }


    class LongOperation extends AsyncTask<String, Integer, String> {

        Integer count = 0;

        @Override
        protected String doInBackground(String... params) {
            //set Progress Bar Visible

            char[] splitWord = params[0].toCharArray();
            String combos = "";
            Boolean curLine = true;
            String line = "";


            try {
                BufferedReader reader;
                AssetManager assetManager = getActivity().getAssets();
                InputStream stream;

                stream = assetManager.open("web2.txt");
                reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {

                    count++;
                    publishProgress(count);

                    line = line.replaceAll("\\s+", "");

                    if (line.length() <= splitWord.length + 1) {

                        for (int i = 0; i < splitWord.length; i++) {

                            if (!line.contains("" + splitWord[i]))
                                curLine = false;
                        }

                        if (curLine) {
                            combos += " " + line;
                        }
                    }
                    curLine = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return combos;
        }

        @Override
        protected void onPostExecute(String result) {
            TextView txt = (TextView) v.findViewById(R.id.combos);
            progressBar.setVisibility(View.GONE);
            txt.setText(result);

        }

        @Override
        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            progressBar.setProgress(values[0]);
        }
    }
}