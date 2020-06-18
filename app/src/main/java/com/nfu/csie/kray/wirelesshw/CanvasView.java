package com.nfu.csie.kray.wirelesshw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CanvasView extends View {
    public Paint mPaint;
    public static Canvas mCanvas;
    public ArrayList<Pair<Integer, Integer>> circle_list;
    private int radius = 40;

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        circle_list = new ArrayList<Pair<Integer, Integer>>();
    }

    public void drawCircle(int x, int y) {
        circle_list.add(new Pair<Integer, Integer>(x, y));
        //important. Refreshes the view by calling onDraw function
        invalidate();
    }

    //what I want to draw is here
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(mCanvas);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        for(Pair<Integer, Integer> item : circle_list)
            canvas.drawCircle(item.first, item.second, radius, mPaint);
    }
}
