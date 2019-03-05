package com.pulan.dialogserver.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class HelpUtils {

    //随机数，随机返回结果
    public int getRandomNumber(int max){
        Random random =new Random();
        int result =random.nextInt(max+1);
        return result;
    }
    //是否是你可以做什么。
    public boolean isDoSomething(String voicetext){
        if(voicetext.contains("你可以做什么")){
            return true;
        }
        return false;
    }

    public  boolean isEmail(String email){
        if(email==null||"".equals(email)) return false ;
        if(!containsOneWord('@',email)||!containsOneWord('.',email)) return false;
        String prefix = email.substring(0,email.indexOf("@"));
        String middle = email.substring(email.indexOf("@")+1,email.indexOf("."));
        String subfix = email.substring(email.indexOf(".")+1);

        if(prefix==null||prefix.length()>40||prefix.length()==0) return false ;
        if(!isAllWords(prefix)) return false ;
        if(middle==null||middle.length()>40||middle.length()==0) return false ;
        if(!isAllWordsAndNo(middle)) return false ;
        if(subfix==null||subfix.length()>3||subfix.length()<2) return false ;
        if(!isAllWords(subfix)) return false ;
        return true ;
    }
    //判断字符串只包含指定的一个字符c
    private boolean containsOneWord(char c , String word){
        char[] array = word.toCharArray();
        int count = 0 ;
        for(Character ch : array){
            if(c == ch) {
                count++;
            }
        }
        return count==1 ;
    }

    //检查一个字符串是否全部是字母
    private boolean isAllWords(String prefix){
        char[] array = prefix.toCharArray();
        for(Character ch : array){
            if(ch<'A' || ch>'z' || (ch<'a' && ch>'Z')) return false ;
        }
        return true;
    }

    //检查一个字符串是否包含字母和数字
    private boolean isAllWordsAndNo(String middle){
        char[] array = middle.toCharArray();
        for(Character ch : array){
            if(ch<'0' || ch > 'z') return false ;
            else if(ch >'9' && ch <'A') return false ;
            else if(ch >'Z' && ch <'a') return false ;
        }
        return true ;
    }
}
