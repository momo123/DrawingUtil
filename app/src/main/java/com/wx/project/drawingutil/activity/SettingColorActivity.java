package com.wx.project.drawingutil.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wx.project.drawingutil.R;
import com.wx.project.drawingutil.utils.ChartType;
import com.wx.project.drawingutil.utils.SharedPreferencesUtil;
import com.wx.project.drawingutil.widget.colorPicker.ColorPickerDialog;
import com.wx.project.drawingutil.widget.colorPicker.ColorPickerSwatch;

import java.util.Map;

/**
 * 设置颜色
 *
 * @author WangXin
 */
public class SettingColorActivity extends AppCompatActivity {

    ColorPickerDialog colorPickerDialog;
    int chartType = 0;
    private TextView textDot;
    private TextView textLine;
    private TextView textCircle;
    private TextView textRec;
    private TextView textDbx;
    private TextView textZx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysic_setting);
        initView();
        initColor();
    }

    private void initView() {
        //title init
        ImageView leftImg = (ImageView) findViewById(R.id.imgLeft);
        TextView title = (TextView) findViewById(R.id.titleContent);
        title.setText(getResources().getString(R.string.setting_color));
        leftImg.setVisibility(View.VISIBLE);
        leftImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSetting();
                finish();

            }
        });
        textDot = (TextView) findViewById(R.id.textDot);
        textLine = (TextView) findViewById(R.id.textLine);
        textCircle = (TextView) findViewById(R.id.textCircle);
        textRec = (TextView) findViewById(R.id.textRec);
        textDbx = (TextView) findViewById(R.id.textDBX);
        textZx = (TextView) findViewById(R.id.textZX);
    }

    /**
     * 点击事件
     *
     * @param v
     */
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.layoutDot:
                chartType = ChartType.Dot;
                showDialog();
                break;
            case R.id.layoutLine:
                chartType = ChartType.Line;
                showDialog();
                break;
            case R.id.layoutCircle:
                chartType = ChartType.Circle;
                showDialog();
                break;
            case R.id.layoutRec:
                chartType = ChartType.Rec;
                showDialog();
                break;
            case R.id.layoutDBX:
                chartType = ChartType.Polygon;
                showDialog();
                break;
            case R.id.layoutZX:
                chartType = ChartType.BrokenLine;
                showDialog();
                break;
        }
    }

    /**
     * 显示设置颜色对话框
     */
    private void showDialog() {
        if (colorPickerDialog == null) {
            colorPickerDialog = new ColorPickerDialog();
            colorPickerDialog.initialize(R.string.dialog_title, new int[]{Color.CYAN, Color.LTGRAY, Color.BLACK, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.RED, Color.DKGRAY, Color.YELLOW}, Color.YELLOW, 3, 2);
            colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                @Override
                public void onColorSelected(int color) {
                    switch (chartType) {
                        case ChartType.Dot:
                            textDot.setBackgroundColor(color);
                            break;
                        case ChartType.Line:
                            textLine.setBackgroundColor(color);
                            break;
                        case ChartType.Circle:
                            textCircle.setBackgroundColor(color);
                            break;
                        case ChartType.Rec:
                            textRec.setBackgroundColor(color);
                            break;
                        case ChartType.Polygon:
                            textDbx.setBackgroundColor(color);
                            break;
                        case ChartType.BrokenLine:
                            textZx.setBackgroundColor(color);
                            break;
                    }
                }
            });
        }
        colorPickerDialog.show(getSupportFragmentManager(), "colorpicker");
        switch (chartType) {
            case ChartType.Dot:
                colorPickerDialog.setColorSelected(((ColorDrawable) textDot.getBackground()).getColor());
                break;
            case ChartType.Line:
                colorPickerDialog.setColorSelected(((ColorDrawable) textLine.getBackground()).getColor());
                break;
            case ChartType.Circle:
                colorPickerDialog.setColorSelected(((ColorDrawable) textCircle.getBackground()).getColor());
                break;
            case ChartType.Rec:
                colorPickerDialog.setColorSelected(((ColorDrawable) textRec.getBackground()).getColor());
                break;
            case ChartType.Polygon:
                colorPickerDialog.setColorSelected(((ColorDrawable) textDbx.getBackground()).getColor());
                break;
            case ChartType.BrokenLine:
                colorPickerDialog.setColorSelected(((ColorDrawable) textZx.getBackground()).getColor());
                break;
        }
    }

    private void saveSetting() {
        SharedPreferencesUtil.setChartColorSetting(this, ((ColorDrawable) textDot.getBackground()).getColor(), ((ColorDrawable) textLine.getBackground()).getColor()
                , ((ColorDrawable) textCircle.getBackground()).getColor(), ((ColorDrawable) textRec.getBackground()).getColor()
                , ((ColorDrawable) textDbx.getBackground()).getColor(), ((ColorDrawable) textZx.getBackground()).getColor());
    }

    /**
     * 初始化颜色
     */
    private void initColor() {
        Map<String, Object> map = SharedPreferencesUtil.getChartColorSetting(this);
        int dotColor = (int) map.get("dotColor");
        int lineColor = (int) map.get("lineColor");
        int circleColor = (int) map.get("circleColor");
        int recColor = (int) map.get("recColor");
        int dbxColor = (int) map.get("dbxColor");
        int zxColor = (int) map.get("zxColor");
        textDot.setBackgroundColor(dotColor);
        textLine.setBackgroundColor(lineColor);
        textCircle.setBackgroundColor(circleColor);
        textRec.setBackgroundColor(recColor);
        textDbx.setBackgroundColor(dbxColor);
        textZx.setBackgroundColor(zxColor);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveSetting();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
