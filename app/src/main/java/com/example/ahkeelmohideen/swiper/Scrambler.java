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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Scrambler extends Fragment {

    View v;
    TextView out;
    TextView combos;
    EditText field;
    ProgressBar progressBar;
    Trie trie;

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


        trie = new Trie();
        String line = "";

        try {
            BufferedReader reader;

            AssetManager assetManager = getActivity().getAssets();
            InputStream stream;

            stream = assetManager.open("web2.txt");
            reader = new BufferedReader(new InputStreamReader(stream));

            while ((line = reader.readLine()) != null) {

                trie.addWord(line.replaceAll("[^A-Za-z]\\s+", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        trie.serialize();

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

    public static String toString(ArrayList<Word> words){

        ArrayList<Word> copy = words;
        ArrayList<Word> newWord = new ArrayList<>();

        for(int i = 0; i < words.size(); i++){
            newWord.add(words.get(getBiggestWord(copy)));
            copy.remove(words.get(getBiggestWord(copy)));
        }
        String end = "";
        for(int i = 0; i < newWord.size(); i++){
            end += "\n" + newWord.get(i).getWord() + " " + newWord.get(i).getPoints();
        }

        return end;
    }

    public static int getBiggestWord(ArrayList<Word> words){

        int biggest = words.get(0).getPoints();

        for(int i = 0; i < words.size(); i++){

            if(biggest < words.get(i).getPoints()){

                biggest = words.get(i).getPoints();
            }
        }

        for(int i = 0; i < words.size(); i++){

            if(biggest == words.get(i).getPoints()){
                return i;
            }
        }
        return -1;
    }

    public String[] permute(String s) {

        if (s.length() == 1) {
            return new String[]{s};
        } else {
            ArrayList<String> words = new ArrayList<>();
            ArrayList<String> words2 = new ArrayList<>();

            Collections.addAll(words, permute(s.substring(1)));

            for (int i = 0; i < words.size(); i++) {

                char temp = s.charAt(0);
                char[] curr = words.get(i).toCharArray();

                words2.add("" + temp + new String(curr));


                for (int j = 0; j < words.get(i).length(); j++) {

                    if(j > 0){
                        curr[j-1] = temp;
                    }
                    temp = curr[j];
                    curr[j] = s.charAt(0);
                    words2.add("" + temp + new String(curr));

                }
            }

            String[] toReturn = new String[words2.size()];
            toReturn = words2.toArray(toReturn);

            return toReturn;
        }
    }


    class LongOperation extends AsyncTask<String, Integer, String> {

        Integer count = 0;
        SpellChecker spellChecker = new SpellChecker();
        ArrayList<Word> words = new ArrayList();


        @Override
        protected String doInBackground(String... params) {
            //set Progress Bar Visible

            String[] allCombos = permute(params[0]);
            List<String> results = new ArrayList<>();
            for(int i = 0; i < allCombos.length; i++) {

                results.addAll(trie.getWords(allCombos[i]));
            }
            for(int j = 0; j < results.size(); j++){

                if(results.get(j).length() > params[0].length() + 1){

                    results.remove(j);
                }

            }

            for(int z = 0; z < results.size(); z++){

                words.add(new Word(results.get(z), spellChecker.calcPoints(results.get(z))));
            }





            /*String line = "";


            try {
                BufferedReader reader;
                AssetManager assetManager = getActivity().getAssets();
                InputStream stream;

                stream = assetManager.open("web2.txt");
                reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {

                    count++;
                    publishProgress(count);

                    line = line.replaceAll("\\s+", "").toLowerCase();

                    if (line.length() <= splitWord.length + 1) {

                        for (int i = 0; i < splitWord.length; i++) {

                            if (!line.contains("" + splitWord[i]))
                                curLine = false;
                        }

                        if (curLine) {
                            words.add(new Word(line, spellChecker.calcPoints(line)));
                        }
                    }
                    curLine = true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }*/

            return Scrambler.toString(words);
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