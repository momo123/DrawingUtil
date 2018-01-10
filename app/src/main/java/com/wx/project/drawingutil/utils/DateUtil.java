package com.wx.project.drawingutil.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by WangXin on 2016/5/17.
 */
public class DateUtil {

    /**
     * 获取当前时间戳
     * @return
     */
    public static long getTime(){
        //return new Date().getTime();
        return System.currentTimeMillis();
    }

    /**
     * 根据日期获取时间戳
     * @param mDate 日期格式为yyyy-MM-dd hh:mm:ss
     * @return
     */
    public static long getTime(String mDate){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateString = mDate;
        try {
            Date date  = df.parse(dateString);
            long millisTime = date.getTime();
            return  millisTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获得系统当前年、月、日、小时、分钟、秒
     *  格式 ： 2016-05-17 12:23:34
     * @return
     */
    public static String getFullDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int minute = calendar.get(Calendar.MINUTE);//分
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//小时
        int second = calendar.get(Calendar.SECOND);//秒

        String temp = year+"";
        if(month < 10){
            temp += "-0"+month;
        }else {
            temp += "-"+month;
        }
        if (day < 10){
            temp += "-0"+day;
        }else {
            temp += "-"+day;
        }
        if(hour < 10){
            temp += " 0"+hour;
        }else{
            temp += " "+hour;
        }
        if(minute < 10){
            temp += ":0"+minute;
        }else {
            temp += ":"+minute;
        }
        if (second < 10){
            temp += "_0"+second;
        }else {
            temp += "_"+second;
        }
        return temp;
    }

    /**
     * 获得 YY-MM-DD
     * @param offset 0==当天  1往后加一天  -1往前减一天
     * @return
     */
    public static String getYMD(int offset){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,offset);
        int year = calendar.get(Calendar.YEAR);//获取年份
        int month = calendar.get(Calendar.MONTH)+1;//获取月份
        int day = calendar.get(Calendar.DATE);//获取日
        String temp = year+"";
        if(month < 10){
            temp += "-0"+month;
        }else {
            temp += "-"+month;
        }
        if (day < 10){
            temp += "-0"+day;
        }else {
            temp += "-"+day;
        }
        return temp;
    }

    /**
     * MM月DD日
     * @param offset 0==当天  1往后加一天  -1往前减一天
     * @return
     */
    public static String getMD(int offset){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,offset);
        int month = calendar.get(Calendar.MONTH)+1;//获取月份
        int day = calendar.get(Calendar.DATE);//获取日
        String temp = month+"月"+day+"日";
        return temp;
    }

    /**
     * 根据日期 获得 YY-MM-DD
     * @param offset 0==传过来日期  1往后加一天  -1往前减一天
     * @return
     */
    public static String getYMD(String date, int offset){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        if(isDate(date, "yyyy-MM-dd")){
            try {
                calendar.setTime(dateFormat.parse(date));
                calendar.add(Calendar.DAY_OF_MONTH,offset);
                int year = calendar.get(Calendar.YEAR);//获取年份
                int month = calendar.get(Calendar.MONTH)+1;//获取月份
                int day = calendar.get(Calendar.DATE);//获取日
                String temp = year+"";
                if(month < 10){
                    temp += "-0"+month;
                }else {
                    temp += "-"+month;
                }
                if (day < 10){
                    temp += "-0"+day;
                }else {
                    temp += "-"+day;
                }
                return temp;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";
    }
    
    
    /**
     * 根据日期 获得 YY-MM-DD
     * @param offset 0==传过来日期  1往后加一天  -1往前减一天
     * @return
     */
    public static String getHSM(int offset){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
     
        calendar.add(Calendar.SECOND,offset);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//获取年份
        int minute = calendar.get(Calendar.MINUTE);//获取月份
        int second = calendar.get(Calendar.SECOND);//获取日
        String temp = "";
        if(hour < 10){
            temp += "0"+hour;
        }else{
        	temp += hour;
        }
        if(minute < 10){
            temp += ":0"+minute;
        }else {
            temp += ":"+minute;
        }
        if (second < 10){
            temp += ":0"+second;
        }else {
            temp += ":"+second;
        }
        return temp;
    
       
    }

    /**
     * MM月DD日
     * @param offset 0==传过来日期  1往后加一天  -1往前减一天
     * @return
     */
    public static String getMD(String date,int offset){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        if(isDate(date, "yyyy-MM-dd")){
            try {
                calendar.setTime(dateFormat.parse(date));
                calendar.add(Calendar.DAY_OF_MONTH,offset);
                int month = calendar.get(Calendar.MONTH)+1;//获取月份
                int day = calendar.get(Calendar.DATE);//获取日
                String temp = month+"月"+day+"日";
                return temp;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return "";

    }


    /**
     * 获得 week
     * @param offset 0==当天  1往后加一天  -1往前减一天
     * @return
     */
    public static String getWeek(int offset){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,offset);
        int weekOfYear = calendar.get(Calendar.DAY_OF_WEEK);
        String week = "";
        switch (weekOfYear){
            case Calendar.MONDAY:
                week = "星期一";
                break;
            case  Calendar.TUESDAY:
                week = "星期二";
                break;
            case Calendar.WEDNESDAY:
                week = "星期三";
                break;
            case Calendar.THURSDAY:
                week = "星期四";
                break;
            case Calendar.FRIDAY:
                week = "星期五";
                break;
            case Calendar.SATURDAY:
                week = "星期六";
                break;
            case Calendar.SUNDAY:
                week = "星期日";
                break;
        }
        return week;
    }

    /**
     *根据日期获得 week
     * @param date 根据日期获得星期几  date格式为"yyyy-MM-dd"
     * @return
     */
    public static String getWeek(String date){

        if (isDate(date,"yyyy-MM-dd")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat weekFormat = new SimpleDateFormat("E");
            Date d = null;
            try {
                d = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return weekFormat.format(d);
        }
        return "";
    }

    /**
     *
     * @param date 根据日期获得年  date格式为"yyyy-MM-dd"
     * @return
     */
    public static int getYear(String date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        if(isDate(date, "yyyy-MM-dd")){
            try {
                calendar.setTime(dateFormat.parse(date));
                int day =  calendar.get(Calendar.YEAR);
                return day;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     *
     * @param date 根据日期获得月  date格式为"yyyy-MM-dd"
     * @return
     */
    public static int getMonth(String date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        if(isDate(date, "yyyy-MM-dd")){
            try {
                calendar.setTime(dateFormat.parse(date));
                int day =  calendar.get(Calendar.MONTH);
                return day;
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     *
     * @param date 根据日期获得日  date格式为"yyyy-MM-dd"
     * @return
     */
    public static int getDay(String date){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        if(isDate(date, "yyyy-MM-dd")){
            try {
                calendar.setTime(dateFormat.parse(date));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       int day =  calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 获得系统当前年
     * @return
     */
    public static int getCurrentYear(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);//获取年份
        return year;
    }

    /**
     * 获得系统当前月
     * @return
     */
    public static int getCurrentMonth(){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);//获取月份
        return month;
    }

    /**
     * 获得系统当前日
     * @return
     */
    public static int getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);//获取日
        return day;
    }
    /**
     * 判断是否为合法的日期时间字符串
     * @param str_input
     * @param str_input
     * @return boolean;符合为true,不符合为false
     */
    public static  boolean isDate(String str_input,String rDateFormat){
        if (!isNull(str_input)) {
            SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
            formatter.setLenient(false);
            try {
                formatter.format(formatter.parse(str_input));
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }


    public static boolean isNull(String str){
        if(str==null)
            return true;
        else
            return false;
    }

}
