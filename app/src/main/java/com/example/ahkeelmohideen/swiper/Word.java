package com.example.ahkeelmohideen.swiper;

/**
 * Created by ahkeelmohideen on 7/9/16.
 */
public class Word {

    String word;
    int points;

    public Word(String s, int n){

        word = s;
        points = n;
    }

    public int getPoints(){return points;}

    public String getWord(){return word;}

}
