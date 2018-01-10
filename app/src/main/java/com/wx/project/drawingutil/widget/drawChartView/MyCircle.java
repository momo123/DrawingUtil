package com.wx.project.drawingutil.widget.drawChartView;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by WangXin on 2017/3/14.
 */
//圆框
public class MyCircle extends Action{
    float startX;
    float startY;
    float stopX;
    float stopY;
    float radius;
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

    public float getRadius() {
        return radius;
    }

    MyCircle(){
        startX=0;
        startY=0;
        stopX=0;
        stopY=0;
        radius=0;
    }

    MyCircle(float x,float y,int size, int color){
        super(color);
        startX=x;
        startY=y;
        stopX=x;
        stopY=y;
        radius=0;
        this.size=size;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        canvas.drawCircle((startX+stopX)/2, (startY+stopY)/2, radius, paint);
    }

    public void move(float mx,float my){
        stopX=mx;
        stopY=my;
        radius=(float) ((Math.sqrt((mx-startX)*(mx-startX)+(my-startY)*(my-startY)))/2);
    }
}
