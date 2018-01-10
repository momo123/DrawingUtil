package com.wx.project.drawingutil.widget.drawChartView;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by WangXin on 2017/3/14.
 */
public //方框
class MyRect extends Action{
    float startX;
    float startY;
    float stopX;
    float stopY;
    int size;

    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getStopX() {
        return stopX;
    }

    public float getStopY() {
        return stopY;
    }

    MyRect(){
        startX=0;
        startY=0;
        stopX=0;
        stopY=0;
    }

    MyRect(float x,float y,int size, int color){
        super(color);
        startX=x;
        startY=y;
        stopX=x;
        stopY=y;
        this.size=size;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        canvas.drawRect(startX, startY, stopX, stopY, paint);
    }

    public void move(float sX,float sY, float mx,float my){
        startX = sX;
        startY = sY;
        stopX=mx;
        stopY=my;
    }


    public void move(float mx,float my){
        stopX=mx;
        stopY=my;
    }
}
