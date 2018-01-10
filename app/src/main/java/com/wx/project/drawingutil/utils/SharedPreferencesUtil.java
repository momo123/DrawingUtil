package com.wx.project.drawingutil.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class SharedPreferencesUtil {

	/**
	 * 获得图形颜色
	 * @param context
	 * @return
	 */
	public static Map<String,Object> getChartColorSetting(Context context) {
		Map<String,Object> map = new HashMap<>();
		SharedPreferences sp = context.getSharedPreferences("HongWai_Data", Context.MODE_PRIVATE);
		map.put("dotColor",sp.getInt("chart_dotColor", Color.GREEN));
		map.put("lineColor",sp.getInt("chart_lineColor", Color.GREEN));
		map.put("circleColor",sp.getInt("chart_circleColor", Color.GREEN));
		map.put("recColor",sp.getInt("chart_recColor", Color.GREEN));
		map.put("dbxColor",sp.getInt("chart_dbxColor", Color.GREEN));
		map.put("zxColor",sp.getInt("chart_zxColor", Color.GREEN));
		return map;
	}

	/**
	 * 设置图形颜色
	 * @param context
	 * @param dotColor
	 * @param lineColor
	 * @param circleColor
	 * @param recColor
	 * @param dbx
	 * @param zx
	 */
	public static void setChartColorSetting(Context context,int dotColor,int lineColor,int circleColor,int recColor,int dbx,int zx){
		SharedPreferences sp = context.getSharedPreferences("HongWai_Data", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt("chart_dotColor",dotColor);
		editor.putInt("chart_lineColor",lineColor);
		editor.putInt("chart_circleColor",circleColor);
		editor.putInt("chart_recColor",recColor);
		editor.putInt("chart_dbxColor",dbx);
		editor.putInt("chart_zxColor",zx);
		editor.commit();
	}

}
