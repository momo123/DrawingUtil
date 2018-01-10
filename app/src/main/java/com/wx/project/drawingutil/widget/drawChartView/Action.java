package com.wx.project.drawingutil.widget.drawChartView;

import android.graphics.*;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;

//基础类
public class Action {
	public int color;

	Action() {
		color=Color.BLACK;
	}

	Action(int color) {
		this.color = color;
	}

	public void draw(Canvas canvas) {
	}
	
	public void move(float mx,float my){
		
	}

	public void move(float sX,float sY, float mx,float my){}
}