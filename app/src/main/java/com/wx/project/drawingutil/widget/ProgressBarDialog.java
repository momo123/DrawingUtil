package com.wx.project.drawingutil.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;

import com.wx.project.drawingutil.R;
import com.wx.project.drawingutil.utils.DeviceUtil;


public class ProgressBarDialog extends AlertDialog{

	public Context mContext;

	public ProgressBarDialog(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_progress_bar);
		//设置dialog全屏
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = DeviceUtil.getWidth((Activity) mContext);
		lp.height =DeviceUtil.getHeight((Activity) mContext);
		this.getWindow().setAttributes(lp);
		this.getWindow().setBackgroundDrawable(new ColorDrawable());
	}
}
