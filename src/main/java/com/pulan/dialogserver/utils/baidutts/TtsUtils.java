package com.pulan.dialogserver.utils.baidutts;

import com.pulan.dialogserver.api.RobotRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class TtsUtils {
    private static Logger logger = LogManager.getLogger(TtsUtils.class);
    private static TtsUtils ttsUtils = new TtsUtils();
    private static TokenHolder holder;
    private TtsUtils(){}

    //  填写网页上申请的appkey 如 $apiKey="g8eBUMSokVB1BHGmgxxxxxx"
    private static final String appKey = "ZcL3uxCrjKcZW73Z1POdamOO";

    // 填写网页上申请的APP SECRET 如 $secretKey="94dc99566550d87f8fa8ece112xxxxx"
    private static final String secretKey = "RujZIvc4FObKiGuBxLL2da47uRVF3rfW";

    // text 的内容为"欢迎使用百度语音合成"的urlencode,utf-8 编码
    // 可以百度搜索"urlencode"

    // 发音人选择, 0为普通女声，1为普通男生，3为情感合成-度逍遥，4为情感合成-度丫丫，默认为普通女声
    private static final int per = 0;
    // 语速，取值0-15，默认为5中语速
    private static final int spd = 5;
    // 音调，取值0-15，默认为5中语调
    private static final int pit = 5;
    // 音量，取值0-9，默认为5中音量
    private static final int vol = 5;

    // 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
    private static final int aue = 6;

    public static final String url = "http://tsn.baidu.com/text2audio"; // 可以使用https

    private static String cuid = "1234567JAVA";

    static {
        //获取token
        holder = new TokenHolder(appKey, secretKey, TokenHolder.ASR_SCOPE);
    }

    // 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
    private static String getFormat(int aue) {
        String[] formats = {"mp3", "pcm", "pcm", "wav"};
        return formats[aue - 3];
    }

    public static String saveVoice(String text,String filename) throws IOException, DemoException, JSONException {
        String token = holder.getToken();

        // 此处2次urlencode， 确保特殊字符被正确编码
        String params = "tex=" + ConnUtil.urlEncode(ConnUtil.urlEncode(text));
        params += "&per=" + per;
        params += "&spd=" + spd;
        params += "&pit=" + pit;
        params += "&vol=" + vol;
        params += "&cuid=" + cuid;
        params += "&tok=" + token;
        params += "&aue=" + aue;
        params += "&lan=zh&ctp=1";
        System.out.println(url + "?" + params); // 反馈请带上此url，浏览器上可以测试
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
        printWriter.write(params);
        printWriter.close();
        String contentType = conn.getContentType();
        String path="";
        if (contentType.contains("audio/")) {
            byte[] bytes = ConnUtil.getResponseBytes(conn);
            String format = getFormat(aue);
            File file = new File("/home/pulan/huaxiaomi/dialog-server/tts/result"+ UUID.randomUUID() +"."+ format); // 打开mp3文件即可播放
            FileOutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
            path = file.getName();
            System.out.println("audio file write to " + path);
            logger.info("文件路径："+file.getAbsolutePath());
        } else {
            System.err.println("ERROR: content-type= " + contentType);
            String res = ConnUtil.getResponseString(conn);
            System.err.println(res);
            if (res.contains(":502,")) {
                holder.refresh();
                return saveVoice(text,filename);
            }
        }
        return path;
    }
}
