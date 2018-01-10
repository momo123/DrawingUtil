package com.wx.project.drawingutil.widget.drawChartView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.wx.project.drawingutil.activity.ChartStateChangedListener;
import com.wx.project.drawingutil.utils.ChartType;
import com.wx.project.drawingutil.utils.ImageUtils;
import com.wx.project.drawingutil.utils.SharedPreferencesUtil;
import com.wx.project.drawingutil.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by xuyang on 15/10/6.
 */
public class DashFrameLayout extends FrameLayout {

    public float width_scare = 1;//图片宽度和surfaceview宽度比例
    public float height_scare = 1;//图片高度度和surfaceview高度比例

    public float wScare = 0.5f;//图片宽度和surfaceView比例
    public float hScare = 0.5f;//图片高度和surfaceVview比例

    private final int MSG_DRAW = 1;
    private final int MSG_ERASE = 2;

    /**
     * 默认虚线段长度
     */
    private final static int DASH_ON_WIDTH = 15;

    /**
     * 默认虚线段之间空白长度
     */
    private final static int DASH_OFF_WIDTH = 8;
    private Paint mDashPaint;
    private Path mDashPath;

    /**
     * 虚线段“on”,"off"长度
     */
    private float[] mDashFloats;

    /**
     * PathEffect数组，绘画时复用
     */
    private PathEffect[] mPathEffects;

    /**
     * 当前偏移量
     */
    private int mDashPhase;

    /**
     * 若要动画连续，则PathEffect最少个数为虚线段和空白长度
     */
    private int mDashMinSize = DASH_ON_WIDTH + DASH_OFF_WIDTH;

    private RefreshHandler mRefreshHandler;//刷新handler

    /**
     * 动作标志：无
     */
    private static final int NONE = 0;

    /**
     * 动作标志：拖动
     */
    private static final int DRAG = 1;

    /**
     * 动作标志：单指缩放与旋转
     */
    private static final int SINGLE_ZOOM = 2;

    /**
     * 动作标志：双指缩放与旋转
     */
    private static final int DOUBLE_ZOOM = 3;

    /**
     * 初始化动作标志
     */
    private int mode = NONE;

    /**
     * 变换矩阵，包括移动、旋转和缩放等
     */
    private Matrix mViewMatrix = new Matrix();

    /**
     * 变换矩阵，但不包括旋转
     */
    private Matrix mViewNoRotateMatrix = new Matrix();

    /**
     * 上次缩放距离
     */
    private float mPreDistance;

    /**
     * 上次旋转角度
     */
    private float mPreRotation;

    /**
     * 当前缩放比例，初始为1
     */
    private float mCurScaleFactor = 1.f;

    /**
     * 当前缩放比例，初始为1
     */
    private float mCurYScaleFactor = 1.f;

    /**
     * 当前旋转角度，初始为0
     */
    public float mCurRotation = 15.f;

    /**
     * 标签
     */
    private Bitmap mLabelBitmap;
    private RectF mLabelRect;

    /**
     * 标签中心坐标
     */
    public float[] mCenterPoint = new float[2];

    /**
     * 删除按钮初始中心坐标 【左上角】
     */
    private float[] mDelPoint = new float[2];

    /**
     * 删除按钮移动、旋转和缩放后的中心坐标
     */
    private float[] mDelDrawPoint = new float[2];

    /**
     * 删除按钮移动、旋转和缩放后的中心坐标
     */
    private float[] mDragDrawPoint = new float[2];

    /**
     * 删除按钮初始中心坐标【右下角】
     */
    private float[] mDragPoint = new float[2];

    /**
     * 虚线框初始rect
     */
    private RectF mDashRectF = new RectF();
    private RectF mDashDrawRectF = new RectF();

    /**
     * 是否显示虚线框
     */
    private boolean mShowDashBorder = true;
    private boolean mPreShowDashBorder;

    /**
     * 拖放按钮rect
     */
    private RectF mDragRightBottomDrawRectF = new RectF();
    private RectF mDragleftTopDrawRectF = new RectF();

    private RectF mDragRightTopDrawRectF = new RectF();
    private RectF mDragleftBottomDrawRectF = new RectF();
    private RectF mDragTopCenterDrawRectF = new RectF();
    private RectF mDragBottomCenterDrawRectF = new RectF();
    private RectF mDragLeftCenterDrawRectF = new RectF();
    private RectF mDragRightCenterDrawRectF = new RectF();

    //缩放，旋转固定点
    float scalePointX;
    float scalePointY;

    boolean isRightBottom = true;//判断是否在图形右下角位置
    boolean isLeftBottom = false;//判断是否在图形左下角位置
    boolean isRightTop = false;//判断是否在图形右上角位置

    final int recWidth = 50;//矩形最小width

    /**
     * 是否为点击事件
     */
    private boolean mAlwaysInTapRegion;

    private final int mTouchSlopSquare;

    private float mInitTouchX;
    private float mInitTouchY;

    private float mLastTouchX;
    private float mLastTouchY;

    private LabelListener mListener;

    private View mChildView;


    private int chartType;
    public float left_top_x;
    public float left_top_y;
    public float right_bottom_x;
    public float right_bottom_y;

    private Context mContext;

    boolean isFromReSet = false;
    boolean isNeedTransToCenter = true;

    ChartStateChangedListener chartStateChangedListener;

    public int[][] points;
    public int centerX;
    public int centerY;

    float startX = 0;
    float startY = 0;

    public String tip = "";
    public boolean deleteStateOpen = false;

    public void setChartStateChangedListener(ChartStateChangedListener listener) {
        chartStateChangedListener = listener;
    }


    public int getChartType() {
        return chartType;
    }

    private boolean isRunPreDrawListener = true;


    public DashFrameLayout(Context context) {
        this(context, null);
        mContext = context;
    }

    public DashFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public DashFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mContext = context;
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDashPaint.setColor(Color.GRAY);
        mDashPaint.setStyle(Paint.Style.STROKE);
        mDashPaint.setStrokeWidth(1);

        mDashPath = new Path();
        mRefreshHandler = new RefreshHandler(this);

        mDashFloats = new float[]{DASH_ON_WIDTH, DASH_OFF_WIDTH};
        mPathEffects = new PathEffect[mDashMinSize];
        showDashBorder(true);

        mTouchSlopSquare = ViewConfiguration.get(context).getScaledTouchSlop() *
                ViewConfiguration.get(context).getScaledTouchSlop();

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!isRunPreDrawListener) {
                    return true;
                }
                isRunPreDrawListener = false;

                int childCount = getChildCount();
                if (childCount < 1) {
                    return false;
                }

                Rect rect = new Rect();
                mChildView.getLocalVisibleRect(rect);

                mDashRectF.left = rect.left;
                mDashRectF.top = rect.top;
                mDashRectF.right = rect.right;
                mDashRectF.bottom = rect.bottom;

                // 计算删除按钮初始中心坐标
                mDelPoint[0] = mDashRectF.left;
                mDelPoint[1] = mDashRectF.top;

                // 计算拖放按钮初始中心坐标
                mDragPoint[0] = mDashRectF.right;
                mDragPoint[1] = mDashRectF.bottom;

                if (chartType == ChartType.Line) {
                    // 计算删除按钮初始中心坐标
                    mDelPoint[0] = rect.left;
                    mDelPoint[1] = rect.height() / 2;

                    // 计算拖放按钮初始中心坐标
                    mDragPoint[0] = rect.width();
                    mDragPoint[1] = rect.height() / 2;
                }

                if (mViewMatrix.isIdentity()) {
                    mCenterPoint[0] = rect.width() >> 1;
                    mCenterPoint[1] = rect.height() >> 1;
                    // 移动到

                    if (chartType != ChartType.Line) {
                        translate(left_top_x, left_top_y);
                    }

                    if (chartType == ChartType.Line) {
                        int halfWidth = getWidth() >> 1;
                        int halfHeight = getHeight() >> 1;
                        translate(halfWidth - mCenterPoint[0], halfHeight - mCenterPoint[1]);
                        mViewMatrix.postRotate(mCurRotation, mCenterPoint[0], mCenterPoint[1]);
                        mViewNoRotateMatrix.postRotate(mCurRotation, mCenterPoint[0], mCenterPoint[1]);
                        updateBtnDrawRect();
                        translate(left_top_x - mDelDrawPoint[0], left_top_y - mDelDrawPoint[1]);
                    }
                    setLeftBottomPoint();

                } else {
                    int newCenterX = rect.width() >> 1;
                    int newCenterY = rect.height() >> 1;

                    // 根据新中心调整矩阵
                    mViewMatrix.reset();
                    mViewMatrix.postTranslate(mCenterPoint[0] - newCenterX, mCenterPoint[1] - newCenterY);
                    mViewMatrix.postRotate(mCurRotation, mCenterPoint[0], mCenterPoint[1]);
                    mViewMatrix.postScale(mCurScaleFactor, mCurScaleFactor, mCenterPoint[0], mCenterPoint[1]);

                    mViewNoRotateMatrix.reset();
                    mViewNoRotateMatrix.postTranslate(mCenterPoint[0] - newCenterX, mCenterPoint[1] - newCenterY);
                    mViewNoRotateMatrix.postScale(mCurScaleFactor, mCurScaleFactor, mCenterPoint[0], mCenterPoint[1]);

                    updateBtnDrawRect();
                }

                // 将view转换为bitmap，便于旋转、缩放时提高性能
                mLabelBitmap = ImageUtils.captureScreenByDraw(mChildView);
                mDashPath.reset();
                mDashPath.addRect(mDashRectF, Path.Direction.CCW);
                removeAllViews();

                return true;
            }
        });
    }


    public void setChartType(int type) {
        chartType = type;
    }


    private void setLeftBottomPoint() {
        left_top_x = mDragleftTopDrawRectF.centerX();
        left_top_y = mDragleftTopDrawRectF.centerY();
        right_bottom_x = mDragRightBottomDrawRectF.centerX();
        right_bottom_y = mDragRightBottomDrawRectF.centerY();
        if (left_top_y < 0) {
            left_top_y = 0;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mode == NONE && isFromReSet == false) {
            setLeftBottomPoint();
        }

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);

        //根据类型设置画笔颜色
        Map<String, Object> map = SharedPreferencesUtil.getChartColorSetting(getContext());
        int dotColor = (int) map.get("dotColor");
        int lineColor = (int) map.get("lineColor");
        int circleColor = (int) map.get("circleColor");
        int recColor = (int) map.get("recColor");
        int dbxColor = (int) map.get("dbxColor");
        int zxColor = (int) map.get("zxColor");
        switch (chartType) {
            case ChartType.Circle:
                paint.setColor(circleColor);
                break;
            case ChartType.Line:
                paint.setColor(lineColor);
                break;
            case ChartType.Rec:
                paint.setColor(recColor);
                break;
            case ChartType.Dot:
                paint.setColor(dotColor);
                break;
            case ChartType.BrokenLine:
                paint.setColor(zxColor);
                break;
            case ChartType.Polygon:
                paint.setColor(dbxColor);
                break;
        }

        //绘制提示
        paint.setTextSize(Utils.dip2px(mContext, 10));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        if (mode == NONE) {
            float textPositionX = mCenterPoint[0] - Utils.dip2px(mContext, 10);
            float textPositionY = mCenterPoint[1];

            if (chartType == ChartType.Dot) {
                textPositionX = mCenterPoint[0] - mLabelBitmap.getWidth() / 2;
                textPositionY = mCenterPoint[1] - mLabelBitmap.getHeight() / 2 + Utils.dip2px(mContext, 5);
                if (mCenterPoint[0] >= (getWidth() - Utils.dip2px(mContext, 25))) {
                    textPositionX = getWidth() - Utils.dip2px(mContext, 50);
                }
                if (mCenterPoint[0] <= (Utils.dip2px(mContext, 5))) {
                    textPositionX = mCenterPoint[0];
                }
                if (mCenterPoint[1] <= Utils.dip2px(mContext, 10)) {
                    textPositionY = mCenterPoint[1] + mLabelBitmap.getHeight() / 2 + Utils.dip2px(mContext, 5);
                }
            }
            canvas.drawText(tip, textPositionX, textPositionY - 5, paint);
        }
        isFromReSet = false;

        // 绘制图形
        if (mLabelRect == null) {
            mLabelRect = new RectF();
        }
        mLabelRect.left = mDelDrawPoint[0];
        mLabelRect.top = mDelDrawPoint[1];
        mLabelRect.bottom = mDragDrawPoint[1];
        mLabelRect.right = mDragDrawPoint[0];

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        if (chartType == ChartType.Rec) {
            canvas.drawRect(mLabelRect, paint);
        } else if (chartType == ChartType.Circle) {
            float radious = (mLabelRect.right - mLabelRect.left) / 2;
            canvas.drawCircle(mLabelRect.centerX(), mLabelRect.centerY(), radious, paint);
        } else if (chartType == ChartType.Line) {
            canvas.drawLine(mLabelRect.left, mLabelRect.top, mLabelRect.right, mLabelRect.bottom, paint);
        } else {
            canvas.drawBitmap(mLabelBitmap, mViewMatrix, null);
        }

        if (mShowDashBorder) {
            // 画虚线框
            mDashPaint.setPathEffect(getPathEffect());
            canvas.save();
            canvas.concat(mViewMatrix);
            canvas.drawPath(mDashPath, mDashPaint);
            canvas.restore();

            // 画删除按钮：左上角
            //  canvas.drawBitmap(mDelBitmap, null, mDragleftTopDrawRectF, null);

            if (chartType != ChartType.Dot && chartType != ChartType.Polygon && chartType != ChartType.BrokenLine) {

                paint.setAntiAlias(true);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);

                float radious = 10;

                // 画拖放按钮：右下角
                canvas.drawCircle(mDragleftTopDrawRectF.centerX(), mDragleftTopDrawRectF.centerY(), radious, paint);
                canvas.drawCircle(mDragRightBottomDrawRectF.centerX(), mDragRightBottomDrawRectF.centerY(), radious, paint);

                if (chartType != ChartType.Line) {
                    canvas.drawCircle(mDragleftBottomDrawRectF.centerX(), mDragleftBottomDrawRectF.centerY(), radious, paint);
                    canvas.drawCircle(mDragRightCenterDrawRectF.centerX(), mDragRightCenterDrawRectF.centerY(), radious, paint);
                    canvas.drawCircle(mDragLeftCenterDrawRectF.centerX(), mDragLeftCenterDrawRectF.centerY(), radious, paint);
                    canvas.drawCircle(mDragRightTopDrawRectF.centerX(), mDragRightTopDrawRectF.centerY(), radious, paint);
                    canvas.drawCircle(mDragTopCenterDrawRectF.centerX(), mDragTopCenterDrawRectF.centerY(), radious, paint);
                    canvas.drawCircle(mDragBottomCenterDrawRectF.centerX(), mDragBottomCenterDrawRectF.centerY(), radious, paint);
                }
            }
        }

        //处理线、矩形、圆在屏幕上添加 没有拖动的情况
        if (chartType != ChartType.Dot) {
            float temp = mLabelRect.width();
            if (temp == 0 || Float.isNaN(temp)) {
                if (chartStateChangedListener != null) {
                    chartStateChangedListener.chartRemoved(chartType, tip, true);
                }
                ((ViewGroup) getParent()).removeView(DashFrameLayout.this);
            }
        }
    }

    @Override
    public void addView(View child) {
        removeAllViews();
        // 添加新view后，需再次调用OnPreDrawListener
        isRunPreDrawListener = true;
        mChildView = child;
        super.addView(child, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    public View getLabelView() {
        return mChildView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = true;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: {
                startX = event.getX();
                startY = event.getY();
                // 拖放按钮接收事件
                if (mShowDashBorder && (mDragRightBottomDrawRectF.contains(event.getX(), event.getY()) ||
                        mDragleftTopDrawRectF.contains(event.getX(), event.getY()))) {
                    if (mDragleftTopDrawRectF.contains(event.getX(), event.getY())) {
                        isRightBottom = true;
                    } else {
                        isRightBottom = false;
                    }
                    mode = SINGLE_ZOOM;
                    showDashBorder(true);
                    mPreDistance = Utils.calcDistance(mCenterPoint[0], mCenterPoint[1], event.getX(), event.getY());
                    mPreRotation = Utils.calcRotation(mCenterPoint[0], mCenterPoint[1], event.getX(), event.getY());
                    break;
                } else if (chartType != ChartType.Line) {
                    if (mShowDashBorder && (mDragleftBottomDrawRectF.contains(event.getX(), event.getY()) ||
                            mDragRightCenterDrawRectF.contains(event.getX(), event.getY()) ||
                            mDragLeftCenterDrawRectF.contains(event.getX(), event.getY()) ||
                            mDragRightTopDrawRectF.contains(event.getX(), event.getY()) ||
                            mDragTopCenterDrawRectF.contains(event.getX(), event.getY()) ||
                            mDragBottomCenterDrawRectF.contains(event.getX(), event.getY()))) {

                        if (mDragleftBottomDrawRectF.contains(event.getX(), event.getY())) {
                            isLeftBottom = true;
                        } else {
                            isLeftBottom = false;
                        }
                        if (mDragRightTopDrawRectF.contains(event.getX(), event.getY())) {
                            isRightTop = true;
                        } else {
                            isRightTop = false;
                        }
                        if (mDragLeftCenterDrawRectF.contains(event.getX(), event.getY()) ||
                                mDragTopCenterDrawRectF.contains(event.getX(), event.getY())) {
                            isRightBottom = true;
                        } else {
                            isRightBottom = false;
                        }
                        mode = SINGLE_ZOOM;
                        showDashBorder(true);
                        mPreDistance = Utils.calcDistance(mCenterPoint[0], mCenterPoint[1], event.getX(), event.getY());
                        mPreRotation = Utils.calcRotation(mCenterPoint[0], mCenterPoint[1], event.getX(), event.getY());
                        break;
                    }

                }


                /**
                 * 判断虚线框是否接收事件；
                 * 因旋转后无法直接判断事件是否在rect内，
                 * 可将坐标点反向旋转后再与未进行旋转的rect进行比较判断
                 */
                mDashDrawRectF.set(mDashRectF);
                mViewNoRotateMatrix.mapRect(mDashDrawRectF);
                Matrix inverseMatrix = new Matrix();
                // 获取反向旋转矩阵
                inverseMatrix.postRotate(360 - mCurRotation, scalePointX, scalePointY);
                // 将当前坐标反向旋转，再进行判断
                float[] eventPoint = {event.getX(), event.getY()};
                // inverseMatrix.mapPoints(eventPoint);

                RectF moveRect = new RectF();
                if (chartType == ChartType.Line) {//直线可以旋转需要特殊处理
                    float width = Math.abs(mDragRightBottomDrawRectF.centerX() - mDragleftTopDrawRectF.centerX()) / 2;
                    float height = Math.abs(mDragRightBottomDrawRectF.centerY() - mDragleftTopDrawRectF.centerY()) / 2;
                    if (width < 30) {
                        width = 30;
                    }
                    if (height < 30) {
                        height = 30;
                    }
                    moveRect.left = (int) (mCenterPoint[0] - width / 2);
                    moveRect.top = (int) (mCenterPoint[1] - height / 2);
                    moveRect.right = (int) (mCenterPoint[0] + width / 2);
                    moveRect.bottom = (int) (mCenterPoint[1] + height / 2);

                    if (moveRect.left > moveRect.right) {
                        float temp = moveRect.left;
                        moveRect.left = moveRect.right;
                        moveRect.right = temp;
                    }
                    if (moveRect.top > moveRect.bottom) {
                        float temp = moveRect.top;
                        moveRect.top = moveRect.bottom;
                        moveRect.bottom = temp;
                    }

                    if (moveRect.contains(eventPoint[0], eventPoint[1])) {
                        mode = DRAG;
                        mInitTouchX = event.getX();
                        mInitTouchY = event.getY();
                        mLastTouchX = event.getX();
                        mLastTouchY = event.getY();
                    } else {
                        handled = false;
                    }
                } else if (mDashDrawRectF.contains(eventPoint[0], eventPoint[1])) {
                    mode = DRAG;

                    mInitTouchX = event.getX();
                    mInitTouchY = event.getY();

                    mLastTouchX = event.getX();
                    mLastTouchY = event.getY();
                } else {
                    handled = false;
                }

                showDashBorder(handled);
                if (handled && mListener != null) {
                    mListener.onFocus();
                    mAlwaysInTapRegion = true;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                if (mAlwaysInTapRegion) {
                    float deltaX = event.getX() - mInitTouchX;
                    float deltaY = event.getY() - mInitTouchY;
                    // 判断是否在点击区域内
                    if ((deltaX * deltaX) + (deltaY * deltaY) > mTouchSlopSquare) {
                        mAlwaysInTapRegion = false;
                    }
                }
                if (mode == SINGLE_ZOOM || mode == DOUBLE_ZOOM) {

                    if (isRightBottom == false) {
                        scalePointX = left_top_x;
                        scalePointY = left_top_y;
                    } else {
                        scalePointX = right_bottom_x;
                        scalePointY = right_bottom_y;
                    }
                    if (isLeftBottom) {
                        scalePointX = right_bottom_x;
                        scalePointY = left_top_y;
                    }
                    if (isRightTop) {
                        scalePointX = left_top_x;
                        scalePointY = right_bottom_y;
                    }

                    float curDistance, endRotation;
                    if (mode == SINGLE_ZOOM) {
                        // 单指缩放、旋转
                        curDistance = Utils.calcDistance(mCenterPoint[0], mCenterPoint[1], event.getX(), event.getY());
                        endRotation = Utils.calcRotation(scalePointX, scalePointY, event.getX(), event.getY());
                    } else {
                        // 双指缩放、旋转
                        curDistance = Utils.calcDistance(event);
                        endRotation = Utils.calcRotation(event);
                    }
                    if (chartType != ChartType.Dot) {

                        float deltaX = event.getX() - startX;
                        float deltaY = event.getY() - startY;
                        startX = event.getX();
                        startY = event.getY();

                        float scare = curDistance / mPreDistance;
                        if (scare > 1.09) {//防止放大缩小太快出错
                            scare = 1.09f;
                        }
                        if (scare < 0.9f) {
                            scare = 0.9f;
                        }
                        if (chartType == ChartType.Rec) {

                            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                                //缩放x
                                recScale(scare, 1);
                            } else {
                                //缩放y
                                recScale(1, scare);
                            }
                            mPreDistance = curDistance;

                        } else {
                            scale(scare);
                            mPreDistance = curDistance;
                        }

                    }
                    if (chartType == ChartType.Line) {
                        rotate(endRotation - mPreRotation);
                        mPreRotation = endRotation;
                    }

                } else if (mode == DRAG) {
                    translate(event.getX() - mLastTouchX, event.getY() - mLastTouchY);
                    mLastTouchX = event.getX();
                    mLastTouchY = event.getY();
                }
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                //删除事件已激活，进行删除操作
                if (mShowDashBorder && deleteStateOpen) {
                    if (getParent() instanceof ViewGroup) {
                        if (chartStateChangedListener != null) {
                            chartStateChangedListener.chartRemoved(chartType, tip, false);
                        }
                        ((ViewGroup) getParent()).removeView(DashFrameLayout.this);
                    }
                }
                mode = NONE;
                mPreRotation = 0.f;
                if (mAlwaysInTapRegion && mPreShowDashBorder && mListener != null) {
                    mListener.onClick();
                }
                mAlwaysInTapRegion = false;
                invalidate();
                break;
            }
        }
        return handled;
    }


    //矩形缩放特殊处理
    private void recScale(float scalex, float scaley) {

        if (isRightBottom == false) {
            scalePointX = left_top_x;
            scalePointY = left_top_y;
        } else {
            scalePointX = right_bottom_x;
            scalePointY = right_bottom_y;
        }
        if (isLeftBottom) {
            scalePointX = right_bottom_x;
            scalePointY = left_top_y;
        }
        if (isRightTop) {
            scalePointX = left_top_x;
            scalePointY = right_bottom_y;
        }

        Log.i("scalex==", scalex + "");
        Log.i("scaley==", scaley + "");

        mViewMatrix.postScale(scalex, scaley, scalePointX, scalePointY);
        mViewNoRotateMatrix.postScale(scalex, scaley, scalePointX, scalePointY);
        updateBtnDrawRect();
        while (mDelDrawPoint[0] < 0 || mDragDrawPoint[0] > getWidth() || mDelDrawPoint[1] < 0 || mDragDrawPoint[1] > getHeight()) {
            if (scalex == 1) {
                scaley -= 0.1;
                mCurScaleFactor = scaley;
            } else {
                scalex -= 0.1;
                mCurScaleFactor = scalex;
            }

            mViewMatrix.postScale(scalex, scaley, scalePointX, scalePointY);
            mViewNoRotateMatrix.postScale(scalex, scaley, scalePointX, scalePointY);
            updateBtnDrawRect();
        }

        float x1 = mDragleftTopDrawRectF.centerX();
        float y1 = mDragleftTopDrawRectF.centerY();
        float x2 = mDragRightBottomDrawRectF.centerX();
        float y2 = mDragRightBottomDrawRectF.centerY();

        float marginx = Math.abs(x2 - x1) / 2;
        float marginy = Math.abs(y2 - y1) / 2;
        mCenterPoint[0] = x1 + marginx;
        mCenterPoint[1] = y1 + marginy;
    }

    private void scale(float scale) {
        float curScale = mCurScaleFactor * scale;
        mCurScaleFactor = curScale;

        float sx = mCenterPoint[0];
        float sy = mCenterPoint[1];

        if (chartType == ChartType.Line) {
            if (isRightBottom == false) {
                scalePointX = left_top_x;
                scalePointY = left_top_y;
            } else {
                scalePointX = right_bottom_x;
                scalePointY = right_bottom_y;
            }
            sx = scalePointX;
            sy = scalePointY;
        }

        mViewMatrix.postScale(scale, scale, sx, sy);
        mViewNoRotateMatrix.postScale(scale, scale, sx, sy);
        updateBtnDrawRect();

        float left = mDelDrawPoint[0];
        float leftTop = mDelDrawPoint[1];
        float right = mDragDrawPoint[0];
        float rightBottom = mDragDrawPoint[1];

        if (chartType == ChartType.Line) {
            if (left > right) {
                float temp = left;
                left = right;
                right = temp;
            }
            if (leftTop > rightBottom) {
                float temp = leftTop;
                leftTop = rightBottom;
                rightBottom = temp;
            }
        }

        while (left < 0 || right > getWidth() || leftTop < 0 || rightBottom > getHeight()) {
            scale -= 0.1;
            mCurScaleFactor = scale;
            mViewMatrix.postScale(scale, scale, sx, sy);
            mViewNoRotateMatrix.postScale(scale, scale, sx, sy);
            updateBtnDrawRect();

            left = mDelDrawPoint[0];
            leftTop = mDelDrawPoint[1];
            right = mDragDrawPoint[0];
            rightBottom = mDragDrawPoint[1];

            if (chartType == ChartType.Line) {
                if (left > right) {
                    float temp = left;
                    left = right;
                    right = temp;
                }
                if (leftTop > rightBottom) {
                    float temp = leftTop;
                    leftTop = rightBottom;
                    rightBottom = temp;
                }
            }
        }

        if (chartType == ChartType.Line) {

            float x1 = mDragleftTopDrawRectF.centerX();
            float y1 = mDragleftTopDrawRectF.centerY();
            float x2 = mDragRightBottomDrawRectF.centerX();
            float y2 = mDragRightBottomDrawRectF.centerY();

            float marginx = Math.abs(x2 - x1) / 2;
            float marginy = Math.abs(y2 - y1) / 2;
            if (x1 < x2) {
                mCenterPoint[0] = x1 + marginx;
            } else {
                mCenterPoint[0] = x2 + marginx;
            }
            if (y1 < y2) {
                mCenterPoint[1] = y1 + marginy;
            } else {
                mCenterPoint[1] = y2 + marginy;
            }
        }

    }

    private void rotate(float degree) {
        System.out.print("degree===" + degree);

        if (isRightBottom == false) {
            scalePointX = left_top_x;
            scalePointY = left_top_y;
        } else {
            scalePointX = right_bottom_x;
            scalePointY = right_bottom_y;
        }
        mCurRotation += degree;
        mViewMatrix.postRotate(degree, scalePointX, scalePointY);
        updateBtnDrawRect();


        float x1 = mDragleftTopDrawRectF.centerX();
        float y1 = mDragleftTopDrawRectF.centerY();
        float x2 = mDragRightBottomDrawRectF.centerX();
        float y2 = mDragRightBottomDrawRectF.centerY();

        float marginx = Math.abs(x2 - x1) / 2;
        float marginy = Math.abs(y2 - y1) / 2;
        if (x1 < x2) {
            mCenterPoint[0] = x1 + marginx;
        } else {
            mCenterPoint[0] = x2 + marginx;
        }
        if (y1 < y2) {
            mCenterPoint[1] = y1 + marginy;
        } else {
            mCenterPoint[1] = y2 + marginy;
        }

    }

    private void translate(float dx, float dy) {
        // 改变旋转、缩放中心点
        mCenterPoint[0] += dx;
        mCenterPoint[1] += dy;
        //点只需判断中心点
        if (chartType == ChartType.Dot) {
            // 判断中心点是否移出截图区域
            if (mCenterPoint[0] <= 0) {
                mCenterPoint[0] -= dx;
                dx = 0;
            } else if (mCenterPoint[0] >= getWidth()) {
                mCenterPoint[0] -= dx;
                dx = 0;
            }
            if (mCenterPoint[1] <= 0) {
                mCenterPoint[1] -= dy;
                dy = 0;
            } else if (mCenterPoint[1] >= getHeight()) {
                mCenterPoint[1] -= dy;
                dy = 0;
            }
        }

        if (chartType == ChartType.Line || chartType == ChartType.Circle || chartType == ChartType.Rec || chartType == ChartType.BrokenLine || chartType == ChartType.Polygon) {
            if (mDelDrawPoint[0] != 0 && mDragDrawPoint[0] != 0) {
                float width = Math.abs(mDragDrawPoint[0] - mDelDrawPoint[0]);
                float height = Math.abs(mDragDrawPoint[1] - mDelDrawPoint[1]);
                // 判断中心点是否移出截图区域
                if (mCenterPoint[0] <= 0 + width / 2) {
                    mCenterPoint[0] -= dx;
                    dx = 0;
                } else if (mCenterPoint[0] >= getWidth() - width / 2) {
                    mCenterPoint[0] -= dx;
                    dx = 0;
                }
                if (mCenterPoint[1] - height / 2 <= 0) {
                    mCenterPoint[1] -= dy;
                    dy = 0;
                } else if (mCenterPoint[1] + height / 2 >= getHeight()) {
                    mCenterPoint[1] -= dy;
                    dy = 0;
                }
            }
        }

        mViewMatrix.postTranslate(dx, dy);
        mViewNoRotateMatrix.postTranslate(dx, dy);
        updateBtnDrawRect();


    }


    /**
     * 更新当前删除、缩放按钮绘制位置
     */
    private void updateBtnDrawRect() {
        // 计算删除按钮绘制位置
        mDelDrawPoint[0] = mDelPoint[0];
        mDelDrawPoint[1] = mDelPoint[1];
        mViewMatrix.mapPoints(mDelDrawPoint);
        mDragleftTopDrawRectF.left = mDelDrawPoint[0] - (recWidth >> 1);
        mDragleftTopDrawRectF.top = mDelDrawPoint[1] - (recWidth >> 1);
        mDragleftTopDrawRectF.right = mDelDrawPoint[0] + (recWidth >> 1);
        mDragleftTopDrawRectF.bottom = mDelDrawPoint[1] + (recWidth >> 1);

        // 计算缩放按钮绘制位置
        mDragDrawPoint[0] = mDragPoint[0];
        mDragDrawPoint[1] = mDragPoint[1];
        mViewMatrix.mapPoints(mDragDrawPoint);
        mDragRightBottomDrawRectF.left = mDragDrawPoint[0] - (recWidth >> 1);
        mDragRightBottomDrawRectF.top = mDragDrawPoint[1] - (recWidth >> 1);
        mDragRightBottomDrawRectF.right = mDragDrawPoint[0] + (recWidth >> 1);
        mDragRightBottomDrawRectF.bottom = mDragDrawPoint[1] + (recWidth >> 1);

        if (chartType != ChartType.Line) {
            mDragleftBottomDrawRectF.left = mDragleftTopDrawRectF.left;
            mDragleftBottomDrawRectF.top = mDragRightBottomDrawRectF.top;
            mDragleftBottomDrawRectF.right = mDragleftTopDrawRectF.right;
            mDragleftBottomDrawRectF.bottom = mDragRightBottomDrawRectF.bottom;

            float tempHeight = mDragleftBottomDrawRectF.centerY() - mDragleftTopDrawRectF.centerY();

            mDragLeftCenterDrawRectF.left = mDragleftTopDrawRectF.left;
            mDragLeftCenterDrawRectF.top = mDragleftTopDrawRectF.centerY() + tempHeight / 2 - recWidth / 2;
            mDragLeftCenterDrawRectF.right = mDragleftTopDrawRectF.right;
            mDragLeftCenterDrawRectF.bottom = mDragleftTopDrawRectF.centerY() + tempHeight / 2 + recWidth / 2;

            mDragRightTopDrawRectF.left = mDragRightBottomDrawRectF.left;
            mDragRightTopDrawRectF.top = mDragleftTopDrawRectF.top;
            mDragRightTopDrawRectF.right = mDragRightBottomDrawRectF.right;
            mDragRightTopDrawRectF.bottom = mDragleftTopDrawRectF.bottom;

            mDragRightCenterDrawRectF.left = mDragRightTopDrawRectF.left;
            mDragRightCenterDrawRectF.top = mDragRightTopDrawRectF.centerY() + tempHeight / 2 - recWidth / 2;
            mDragRightCenterDrawRectF.right = mDragRightTopDrawRectF.right;
            mDragRightCenterDrawRectF.bottom = mDragRightTopDrawRectF.centerY() + tempHeight / 2 + recWidth / 2;


            float tempWidth = mDragRightTopDrawRectF.centerX() - mDragleftTopDrawRectF.centerX();
            mDragTopCenterDrawRectF.left = mDragleftTopDrawRectF.centerX() + tempWidth / 2 - recWidth / 2;
            mDragTopCenterDrawRectF.top = mDragleftTopDrawRectF.top;
            mDragTopCenterDrawRectF.right = mDragleftTopDrawRectF.centerX() + tempWidth / 2 + recWidth / 2;
            mDragTopCenterDrawRectF.bottom = mDragleftTopDrawRectF.bottom;

            mDragBottomCenterDrawRectF.left = mDragTopCenterDrawRectF.left;
            mDragBottomCenterDrawRectF.top = mDragleftBottomDrawRectF.top;
            mDragBottomCenterDrawRectF.right = mDragTopCenterDrawRectF.right;
            mDragBottomCenterDrawRectF.bottom = mDragleftBottomDrawRectF.bottom;

        }


    }

    private PathEffect getPathEffect() {
        mDashPhase %= mDashMinSize;
        PathEffect effect = mPathEffects[mDashPhase];
        if (effect == null) {
            effect = new DashPathEffect(mDashFloats, mDashPhase);
            mPathEffects[mDashPhase] = effect;
        }
        mDashPhase++;
        return effect;
    }

    /**
     * 是否显示虚线设置
     * @param show
     */
    public void showDashBorder(boolean show) {
        mPreShowDashBorder = mShowDashBorder;
        mShowDashBorder = show;
        if (mShowDashBorder) {
            if (getParent() instanceof ViewGroup) {
                getParent().bringChildToFront(this);
            }
            mRefreshHandler.sendEmptyMessage(MSG_DRAW);
        } else {
            mRefreshHandler.sendEmptyMessage(MSG_ERASE);
            isFromReSet = true;
        }
    }

    public void setLabelListener(LabelListener listener) {
        mListener = listener;
    }

    private  class RefreshHandler extends Handler {

        private WeakReference<View> mWeakReference;

        public RefreshHandler(View view) {
            mWeakReference = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_DRAW:
                    View view = mWeakReference.get();
                    if (view != null) {
                        view.invalidate();
                    }
                    break;
                case MSG_ERASE:
                    removeMessages(MSG_DRAW);
                    view = mWeakReference.get();
                    if (view != null) {
                        view.invalidate();
                    }
                    break;
            }
        }
    }


    public interface LabelListener {
        void onFocus();

        void onClick();
    }

}
