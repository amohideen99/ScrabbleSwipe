package com.example.ahkeelmohideen.swiper;

/**
 * Created by ahkeelmohideen on 7/10/16.
 */

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Trie implements java.io.Serializable {

    private TrieNode root;

    /**
     * Constructor
     */
    public Trie() {
        root = new TrieNode();
    }

    /**
     * Adds a word to the Trie
     *
     * @param word
     */
    public void addWord(String word) {
        root.addWord(word.toLowerCase());
    }

    /**
     * Get the words in the Trie with the given
     * prefix
     *
     * @param prefix
     * @return a List containing String objects containing the words in
     * the Trie with the given prefix.
     */
   /* public List getWords(String prefix) {
        //Find the node which represents the last letter of the prefix
        TrieNode lastNode = root;
        for (int i = 0; i < prefix.length(); i++) {
            lastNode = lastNode.getNode(prefix.charAt(i));

            //If no node matches, then no words exist, return empty list
            if (lastNode == null) return new ArrayList();
        }

        //Return the words which eminate from the last node
        return lastNode.getWords();
    }*/

    public void serialize() {

        try {
// Serialize data object to a file
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Tree.ser"));
            out.writeObject(root);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
