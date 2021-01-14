package com.example.demo.util;

public class VoteUtil {

    public static String charactorVote(){
        String res = "";

        return res;
    }

    public static void main(String[] args) {
        String str = "54,100   139,94   182,142";
        System.out.println(str.length());
        String[] a = str.split("  ");
        for (String s : a){
            System.out.println(s);
        }
//        System.out.println(str.split("  "));
    }
}
