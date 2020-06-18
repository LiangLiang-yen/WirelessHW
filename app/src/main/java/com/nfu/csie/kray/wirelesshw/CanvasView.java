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
    private ArrayList<Pair<Pair<Integer, Integer>, Integer>> circle_list;
    private int radius = 40;

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        circle_list = new ArrayList<Pair<Pair<Integer, Integer>, Integer>>();
    }

    public void drawCircle(int x, int y, int level) {
        circle_list.add(new Pair<Pair<Integer, Integer>, Integer>(new Pair<Integer, Integer>(x, y), level));
        //important. Refreshes the view by calling onDraw function
        invalidate();
    }

    public void clearCircle(){
        circle_list = new ArrayList<Pair<Pair<Integer, Integer>, Integer>>();
        invalidate();
    }

    public ArrayList<Pair<Pair<Integer, Integer>, Integer>> getCircle_list(){
        return circle_list;
    }

    public Pair<Integer, Integer> findRoute(){
        int length = (int)(circle_list.size()/3);
        for(int i=0; i<length; ++i){
            int a = 3*i, b = 3*i+1, c = 3*i+2;
            int r1 = radius * (-circle_list.get(a).second) / 5;
            int r2 = radius * (-circle_list.get(b).second) / 5;
            int r3 = radius * (-circle_list.get(c).second) / 5;
            double x1 = ((Math.pow(r1, 2)-Math.pow(r2, 2)-((-circle_list.get(a).first.second-circle_list.get(b).first.second)*(circle_list.get(b).first.second-circle_list.get(a).first.second)))/
                    (circle_list.get(b).first.first-circle_list.get(a).first.first)) + circle_list.get(a).first.first + circle_list.get(b).first.first;
            double x2 = ((Math.pow(r2, 2)-Math.pow(r3, 2)-((-circle_list.get(b).first.second-circle_list.get(c).first.second)*(circle_list.get(c).first.second-circle_list.get(b).first.second)))/
                    (circle_list.get(c).first.first-circle_list.get(b).first.first)) + circle_list.get(b).first.first + circle_list.get(c).first.first;
            double y1 =
        }
    }

    //what I want to draw is here
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(mCanvas);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        Paint textPaint = new Paint();
        textPaint.setTextSize(20);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        Paint circle = new Paint();
        circle.setColor(Color.BLUE);
        circle.setStyle(Paint.Style.STROKE);
        circle.setAntiAlias(true);
        for(Pair<Pair<Integer, Integer>, Integer> item : circle_list) {
            canvas.drawCircle(item.first.first, item.first.second, radius, mPaint);
            canvas.drawText(String.valueOf(item.second), item.first.first, item.first.second, textPaint);
            canvas.drawCircle(item.first.first, item.first.second, radius * (-item.second / 5), circle);
        }
        if(circle_list.size() >= 3){
            Pair<Integer, Integer> router = findRoute();
        }
    }
}
