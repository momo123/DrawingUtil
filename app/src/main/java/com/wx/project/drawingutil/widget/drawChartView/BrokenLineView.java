package com.wx.project.drawingutil.widget.drawChartView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wx.project.drawingutil.R;
import com.wx.project.drawingutil.utils.ChartType;
import com.wx.project.drawingutil.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Author: AlexSai
 * Time: 17/1/11 13:54
 * Introduction:
 */
public class BrokenLineView extends View {

    private Path path;
    public Paint paint;
    private OnDrawCompleteListener listener;
    private boolean isFirstPoint = true;
    private float firstX, firstY;
    private long preActionUpTime;
    private long nextActionUpTime;

    private int chartType;

    private List<Float> pointsX;
    private List<Float> pointsY;
    // 获取触摸位置
    float x = 0;
    float y = 0;
    private float preX, preY;// 之前的XY的位置，用于下面的手势移动
    private int view_width, view_height;// 屏幕的高度与宽度
    private boolean isRealTime;


    public BrokenLineView(Context context,boolean isRealTime) {
        super(context);
        initial(context,isRealTime);
    }

    public BrokenLineView(Context context, AttributeSet attrs,boolean isRealTime) {
        super(context, attrs);
        initial(context,isRealTime);
    }


    public BrokenLineView(Context context, AttributeSet attrs, int defStyleAttr,boolean isRealTime) {
        super(context, attrs, defStyleAttr);
        initial(context,isRealTime);
    }

    public void initial(Context context,boolean isRealTime) {
        this.isRealTime = isRealTime;
        path = new Path();
        paint = new Paint();
        // 获取屏幕的高度与宽度
        view_width = context.getResources().getDisplayMetrics().widthPixels;
        view_height = context.getResources().getDisplayMetrics().heightPixels;
        paint.setColor(getResources().getColor(R.color.brokenLine));
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);

        pointsX = new ArrayList<>();
        pointsY = new ArrayList<>();
    }

    public int getpointXSize(){
        return pointsX.size();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);// 绘制路径
    }

    public void setOnDrawCompleteListener(OnDrawCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                if (isFirstPoint) {
                    path.moveTo(x, y);// 绘图的起始点
                    firstX = x;
                    firstY = y;
                    isFirstPoint = false;
                }
                preX = x;
                preY = y;
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                nextActionUpTime = System.currentTimeMillis();
                if (((!isRealTime &&  (nextActionUpTime - preActionUpTime) > 100) || isRealTime ) && (nextActionUpTime - preActionUpTime) < 300){
                    //结束
                    this.setVisibility(GONE);
                    path.lineTo(firstX, firstY);

                    if (listener != null && pointsX.size() > 0) {
                        List<Float> listX = new ArrayList<>();
                        List<Float> listY = new ArrayList();
                        listX.addAll(pointsX);
                        listY.addAll(pointsY);
                        Collections.sort(listX);
                        Collections.sort(listY);
                        listener.OnDrawComplete(pointsX, pointsY, listX.get(listX.size() - 1) - listX.get(0), listY.get(listY.size() - 1) - listY.get(0));
                    } else {
                        //宽高为0
                        listener.OnDrawComplete(pointsX, pointsY, 0, 0);
                    }
                    invalidate();
                    Log.e("结束","结束");

                } else {
                    path.lineTo(x, y);
                    pointsX.add(x);
                    pointsY.add(y);
                    invalidate();

                }
                preActionUpTime = nextActionUpTime;
                break;
        }

        return true;

    }

    public void setPaintColor(int chartType) {

        Map<String, Object> map = SharedPreferencesUtil.getChartColorSetting(getContext());
        int dbxColor = (int) map.get("dbxColor");
        int zxColor = (int) map.get("zxColor");
        if (chartType == ChartType.BrokenLine) {
            paint.setColor(zxColor);
        } else {
            paint.setColor(dbxColor);
        }
    }

    public interface OnDrawCompleteListener {

        void OnDrawComplete(List<Float> pointsX, List<Float> pointsY, float width, float height);
    }
}
