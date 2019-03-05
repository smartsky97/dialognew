package com.pulan.dialogserver;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Test {

    public ShardedJedisPool shardedJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(2);
        jedisPoolConfig.setMaxIdle(1);
        jedisPoolConfig.setMaxWaitMillis(2000);
        List<JedisShardInfo> jedisShardInfos = new ArrayList<>();
        JedisShardInfo jedisShardInfo =new JedisShardInfo("192.168.0.112", 6379, 3000);
        jedisShardInfo.setPassword("AI-assist-MQ");
        jedisShardInfos.add(jedisShardInfo);
        ShardedJedisPool jedisPool = new ShardedJedisPool(jedisPoolConfig, jedisShardInfos);
        return jedisPool;
    }


    public static void main(String[] args){
        Test test = new Test();
        /*ShardedJedisPool jedisPool = test.shardedJedisPool();
        //进行查询等其他操作
        ShardedJedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.pipelined();
            jedis.set("test", "test");
            jedis.set("test1", "test1");
            jedis.expire("test",20);
            //jedis.set("test","001");
           // jedis.del("test");
            String lkm = jedis.get("test");
            System.out.println(lkm);
        } finally {
            //使用后一定关闭，还给连接池
            if (jedis != null) {
                jedis.close();
            }
        }*/
       /* DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse("2017-11-15 09:00:00",f);
        long ls = date.toInstant(ZoneOffset.ofHours(8)).getEpochSecond();
        Instant now1 = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        Instant now = Instant.now();
        long nowTime =now.getEpochSecond();
        System.out.println("转换时间："+ls);
        System.out.println("当前时间："+nowTime);
        String slot_value ="2018-09-01 10:00:00";
        String pattern = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";
        boolean isMatch = Pattern.matches(pattern, slot_value);
        System.out.println(isMatch);
        String mailName ="luolp@cnfantasia.com";
        System.out.println(mailName.split("@")[0]);*/
       try {
           test.tesIg("mm63878078@163.com");
       }catch (Exception e){

       }

        //test.TestWeek();
    }


    private void tesIg(String ls)throws Exception{

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date2 =df.parse("2017-12-06 11:46:05");
        //String time = df.format(date);
        DateFormat df2 = new SimpleDateFormat("yyyyMMddHHmm");
      String rest =  df2.format(date2).toString();
        /*Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,2017);
        calendar.set(Calendar.MONTH,Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_WEEK,2);*/
        //calendar.setTime(date);
        // int time2 =calendar.get(Calendar.WEEK_OF_YEAR);
        //String time2 =df.format(calendar.);
        System.out.println(rest);
     //   System.out.println(time);
    }
    private void TestWeek(){
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, 2016);
        c.set (Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        c.set(Calendar.DATE, 24);//2017年5月20号
        Calendar now = Calendar.getInstance();//现在  5月24号
        //WEEK_OF_YEAR不支持跨年
        System.out.println("now week："+now.get(Calendar.WEEK_OF_YEAR));
        System.out.println("old week:"+c.get(Calendar.WEEK_OF_YEAR));
        System.out.println( now.get(Calendar.WEEK_OF_YEAR) - c.get(Calendar.WEEK_OF_YEAR));
    }
}
