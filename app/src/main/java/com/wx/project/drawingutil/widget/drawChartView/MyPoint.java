package com.wx.project.drawingutil.widget.drawChartView;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by WangXin on 2017/3/14.
 */
public // 点
class MyPoint extends Action {
    public float x;
    public float y;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    MyPoint(float px, float py, int color) {
        super(color);
        this.x = px;
        this.y = py;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }
}
