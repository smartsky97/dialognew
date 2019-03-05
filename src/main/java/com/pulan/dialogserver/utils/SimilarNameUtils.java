package com.pulan.dialogserver.utils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SimilarNameUtils {
    private volatile static SimilarNameUtils similarNameUtils;
    private SimilarNameUtils(){}

    public synchronized static SimilarNameUtils getSingle(){
        if(similarNameUtils == null){
            synchronized(SimilarNameUtils.class){
                if(similarNameUtils == null){
                    similarNameUtils = new SimilarNameUtils();
                }
            }
        }
        return similarNameUtils;
    }

    public static void main(String[] args) throws PinyinException {
        System.out.println(new Date().toString());
        String[] list = new String[]{"张三", "张衫", "张散", "张丹", "张成", "李四", "李奎", "大名", "小乔", "王峰", "冥器", "张飞", "刘启达", "文大明"};
//        SimilarNameUtils d = new SimilarNameUtils(list);
        System.out.println(SimilarNameUtils.getSingle().search(list,"张三", 10));
        System.out.println(SimilarNameUtils.getSingle().search(list,"李四", 10));
        System.out.println(new Date().toString());
    }

    public List<SimilarNameUtils.Score> search(String[] list,String input, int limit) throws PinyinException {
        List<Word> targets = new ArrayList();
        String[] var2 = list;
        int var3 = list.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String s = var2[var4];
            SimilarNameUtils.Word w = new SimilarNameUtils.Word(s);
            targets.add(w);
        }
        SimilarNameUtils.Word w = new SimilarNameUtils.Word(input);
        return (List)targets.stream().map((x) -> {
            SimilarNameUtils.Score s = new SimilarNameUtils.Score();
            s.word = x;
            s.score = x.compareTo(w);
            return s;
        }).sorted().limit((long)limit).collect(Collectors.toList());
    }

    public static int getEditDistance(String s, String t) {
        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        } else {
            int[][] d = new int[n + 1][m + 1];

            int i;
            for(i = 0; i <= n; d[i][0] = i++) {
            }

            int j;
            for(j = 0; j <= m; d[0][j] = j++) {
            }

            for(i = 1; i <= n; ++i) {
                char s_i = s.charAt(i - 1);

                for(j = 1; j <= m; ++j) {
                    char t_j = t.charAt(j - 1);
                    int cost = s_i == t_j ? 0 : 1;
                    d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
                }
            }

            return d[n][m];
        }
    }

    private static int Minimum(int a, int b, int c) {
        int im = a < b ? a : b;
        return im < c ? im : c;
    }

    public static class Score implements Comparable {
        public SimilarNameUtils.Word word;
        public int score;

        Score() {
        }

        public int compareTo(Object o) {
            return o instanceof SimilarNameUtils.Score ? this.score - ((SimilarNameUtils.Score)o).score : 0;
        }

        public String toString() {
            return "{word=" + this.word + ", score=" + this.score + '}';
        }
    }

    class Word implements Comparable {
        public final String word;
        final String pinyin1;
        final String pinyin2;

        Word(String word) throws PinyinException {
            this.word = word;
            this.pinyin1 = PinyinHelper.convertToPinyinString(word, ",", PinyinFormat.WITH_TONE_NUMBER);
            this.pinyin2 = PinyinHelper.convertToPinyinString(word, ",", PinyinFormat.WITHOUT_TONE);
        }

        public String toString() {
            return this.word;
        }

        public int compareTo(Object o) {
            if (o instanceof SimilarNameUtils.Word) {
                SimilarNameUtils.Word o1 = (SimilarNameUtils.Word)o;
                int score1 = SimilarNameUtils.getEditDistance(this.pinyin1, o1.pinyin1);
                int score2 = SimilarNameUtils.getEditDistance(this.pinyin2, o1.pinyin2);
                return score1 + score2;
            } else {
                return 0;
            }
        }
    }
}
