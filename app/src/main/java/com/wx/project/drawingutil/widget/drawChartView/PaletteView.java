package com.wx.project.drawingutil.widget.drawChartView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.wx.project.drawingutil.utils.ChartType;
import com.wx.project.drawingutil.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Map;

public class PaletteView extends View {

    private Paint mPaint = null;
    // 画板的坐标以及宽高
    private int bgBitmapHeight = 0;
    private int bgBitmapWidth = 0;

    // 当前的已经选择的画笔参数
    private int currentPaintTool = 0; // 0~6
    private int currentColor = Color.BLACK;
    private int currentSize = 3; // 1,3,5
    // 存储所有的动作
    private ArrayList<Action> actionList = null;
    // 当前的画笔实例
    private Action curAction = null;
    private int currentPaintIndex = -1;

    private Context mContext;

    private OnDrawCompleteListener listener;
    Bitmap newbit = null;
    boolean isResponseDownEvent = true;


    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    public void setOnDrawCompleteListener(OnDrawCompleteListener listener) {
        this.listener = listener;
    }

    public PaletteView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public PaletteView(Context context, AttributeSet arr) {
        super(context, arr);
        mContext = context;
        init(context);

    }

    public void setCurrentPaintTool(int currentPaintTool) {
        this.currentPaintTool = currentPaintTool;
    }

    private void init(Context context) {
        mPaint = new Paint();
        actionList = new ArrayList<Action>();
        bgBitmapWidth = context.getResources().getDisplayMetrics().widthPixels;
        bgBitmapHeight = context.getResources().getDisplayMetrics().heightPixels;
        newbit = Bitmap.createBitmap(bgBitmapWidth, bgBitmapHeight,
                Config.ARGB_4444);

    }

    public void initAction(float right_bottom_x, float right_bottom_y) {
        isResponseDownEvent = false;
        curAction.move(right_bottom_x, right_bottom_y);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int antion = event.getAction();
        if (antion == MotionEvent.ACTION_CANCEL) {
            return false;
        }
        float touchX = event.getX();
        float touchY = event.getY();

        if (antion == MotionEvent.ACTION_DOWN) {
            x1 = event.getX();
            y1 = event.getY();
            // 点击时
            if (isResponseDownEvent) {
                // 检测点击点是否在主绘图区
                if (testTouchMainPallent(touchX, touchY)) {
                    setCurAction(touchX, touchY);

                    clearSpareAction();
                }
                invalidate();
            }
        }
        // 拖动时
        if (antion == MotionEvent.ACTION_MOVE) {
            if (curAction != null) {
                x2 = event.getX();
                y2 = event.getY();
                //判断是否响应拖拽
                switch (currentPaintTool) {
                    case ChartType.Line:
                        curAction.move(touchX, touchY);
                        break;
                    case ChartType.Rec:
                        curAction.move(touchX, touchY);
                        break;
                    case ChartType.Dot:
                        curAction.move(touchX, touchY);
                        break;
                    case ChartType.Circle:
                        curAction.move(touchX, touchY);
                        break;
                }
                invalidate();
            }
        }
        // 抬起时
        if (antion == MotionEvent.ACTION_UP) {
            if (curAction != null) {
                //判断是否响应拖拽
                actionList.add(curAction);
                currentPaintIndex++;
                listener.OnDrawComplete(curAction);
            }

        }
        return true;
    }


    //通过手势来移动方块：1,2,3,4对应上下左右
    private int detectDicr(float start_x, float start_y, float end_x, float end_y) {
        boolean isLeftOrRight = Math.abs(start_x - end_x) > Math.abs(start_y - end_y) ? true : false;
        if (isLeftOrRight) {
            if (start_x - end_x > 0) {
                return 3;
            } else if (start_x - end_x < 0) {
                return 4;
            }
        } else {
            if (start_y - end_y > 0) {
                return 1;
            } else if (start_y - end_y < 0) {
                return 2;
            }
        }
        return 0;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        // 填充背景
        canvas.drawColor(Color.TRANSPARENT);
        // 画主画板
        drawMainPallent(canvas);
    }


    // 检测点击事件，是否在主绘图区
    public boolean testTouchMainPallent(float x, float y) {
        if (x > 0 + 2 && y > 0 + 2
                && x < 0 + bgBitmapWidth - 2
                && y < 0 + bgBitmapHeight - 2) {
            return true;
        }
        return false;
    }


    // 得到当前画笔的类型，并进行实例
    public void setCurAction(float x, float y) {
        Map<String, Object> map = SharedPreferencesUtil.getChartColorSetting(mContext);
        int dotColor = (int) map.get("dotColor");
        int lineColor = (int) map.get("lineColor");
        int circleColor = (int) map.get("circleColor");
        int recColor = (int) map.get("recColor");
        switch (currentPaintTool) {
            case ChartType.Dot:
                currentColor = dotColor;
                curAction = new MyPoint(x, y, currentColor);
                break;
            case ChartType.Line:
                currentColor = lineColor;
                curAction = new MyLine(x, y, currentSize, currentColor);
                break;
            case ChartType.Rec:
                currentColor = recColor;
                curAction = new MyRect(x, y, currentSize, currentColor);
                break;
            case ChartType.Circle:
                currentColor = circleColor;
                curAction = new MyCircle(x, y, currentSize, currentColor);
                break;
        }
    }


    // 画主画板
    private void drawMainPallent(Canvas canvas) {
        // 设置画笔没有锯齿，空心
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //canvas.drawColor(Color.TRANSPARENT);

        newbit = Bitmap.createBitmap(bgBitmapWidth, bgBitmapHeight,
                Config.ARGB_4444);
        Canvas canvasTemp = new Canvas(newbit);
        canvasTemp.drawColor(Color.TRANSPARENT);

        for (int i = 0; i <= currentPaintIndex; i++) {
            actionList.get(i).draw(canvasTemp);
        }

        // 画当前画笔痕迹
        if (curAction != null) {
            curAction.draw(canvas);
        }

//		// 在主画板上绘制临时画布上的图像
        canvas.drawBitmap(newbit, 0, 0, null);

    }


    public interface OnDrawCompleteListener {

        void OnDrawComplete(Action curAction);
    }

    // 后退前进完成后，缓存的动作
    private void clearSpareAction() {
        for (int i = actionList.size() - 1; i > currentPaintIndex; i--) {
            actionList.remove(i);
        }
    }

}
