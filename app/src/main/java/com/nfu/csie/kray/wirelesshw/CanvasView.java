package com.nfu.csie.kray.wirelesshw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CanvasView extends View {
    private static final String TAG = "wifi";
    public Paint mPaint;
    public static Canvas mCanvas;
    private ArrayList<WireLessList> wireLessList;
    private final int size = 40;

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        wireLessList = new ArrayList<>();
    }

    public void drawCircle(int x, int y, int level) {
        int d0 = 20;
        int n = 2;
        int d0_radius = 10 * n;
        double radius = d0_radius + 10 * n * Math.log10(level/d0);
        wireLessList.add(new WireLessList(x, y, level, radius));
        //important. Refreshes the view by calling onDraw function
        invalidate();
    }

    public void clearCircle(){
        wireLessList = new ArrayList<>();
        invalidate();
    }

    public ArrayList<WireLessList> getCircle_list(){
        return wireLessList;
    }

    public Pair<Integer, Integer> findRoute(){
        int length = (int)(wireLessList.size()/3);
        ArrayList<Pair<Integer, Integer>> routers = new ArrayList<>();
        for(int i=0; i<length; ++i){
            int a = 3*i, b = 3*i+1, c = 3*i+2;
            // (r_1^2 (-y_2) + r_1^2 y_3 + r_3^2 (y_2 - y_1) + r_2^2 (y_1 - y_3) - x_2^2 y_1 + x_3^2 y_1 + x_1^2 y_2 - x_3^2 y_2 - x_1^2 y_3 + x_2^2 y_3 - y_1 y_2^2 + y_1 y_3^2 - y_2 y_3^2 + y_1^2 y_2 - y_1^2 y_3 + y_2^2 y_3)/(2 (x_3 (y_1 - y_2) + x_1 (y_2 - y_3) + x_2 (y_3 - y_1)))
            int x = 0.5 * ()
        }
        return new Pair<>(0, 0);
    }

    //what I want to draw is here
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(mCanvas);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        @SuppressLint("DrawAllocation") Paint textPaint = new Paint();
        textPaint.setTextSize(20);
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        @SuppressLint("DrawAllocation") Paint circle = new Paint();
        circle.setColor(Color.parseColor("#2A9D8F"));
        circle.setStyle(Paint.Style.STROKE);
        circle.setAntiAlias(true);
        for(WireLessList item : wireLessList) {
            Log.i(TAG, "level: " + item.level + ", radius: " + item.radius);
            canvas.drawCircle(item.x, item.y, size, mPaint);
            canvas.drawText(String.valueOf(item.level), item.x, item.y, textPaint);
            canvas.drawCircle(item.x, item.y, (float) item.radius * 10, circle);
        }
        if(wireLessList.size() >= 3){
            Pair<Integer, Integer> router = findRoute();
            @SuppressLint("DrawAllocation") Paint rect_paint = new Paint();
            rect_paint.setColor(Color.parseColor("#E9C46A"));
            rect_paint.setStyle(Paint.Style.FILL_AND_STROKE);
            rect_paint.setAntiAlias(true);
            @SuppressLint("DrawAllocation") Rect rect = new Rect(router.first-size/2, router.second-size/2, size/2+router.first, size/2+router.second);
            canvas.drawRect(rect, rect_paint);
        }
    }
}
