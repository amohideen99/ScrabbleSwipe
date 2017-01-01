package com.example.ahkeelmohideen.swiper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Scrambler extends Fragment {

    View v;
    TextView out;
    TextView combos;
    EditText field;
    TrieNode trie;
    TrieNode tempTrie;
    int progressInt = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        v = inflater.inflate(R.layout.scrambler, container, false);

        //  new InstantiateTree().execute();

        field = (EditText) v.findViewById(R.id.editText);
        combos = (TextView) v.findViewById(R.id.combos);

        File file = new File(new File(this.getContext().getFilesDir(), "") + File.separator + "tree.ser");

        if (!file.exists()) {

            trie = createTree();

        } else
            trie = readInTree();


        field.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {

                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);


                    String enteredWord = field.getText().toString().replaceAll("\\s+", "");

                    new LongOperation().execute(enteredWord);

                    return true;
                }
                return false;
            }
        });

        return v;
    }

    public static String toString(ArrayList<Word> words) {

        ArrayList<Word> copy = words;
        ArrayList<Word> newWord = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {
            newWord.add(words.get(getBiggestWord(copy)));
            copy.remove(words.get(getBiggestWord(copy)));
        }
        String end = "";
        for (int i = 0; i < newWord.size(); i++) {
            end += "\n" + newWord.get(i).getWord() + " " + newWord.get(i).getPoints();
        }

        return end;
    }

    public static int getBiggestWord(ArrayList<Word> words) {

        int biggest = words.get(0).getPoints();

        for (int i = 0; i < words.size(); i++) {

            if (biggest < words.get(i).getPoints()) {

                biggest = words.get(i).getPoints();
            }
        }

        for (int i = 0; i < words.size(); i++) {

            if (biggest == words.get(i).getPoints()) {
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

                    if (j > 0) {
                        curr[j - 1] = temp;
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

    public TrieNode createTree() {

        tempTrie = new TrieNode();
        String line = "";

        try {
            BufferedReader reader;

            AssetManager assetManager = getActivity().getAssets();
            InputStream stream;

            stream = assetManager.open("web2.txt");
            reader = new BufferedReader(new InputStreamReader(stream));

            while ((line = reader.readLine()) != null) {

                progressInt++;
                tempTrie.addWord(line.replaceAll("[^A-Za-z]\\s+", ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        tempTrie.Serialize(tempTrie, this.getContext());

        return tempTrie;
    }

    public TrieNode readInTree() {

        TrieNode trieNode;
        try {
            FileInputStream input = new FileInputStream(new File(new File(this.getContext().getFilesDir(), "") + File.separator + "tree.ser"));
            InputStream buffer = new BufferedInputStream(input);
            ObjectInputStream stream = new ObjectInputStream(buffer);
            trieNode = (TrieNode) stream.readObject();
            Log.v("serialization", "Completed");
            input.close();
            return trieNode;
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

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
            for (int i = 0; i < allCombos.length; i++) {

                results.addAll(trie.getWords(allCombos[i], trie));
            }
            for (int j = 0; j < results.size(); j++) {

                if (results.get(j).length() > params[0].length() + 1) {

                    results.remove(j);
                }

            }

            for (int z = 0; z < results.size(); z++) {

                words.add(new Word(results.get(z), spellChecker.calcPoints(results.get(z))));
            }

            return Scrambler.toString(words);
        }

        @Override
        protected void onPostExecute(String result) {
            TextView txt = (TextView) v.findViewById(R.id.combos);
            txt.setText(result);

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    /*class InstantiateTree extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //set Progress Bar Visible

           trie = readInTree();
            return "Complete";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }*/
}