package com.wx.project.drawingutil.widget.drawChartView;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by WangXin on 2017/3/14.
 */
public //直线
class MyLine extends Action{
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

    MyLine(){
        startX=0;
        startY=0;
        stopX=0;
        stopY=0;
    }

    MyLine(float x,float y,int size, int color){
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
        float radious = 8;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
        canvas.drawCircle(stopX,stopY,radious,paint);
    }

    public void move(float mx,float my){
        stopX=mx;
        stopY=my;
    }
}
