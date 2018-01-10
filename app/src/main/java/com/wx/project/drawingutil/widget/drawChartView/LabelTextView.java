package com.wx.project.drawingutil.widget.drawChartView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wx.project.drawingutil.utils.ChartType;
import com.wx.project.drawingutil.utils.SharedPreferencesUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by xuyang on 15/8/29.
 */
@SuppressLint("AppCompatCustomView")
public class LabelTextView extends TextView {
    public int chartType = 0;
    private Context mContext;
    private float left_top_y;
    private float right_bottom_y;
    private  float left_top_x;
    private  float right_bottom_x;

    private List<Float> pointsX;
    private List<Float> pointsY;

    public LabelTextView(Context context) {
        this(context, null);
        mContext = context;
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void  setPoint(float left_top_x,float left_top_y,float right_bottom_x,float right_bottom_y){
        this.left_top_x = left_top_x;
        this.left_top_y = left_top_y;
        this.right_bottom_x = right_bottom_x;
        this.right_bottom_y = right_bottom_y;
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
    }

    public void setChartType(int type) {
        chartType = type;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //清除画布
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        Paint p = new Paint();
        p.setColor(Color.GREEN);
        p.setAntiAlias(true);// 设置画笔的锯齿效果
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2);

        Map<String, Object> map = SharedPreferencesUtil.getChartColorSetting(getContext());
        int dotColor = (int) map.get("dotColor");
        int lineColor = (int) map.get("lineColor");
        int circleColor = (int) map.get("circleColor");
        int recColor = (int) map.get("recColor");
        int dbxColor = (int) map.get("dbxColor");
        int zxColor = (int) map.get("zxColor");
        switch (chartType) {
            case ChartType.Circle:
                p.setStrokeWidth(2);
                p.setColor(circleColor);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - 1, p);
                break;
            case ChartType.Line:
                p.setColor(lineColor);
                canvas.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2, p);
                break;
            case ChartType.Rec:
                p.setColor(recColor);
                canvas.drawRect(2, 2, getWidth() - 2, getHeight() - 2, p);
                break;
            case ChartType.Dot:
                p.setStrokeWidth(3);
                p.setColor(dotColor);
                int margin = (getWidth()-32)/2;
                canvas.drawLine(margin, getHeight() / 2, getWidth()-margin,getHeight() / 2, p);
                canvas.drawLine(getWidth() / 2, margin, getWidth() / 2,getHeight()-margin, p);
                break;
            case ChartType.BrokenLine:
                paint.setColor(zxColor);
                drawPolygonOrBrokenline(ChartType.BrokenLine,canvas,paint);
                break;
            case ChartType.Polygon:
                paint.setColor(dbxColor);
                drawPolygonOrBrokenline(ChartType.Polygon,canvas,paint);
                break;
        }

    }

    public void setPoints(List<Float> pointsX, List<Float> pointsY) {
        this.pointsX = pointsX;
        this.pointsY = pointsY;
        invalidate();
    }

    public void drawPolygonOrBrokenline(int chartType,Canvas canvas,Paint paint){
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        Path path = new Path();
        if (pointsX != null && pointsX.size() > 1) {
            float maxX = Collections.min(pointsX);
            float maxY = Collections.min(pointsY);

            path.moveTo(pointsX.get(0) - maxX, pointsY.get(0) - maxY);
            for (int i = 1; i < pointsX.size(); i++) {
                path.lineTo(pointsX.get(i) - maxX, pointsY.get(i) - maxY);
            }

            if (chartType==ChartType.Polygon){
                path.lineTo(pointsX.get(0) - maxX, pointsY.get(0) - maxY);
            }
            canvas.drawPath(path, paint);// 绘制路径
        }

    }


}
