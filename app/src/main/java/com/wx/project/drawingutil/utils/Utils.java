package com.wx.project.drawingutil.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import java.util.Map;

/**
 * Created by xuyang on 15/7/17.
 */
public class Utils {

    /**
     * 返回作用于资源文件的显示参数
     * <p>注：常用于控件大小、位置的动态计算
     *
     * @param ctx 上下文
     * @return 作用于资源文件的显示参数
     */
    public static DisplayMetrics getResourceDisplayMetrics(Context ctx) {
        return ctx.getResources().getDisplayMetrics();
    }

    /**
     * 单位转换，dip转 px
     *
     * @param ctx 上下文
     * @param dpValue dip单位表示的值
     * @return px单位表示的值
     */
    public static int dip2px(Context ctx, float dpValue) {
        DisplayMetrics dm = getResourceDisplayMetrics(ctx);
        final float scale = dm.density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 双指计算距离
     *
     * @param event
     * @return
     */
    public static float calcDistance(MotionEvent event) {
        return calcDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    /**
     * 计算距离
     *
     * @param firstX
     * @param firstY
     * @param secondX
     * @param secondY
     * @return
     */
    public static float calcDistance(float firstX, float firstY, float secondX, float secondY) {
        float deltaX = secondX - firstX;
        float deltaY = secondY - firstY;
        return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }



    /**
     * 双指计算旋转角度
     *
     * @param event
     * @return
     */
    public static float calcRotation(MotionEvent event) {
        return calcRotation(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
    }

    /**
     * 计算旋转角度
     *
     * @param firstX
     * @param firstY
     * @param secondX
     * @param secondY
     * @return
     */
    public static float calcRotation(float firstX, float firstY, float secondX, float secondY) {
        float deltaX = secondX - firstX;
        float deltaY = secondY - firstY;
        return (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    //回收bitmap
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }

    /**设置当前chart的提示文字**/
    public static String addChartNumTip(int chartType, Map<String, Object> lineNumWithChartType, boolean isChangeValue) {
        String tip = "";
        int num = 0;
        switch (chartType) {
            case ChartType.Dot:
                tip = "P";
                num = (int) lineNumWithChartType.get("DotNum");
                if (isChangeValue)
                    lineNumWithChartType.put("DotNum", num + 1);
                break;
            case ChartType.Circle:
                tip = "E";
                num = (int) lineNumWithChartType.get("CircleNum");
                if (isChangeValue)
                    lineNumWithChartType.put("CircleNum", num + 1);
                break;
            case ChartType.Line:
                tip = "L";
                num = (int) lineNumWithChartType.get("LineNum");
                if (isChangeValue)
                    lineNumWithChartType.put("LineNum", num + 1);
                break;
            case ChartType.Rec:
                tip = "R";
                num = (int) lineNumWithChartType.get("RecNum");
                if (isChangeValue)
                    lineNumWithChartType.put("RecNum", num + 1);
                break;

            case ChartType.Polygon:
                tip = "D";
                num = (int) lineNumWithChartType.get("PolyNum");
                if (isChangeValue)
                    lineNumWithChartType.put("PolyNum", num + 1);
                break;

            case ChartType.BrokenLine:
                tip = "B";
                num = (int) lineNumWithChartType.get("BrokenNum");
                if (isChangeValue)
                    lineNumWithChartType.put("BrokenNum", num + 1);
                break;

        }

        if (num + 1 < 10) {
            tip = tip + "0" + (num + 1);
        } else {
            tip = tip + (num + 1);
        }

        return tip;
    }

    /**设置当前chartview上各个类型的数目**/
    public static void delChartNumTip(int chartType, Map<String, Object> lineNumWithChartType) {
        int num = 0;
        switch (chartType) {
            case ChartType.Dot:
                num = (int) lineNumWithChartType.get("DotNum");
                lineNumWithChartType.put("DotNum", num - 1);
                break;
            case ChartType.Circle:
                num = (int) lineNumWithChartType.get("CircleNum");
                lineNumWithChartType.put("CircleNum", num - 1);
                break;
            case ChartType.Line:
                num = (int) lineNumWithChartType.get("LineNum");
                lineNumWithChartType.put("LineNum", num - 1);
                break;
            case ChartType.Rec:
                num = (int) lineNumWithChartType.get("RecNum");
                lineNumWithChartType.put("RecNum", num - 1);
                break;
            case ChartType.BrokenLine:
                num = (int) lineNumWithChartType.get("BrokenNum");
                lineNumWithChartType.put("BrokenNum", num - 1);
                break;
            case ChartType.Polygon:
                num = (int) lineNumWithChartType.get("PolyNum");
                lineNumWithChartType.put("PolyNum", num - 1);
                break;
        }
    }

}
