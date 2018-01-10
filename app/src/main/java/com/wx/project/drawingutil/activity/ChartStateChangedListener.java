package com.wx.project.drawingutil.activity;

/**
 * Created by WangXin on 2016/11/29.
 */
public interface ChartStateChangedListener {
    //图形删除后回调
    void chartRemoved(int chartType, String tip, boolean isNeedDealChartNumTip);
}
