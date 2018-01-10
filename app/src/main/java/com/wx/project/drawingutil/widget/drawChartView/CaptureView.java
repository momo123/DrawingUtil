package com.wx.project.drawingutil.widget.drawChartView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.wx.project.drawingutil.R;
import com.wx.project.drawingutil.activity.ChartStateChangedListener;
import com.wx.project.drawingutil.utils.ChartType;
import com.wx.project.drawingutil.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author WangXin
 */
public class CaptureView extends FrameLayout {

    private final int LABEL_MAX_TEXT_LENGTH = 20;
    private final String LABEL_DEFAULT_TEXT = "标签名";
    private FrameLayout mContainerLayout;

    public float width_scare = 1;//surfaceview宽度和图片宽度度比例
    public float height_scare = 1;//surfaceview高度和图片高度度比例

    public float wScare = 0.5f;//图片宽度和surfaceView比例
    public float hScare = 0.5f;//图片高度和surfaceVview比例

    private Context mContext;

    /**
     * 当前焦点标签
     */
    private DashFrameLayout mFocusLabel;

    /**
     * 标签最多字数
     */
    private int mLabelMaxTextLength = LABEL_MAX_TEXT_LENGTH;

    /**
     * 标签默认文字
     */
    private String mLabelDefaultText = LABEL_DEFAULT_TEXT;

    private ChartStateChangedListener chartStateChangedListener;

    public void setChartStateChangedListener(ChartStateChangedListener listener){
        chartStateChangedListener = listener;
    }

    public CaptureView(Context context) {
        this(context, null);
    }

    public CaptureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_capture, this);
        if (attrs != null) {
            TypedArray typed = context.obtainStyledAttributes(attrs,
                    R.styleable.CaptureStyle);
            try {
                mLabelMaxTextLength = typed.getInt(R.styleable.CaptureStyle_labelMaxTextLength, mLabelMaxTextLength);
                mLabelDefaultText = typed.getString(R.styleable.CaptureStyle_labelDefaultText);
                mLabelDefaultText = TextUtils.isEmpty(mLabelDefaultText) ? LABEL_DEFAULT_TEXT : mLabelDefaultText;
            } finally {
                typed.recycle();
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContainerLayout = (FrameLayout) findViewById(R.id.fl_container);
    }


    public FrameLayout getmContainerLayout(){
        return mContainerLayout;
    }

    /**
     * 添加图形
     **/
    public void addChart(int chartType
            , float left_top_x, float left_top_y, float right_bottom_x, float right_bottom_y, String lineNumWithChartType) {

        float line_left_topx = left_top_x;
        float line_left_topy = left_top_y;
        float line_right_bottomx = right_bottom_x ;
        float line_right_bottomy = right_bottom_y;

        float width = right_bottom_x - left_top_x;
        float height = right_bottom_y - left_top_y;

        double curRotation  = 0;
        switch (chartType){
            case ChartType.Dot:
                width = Utils.dip2px(getContext(),45);
                height = Utils.dip2px(getContext(),45);
                left_top_x = left_top_x - width/2;
                left_top_y = left_top_y - width/2;
                right_bottom_x = right_bottom_x + width/2;
                right_bottom_y = right_bottom_y + width/2;
                break;
            case ChartType.Line:
                float rec_width = Math.abs(right_bottom_x-left_top_x);
                float rec_height =  Math.abs(right_bottom_y-left_top_y);

                //需要记录两端坐标
                width = (int) Math.sqrt (Math.pow(Math.abs(right_bottom_x-left_top_x),2) + Math.pow(Math.abs(right_bottom_y-left_top_y),2));
                height = Utils.dip2px(getContext(),20);

                if (left_top_x > right_bottom_x){
                    float tempx = left_top_x;
                    float tempy = left_top_y;
                    left_top_y = right_bottom_y;
                    right_bottom_y = tempy;
                    left_top_x = right_bottom_x;
                    right_bottom_x = tempx;
                }
                if (left_top_y < right_bottom_y){
                    curRotation = Math.toDegrees (Math.atan (rec_height/rec_width));
                }else{
                    curRotation = 360-Math.toDegrees (Math.atan (rec_height/rec_width));
                }
                break;
        }

        final LabelTextView textView = (LabelTextView) LayoutInflater.from(mContext).inflate(R.layout.view_label_text, null);
        textView.setChartType(chartType);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setWidth((int) width);
        textView.setHeight((int) height);
        if(chartType == ChartType.Line){
            textView.setPoint(line_left_topx,line_left_topy,line_right_bottomx,line_right_bottomy);
        }else{
            textView.setPoint(left_top_x,left_top_y,right_bottom_x,right_bottom_y);
        }
        addLabel(textView, chartType, false, left_top_x, left_top_y, right_bottom_x, right_bottom_y ,lineNumWithChartType,Float.valueOf(String.valueOf(curRotation))  );
    }


    /**
     * 添加图形
     **/
    public void addBrokenChart(List<Float> listX, List<Float> listY, int width, int height,int chartType, int centerX, int centerY, String lineNumWithChartType) {
        final LabelTextView textView = (LabelTextView) LayoutInflater.from(mContext).inflate(R.layout.view_label_text, null);
        textView.setChartType(chartType);
        textView.setBackgroundColor(Color.TRANSPARENT);

        float minX = Collections.min(listX);
        float maxX = Collections.max(listX);
        float minY = Collections.min(listY);
        float maxY = Collections.max(listY);
        int cx = (int) (minX + (maxX - minX)/2);
        int cy = (int) (minY + (maxY - minY)/2);

        int top_left_x = cx - width / 2;
        int top_left_y = cy - height / 2;
        int bottom_right_x = cx + width / 2;
        int bottom_right_y = cy + height / 2;

        textView.setWidth(width);
        textView.setHeight(height);
        textView.setPoints(listX, listY);

        int[][] points = dealPrimitivePoint(listX, listY, width, height, centerX, centerY);

        addLabel(centerX, centerY, points, textView, chartType, false, top_left_x, top_left_y, bottom_right_x, bottom_right_y, 0, lineNumWithChartType);
    }


    public int[][] dealPrimitivePoint(List<Float> listX, List<Float> listY, int width, int height, int centerX, int centerY) {

        List<Float> pointsX = new ArrayList();
        List<Float> pointsY = new ArrayList();
        pointsX.addAll(listX);
        pointsY.addAll(listY);

        //转化成相对坐标,和LabelTextView中坐标一样
        List<Float> pointsXnew = new ArrayList();
        List<Float> pointsYnew = new ArrayList();
        float minX = Collections.min(pointsX);
        float minY = Collections.min(pointsY);
        for (int i = 0; i < pointsX.size(); i++) {
            pointsXnew.add(pointsX.get(i) - minX);
            pointsYnew.add(pointsY.get(i) - minY);
        }

        //转化成绝对坐标
        List<Float> pointsXnew2 = new ArrayList();
        List<Float> pointsYnew2 = new ArrayList();
        for (int i = 0; i < pointsXnew.size(); i++) {
            pointsXnew2.add(pointsXnew.get(i) + (centerX - width / 2));
            pointsYnew2.add(pointsYnew.get(i) + (centerY - height / 2));
            Log.e("pointsYnew2", "" + pointsXnew2.get(i) + "===" + pointsYnew2.get(i));
        }


        //创建一个二维数组,装多边形的点
        int[][] points = new int[pointsXnew2.size()][2];
        for (int i = 0; i < pointsXnew2.size(); i++) {
            points[i][0] = pointsXnew2.get(i).intValue();
            points[i][1] = pointsYnew2.get(i).intValue();
        }
        return points;

    }


    /**
     * 添加图形
     * @param view
     * @param chartType
     * @param isNeedTransToCenter
     * @param left_top_x
     * @param left_top_y
     * @param right_bottom_x
     * @param right_bottom_y
     * @param tip
     * @param curRotation
     */
    private void addLabel(View view,int chartType,boolean isNeedTransToCenter
    ,float left_top_x, float left_top_y,float right_bottom_x,float right_bottom_y,String tip,float curRotation) {
        final DashFrameLayout dashFrameLayout = new DashFrameLayout(getContext());
        dashFrameLayout.setLabelListener(new DashFrameLayout.LabelListener() {
            @Override
            public void onFocus() {
                mFocusLabel = dashFrameLayout;
            }

            @Override
            public void onClick() {
                onLabelClick();
            }

        });
        dashFrameLayout.setChartType(chartType);
        dashFrameLayout.tip = tip;
        dashFrameLayout.isNeedTransToCenter =  isNeedTransToCenter;
        dashFrameLayout.left_top_x = left_top_x;
        dashFrameLayout.left_top_y = left_top_y;
        dashFrameLayout.right_bottom_x = right_bottom_x;
        dashFrameLayout.right_bottom_y = right_bottom_y;
        dashFrameLayout.width_scare = width_scare;
        dashFrameLayout.height_scare = height_scare;
        dashFrameLayout.wScare = wScare;
        dashFrameLayout.hScare = hScare;
        if (chartType == ChartType.Line) {
            if(curRotation == 0){
                curRotation = 15.0f;
            }
            dashFrameLayout.mCurRotation = curRotation;
        }
        dashFrameLayout.addView(view);
        dashFrameLayout.setChartStateChangedListener(chartStateChangedListener);
        hideFocusLabelDashBorder();
        mFocusLabel = dashFrameLayout;
        mContainerLayout.addView(dashFrameLayout);
    }

    /**
     * 折线或多边形
     */
    private void addLabel(int centerx, int centery, int[][] points, View view, int chartType, boolean isNeedTransToCenter
            , float left_top_x, float left_top_y, float right_bottom_x, float right_bottom_y, float curRotation, String tip) {

        final DashFrameLayout dashFrameLayout = new DashFrameLayout(getContext());
        dashFrameLayout.setLabelListener(new DashFrameLayout.LabelListener() {
            @Override
            public void onFocus() {
                mFocusLabel = dashFrameLayout;
            }

            @Override
            public void onClick() {
                onLabelClick();
            }

        });
        dashFrameLayout.setChartType(chartType);
        dashFrameLayout.tip = tip;
        Log.e("tip", "" + tip);
        dashFrameLayout.points = points;
        dashFrameLayout.centerX = centerx;
        dashFrameLayout.centerY = centery;
        dashFrameLayout.isNeedTransToCenter = isNeedTransToCenter;
        dashFrameLayout.left_top_x = left_top_x;
        dashFrameLayout.left_top_y = left_top_y;
        dashFrameLayout.right_bottom_x = right_bottom_x;
        dashFrameLayout.right_bottom_y = right_bottom_y;
        dashFrameLayout.width_scare = width_scare;
        dashFrameLayout.height_scare = height_scare;
        dashFrameLayout.wScare = wScare;
        dashFrameLayout.hScare = hScare;

        dashFrameLayout.addView(view);
        dashFrameLayout.setChartStateChangedListener(chartStateChangedListener);
        hideFocusLabelDashBorder();
        mFocusLabel = dashFrameLayout;
        mContainerLayout.addView(dashFrameLayout);
    }


    private void hideFocusLabelDashBorder() {
        if (mFocusLabel != null) {
            mFocusLabel.showDashBorder(false);
        }
    }

    private void showFocusLabelDashBorder() {
        if (mFocusLabel != null) {
            mFocusLabel.showDashBorder(true);
        }
    }

    /**
     * 点击事件
     */
    private void onLabelClick() {
        if (mFocusLabel != null) {
            View view = mFocusLabel.getLabelView();
            if (view instanceof LabelTextView) {
            }
        }
    }



    public void changeDeleteState(boolean deleteState){
        int count = mContainerLayout.getChildCount();
        for (int i=0;i<count;i++){
            View view = mContainerLayout.getChildAt(i);
            if (view instanceof DashFrameLayout){
                ((DashFrameLayout) view).deleteStateOpen = deleteState;
            }
        }
    }

}
