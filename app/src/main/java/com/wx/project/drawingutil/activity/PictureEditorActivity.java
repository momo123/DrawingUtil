package com.wx.project.drawingutil.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wx.project.drawingutil.R;
import com.wx.project.drawingutil.utils.ChartType;
import com.wx.project.drawingutil.utils.DateUtil;
import com.wx.project.drawingutil.utils.DeviceUtil;
import com.wx.project.drawingutil.utils.FileUtil;
import com.wx.project.drawingutil.utils.Utils;
import com.wx.project.drawingutil.widget.ProgressBarDialog;
import com.wx.project.drawingutil.widget.drawChartView.Action;
import com.wx.project.drawingutil.widget.drawChartView.BrokenLineView;
import com.wx.project.drawingutil.widget.drawChartView.CaptureView;
import com.wx.project.drawingutil.widget.drawChartView.MyCircle;
import com.wx.project.drawingutil.widget.drawChartView.MyLine;
import com.wx.project.drawingutil.widget.drawChartView.MyPoint;
import com.wx.project.drawingutil.widget.drawChartView.MyRect;
import com.wx.project.drawingutil.widget.drawChartView.PaletteView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WangXin
 */
public class PictureEditorActivity extends Activity implements ChartStateChangedListener {
    private SurfaceView surfaceView;
    private CaptureView mCaptureView;
    private RelativeLayout layoutPanelContent;
    private LinearLayout layoutPanel;
    private ImageView img;//保存的时候用到（因为surfaceview 截图会黑屏）
    private LinearLayout layoutMain;
    private TextView deleteStateTv;
    private ImageView ivMenu;

    private Map<String, Object> lineNumWithChart = new HashMap<>();//存放各种类型图形的个数
    private final int MAX_NUM = 11;//设置可添加图形的个数
    private Bitmap bitmap;
    private BitmapFactory.Options options;
    private PopupWindow popupWindow;
    private ProgressBarDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_editor);
        //防止手机自动休眠或锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        options = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_bg, options);
        options.inJustDecodeBounds = true;//设置之后，获得的bitmap为null
        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_bg, options);
        initView();
        //初始化数据
        clearLineNumWithChart();
    }

    /**
     * 初始化布局控件
     */
    private void initView() {

        TextView title = (TextView) findViewById(R.id.titleContent);
        title.setText("画图");

        deleteStateTv = (TextView) findViewById(R.id.deleteStateTv);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivMenu.setVisibility(View.VISIBLE);

        //设置页面背景色
        layoutMain = (LinearLayout) findViewById(R.id.layoutMain);
        layoutMain.setBackgroundColor(getResources().getColor(R.color.color_e1dede));
        img = (ImageView) findViewById(R.id.img);

        //surfaceView初始化
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setTag("initScare-false");
        surfaceView.getHolder().addCallback(callback);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        layoutPanel = (LinearLayout) findViewById(R.id.layoutPanel);
        layoutPanelContent = (RelativeLayout) findViewById(R.id.layoutPanelContent);
        mCaptureView = (CaptureView) findViewById(R.id.capture_view);
        mCaptureView.setChartStateChangedListener(this);
        //动态设置高度
        ViewGroup.LayoutParams pp = layoutPanelContent.getLayoutParams();
        pp.height = DeviceUtil.getWidth(this) * 3 / 4;
        layoutPanelContent.setLayoutParams(pp);

        //动态设置底部菜单宽度
        initScrollChildWidth();
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing() == true) {
                    popupWindow.dismiss();
                } else {
                    //显示菜单
                    showPopupWindow();
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void clearLineNumWithChart() {
        lineNumWithChart.put("DotNum", 0);
        lineNumWithChart.put("LineNum", 0);
        lineNumWithChart.put("CircleNum", 0);
        lineNumWithChart.put("RecNum", 0);
        lineNumWithChart.put("BrokenNum", 0);
        lineNumWithChart.put("PolyNum", 0);
    }

    /**
     * 动态设置布局宽度
     */
    private void initScrollChildWidth() {
        //动态改变布局宽度
        LinearLayout layoutScrollMain = (LinearLayout) findViewById(R.id.layoutGraph);
        int len = layoutScrollMain.getChildCount();
        for (int i = 0; i < len; i++) {
            View view = layoutScrollMain.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                params.width = DeviceUtil.getWidth(this) / 6;
                view.setLayoutParams(params);
            }
        }
    }

    /**
     * 截图
     */
    private void capture() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cancleProgressDialog();
                surfaceView.setVisibility(View.GONE);
                layoutPanel.setDrawingCacheEnabled(true);
                layoutPanel.buildDrawingCache();
                Bitmap captureViewBitmap = Bitmap.createBitmap(layoutPanel.getDrawingCache());
                layoutPanel.setDrawingCacheEnabled(false);
                if (captureViewBitmap != null) {
                    String date = DateUtil.getFullDate();
                    String filePath = FileUtil.saveBitmapWithName("images", date+".png", captureViewBitmap);
                    Toast.makeText(PictureEditorActivity.this, "保存成功: " + filePath, Toast.LENGTH_SHORT).show();
                }
                surfaceView.setVisibility(View.VISIBLE);
                img.setVisibility(View.GONE);
            }
        }, 1000);
    }

    /**
     * 点击事件处理
     *
     * @param view
     */
    public void onClickListener(View view) {

        if (judgeIsDrawingChart() == true) {
            return;
        }
        switch (view.getId()) {
            case R.id.layoutLine:
                String tip = "";
                if (judgeChartNum() && judgeIsCanDrawCanvas()) {
                    addChart(ChartType.Line, false, true, 0, 0, 0, 0);
                }
                break;
            case R.id.layoutDot:
                if (judgeChartNum() && judgeIsCanDrawCanvas()) {
                    addChart(ChartType.Dot, false, true, 0, 0, 0, 0);
                }
                break;
            case R.id.layoutRec:
                if (judgeChartNum() && judgeIsCanDrawCanvas()) {
                    addChart(ChartType.Rec, false, true, 0, 0, 0, 0);
                }
                break;
            case R.id.layoutCircle:
                if (judgeChartNum() && judgeIsCanDrawCanvas()) {
                    addChart(ChartType.Circle, false, true, 0, 0, 0, 0);
                }
                break;

            case R.id.layoutZX:
                if (judgeChartNum() && judgeIsCanDrawCanvas()) {
                    tip = Utils.addChartNumTip(ChartType.BrokenLine, lineNumWithChart, true);
                    final String tipnew = tip;
                    final BrokenLineView brokenLineView = new BrokenLineView(this, false);
                    brokenLineView.setPaintColor(ChartType.BrokenLine);
                    brokenLineView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    layoutPanelContent.addView(brokenLineView);
                    brokenLineView.setOnDrawCompleteListener(new BrokenLineView.OnDrawCompleteListener() {
                        @Override
                        public void OnDrawComplete(List<Float> pointsX, List<Float> pointsY, float width, float height) {

                            layoutPanelContent.removeView(brokenLineView);
                            if (width != 0 && height != 0) {
                                mCaptureView.addBrokenChart(pointsX, pointsY, (int) width, (int) height, ChartType.BrokenLine, surfaceView.getWidth() / 2, surfaceView.getHeight() / 2, tipnew);
                            }
                        }
                    });
                }
                break;

            case R.id.layoutDBX:
                if (judgeChartNum() && judgeIsCanDrawCanvas()) {
                    tip = Utils.addChartNumTip(ChartType.Polygon, lineNumWithChart, true);
                    final String tipnew = tip;
                    final BrokenLineView brokenLineView = new BrokenLineView(this, false);
                    brokenLineView.setPaintColor(ChartType.Polygon);
                    brokenLineView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                    layoutPanelContent.addView(brokenLineView);
                    brokenLineView.setOnDrawCompleteListener(new BrokenLineView.OnDrawCompleteListener() {
                        @Override
                        public void OnDrawComplete(List<Float> pointsX, List<Float> pointsY, float width, float height) {

                            layoutPanelContent.removeView(brokenLineView);
                            if (width != 0 && height != 0) {
                                mCaptureView.addBrokenChart(pointsX, pointsY, (int) width, (int) height, ChartType.Polygon, surfaceView.getWidth() / 2, surfaceView.getHeight() / 2, tipnew);
                            }
                        }
                    });
                }
                break;
            case R.id.layoutDeleteAll:
                clearLineNumWithChart();
                view = layoutPanelContent.getChildAt(layoutPanelContent.getChildCount() - 1);
                if (view instanceof BrokenLineView) {
                    layoutPanelContent.removeView(view);
                }
                if (mCaptureView != null) {
                    FrameLayout frameLayout = mCaptureView.getmContainerLayout();
                    frameLayout.removeViews(1, frameLayout.getChildCount() - 1);
                }
                break;
            case R.id.layoutDelete:
                if (deleteStateTv.getText().toString().equals(getResources().getString(R.string.delete_single))) {
                    deleteStateTv.setText(getResources().getString(R.string.cancel_delete_single));
                    mCaptureView.changeDeleteState(true);
                } else {
                    deleteStateTv.setText(getResources().getString(R.string.delete_single));
                    mCaptureView.changeDeleteState(false);
                }
                break;
        }
    }

    /**
     * 添加点、线、圆、矩形
     *
     * @param chartType
     * @param isInitAction
     * @param isFromTopToBottom
     * @param left_top_x
     * @param left_top_y
     * @param right_bottom_x
     * @param right_bottom_y
     */
    public void addChart(final int chartType, boolean isInitAction, boolean isFromTopToBottom, float left_top_x, float left_top_y, float right_bottom_x, float right_bottom_y) {
        final String numTip = Utils.addChartNumTip(chartType, lineNumWithChart, true);
        final PaletteView paletteView = new PaletteView(PictureEditorActivity.this);
        paletteView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        paletteView.setCurrentPaintTool(chartType);
        if (isInitAction) {
            paletteView.setCurAction(left_top_x, left_top_y);
            paletteView.initAction(right_bottom_x, right_bottom_y);
        }
        layoutPanelContent.addView(paletteView);
        paletteView.setOnDrawCompleteListener(new PaletteView.OnDrawCompleteListener() {
            @Override
            public void OnDrawComplete(Action action) {
                float left_top_x = 0, left_top_y = 0;
                float right_bottom_x = 0, right_bottom_y = 0;
                switch (chartType) {
                    case ChartType.Dot:
                        MyPoint point = (MyPoint) action;
                        left_top_x = point.getX();
                        left_top_y = point.getY();
                        right_bottom_x = point.getX();
                        right_bottom_y = point.getY();
                        break;
                    case ChartType.Line:
                        MyLine line = (MyLine) action;
                        left_top_x = line.getStartX();
                        left_top_y = line.getStartY();
                        right_bottom_x = line.getStopX();
                        right_bottom_y = line.getStopY();
                        break;
                    case ChartType.Rec:
                        MyRect rect = (MyRect) action;
                        left_top_x = rect.getStartX();
                        left_top_y = rect.getStartY();
                        right_bottom_x = rect.getStopX();
                        right_bottom_y = rect.getStopY();
                        break;
                    case ChartType.Circle:
                        MyCircle circle = (MyCircle) action;
                        float centerX = 0, centerY = 0;
                        centerX = (circle.getStartX() + circle.getStopX()) / 2;
                        centerY = (circle.getStartY() + circle.getStopY()) / 2;
                        float radious = circle.getRadius();
                        left_top_x = centerX - radious;
                        left_top_y = centerY - radious;
                        right_bottom_x = centerX + radious;
                        right_bottom_y = centerY + radious;
                        break;

                }
                layoutPanelContent.removeView(paletteView);
                if (action != null) {
                    mCaptureView.addChart(chartType, left_top_x, left_top_y, right_bottom_x, right_bottom_y, numTip);
                }

            }
        });
    }


    /**
     * surfaceview 创建回调
     */
    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        @Override
        public void surfaceCreated(final SurfaceHolder holder) {
            if (bitmap != null) {
                canvasDrawBitmap();
                setScare();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }
    };

    private void canvasDrawBitmap() {
        try {
            System.out.println("开始画图");
            Canvas canvas = surfaceView.getHolder().lockCanvas();
            Paint p = new Paint();
            canvas.drawBitmap(bitmap, null, new Rect(0, 0, DeviceUtil.getWidth(PictureEditorActivity.this), surfaceView.getHeight()), p);
            //3. 解锁画布   更新提交屏幕显示内容
            if (surfaceView.getHolder() != null) {
                surfaceView.getHolder().unlockCanvasAndPost(canvas);
            }
        } catch (Exception e) {
            //home 建进入后台
        }
    }

    /**
     * 得到surfaceview和原始图片比例
     **/
    private void setScare() {
        if (((String) surfaceView.getTag()).contains("initScare-false")) {
            surfaceView.setTag("initScare-true");
            int width = DeviceUtil.getWidth(this);
            int height = DeviceUtil.getWidth(this) * 3 / 4;
            float width_scare = (float) width / options.outWidth;
            float height_scare = (float) height / options.outHeight;
            float wScare = (float) options.outWidth / width;
            float hScare = (float) options.outHeight / height;
            DecimalFormat df = new DecimalFormat("0.00");//格式化小数
            width_scare = Float.valueOf(df.format(width_scare));
            height_scare = Float.valueOf(df.format(height_scare));
            wScare = Float.valueOf(df.format(wScare));
            hScare = Float.valueOf(df.format(hScare));
            mCaptureView.width_scare = width_scare;
            mCaptureView.height_scare = height_scare;
            mCaptureView.wScare = wScare;
            mCaptureView.hScare = hScare;
            Log.i("PictureEditor_wScare", wScare + "");
            Log.i("PictureEditor_hScare", hScare + "");
        }

    }

    /**
     * 判断是否处于激活单个删除状态，如果处于单个删除状态，添加图形时默认关闭删除状态
     *
     * @return
     */
    private boolean judgeIsCanDrawCanvas() {
        if (deleteStateTv.getText().toString().equals(getResources().getString(R.string.cancel_delete_single))) {
            //处于删除激活状态，取消单个删除
            deleteStateTv.setText(getResources().getString(R.string.delete_single));
            mCaptureView.changeDeleteState(false);
            return true;
        }
        return true;
    }

    /**
     * 判断是否正在绘制多边形或折线
     *
     * @return
     */
    private boolean judgeIsDrawingChart() {
        View view = layoutPanelContent.getChildAt(layoutPanelContent.getChildCount() - 1);
        if (view instanceof PaletteView) {
            layoutPanelContent.removeView(view);
        }
        if (view instanceof BrokenLineView) {
            if (((BrokenLineView) view).getpointXSize() == 0) {
                layoutPanelContent.removeView(view);
                return false;
            } else {
                Toast.makeText(PictureEditorActivity.this, getResources().getString(R.string.double_click_tip), Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    /**
     * 判断绘制个数是否超过最大限制
     *
     * @return
     */
    private boolean judgeChartNum() {
        FrameLayout frameLayout = mCaptureView.getmContainerLayout();
        int count = frameLayout.getChildCount();
        if (count >= MAX_NUM) {
            Toast.makeText(this, getResources().getString(R.string.analysys_chart_num_tip), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 显示菜单
     */
    private void showPopupWindow() {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.layout_menu, null);
            popupWindow = new PopupWindow(contentView);
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            //外部是否可以点击
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            TextView tvSave = (TextView) contentView.findViewById(R.id.tv_save);
            TextView tvSettingColor = (TextView) contentView.findViewById(R.id.tv_setting_color);
            tvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    showProgressDialog();
                    img.setImageBitmap(bitmap);
                    img.setVisibility(View.VISIBLE);
                    capture();
                }
            });
            tvSettingColor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PictureEditorActivity.this, SettingColorActivity.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                }
            });
        }
        popupWindow.showAsDropDown(ivMenu);
    }

    /**
     * 图形删除回调
     *
     * @param chartType
     * @param tip
     */
    @Override
    public void chartRemoved(int chartType, String tip, boolean isNeedDealChartNumTip) {
        if (isNeedDealChartNumTip) {
            Utils.delChartNumTip(chartType, lineNumWithChart);
        }
    }

    private void showProgressDialog(){
        if (dialog == null){
            dialog = new ProgressBarDialog(this);
        }
        dialog.show();
    }

    private void cancleProgressDialog(){
        if (dialog != null){
            dialog.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.recycleBitmap(bitmap);
    }
}
