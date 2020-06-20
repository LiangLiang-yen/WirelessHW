package com.nfu.csie.kray.wirelesshw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.net.wifi.WifiInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CanvasView extends View {
    private static final String TAG = "wifi";
    public Paint mPaint;
    public static Canvas mCanvas;
    private Map<String, ArrayList<WireLessList>> wireLessList;
    private Map<String, String> AP_SSID_MAP;
    private final int size = 20;
    public String currentMAC = "";

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        wireLessList = new HashMap<>();
        AP_SSID_MAP = new HashMap<>();
    }

    public void drawCircle(int x, int y, ArrayList<WifiList> APlist) {
        int d0 = 20;
        int n = 2;
        int d0_radius = 10 * n;
        for (WifiList item : APlist) {
            double radius = d0_radius + 10 * n * Math.log10(item.level / d0);
            if (!wireLessList.containsKey(item.MacAddr))
                wireLessList.put(item.MacAddr, new ArrayList<WireLessList>());
            wireLessList.get(item.MacAddr).add(new WireLessList(x, y, item.level, radius));

            if(!AP_SSID_MAP.containsKey(item.MacAddr))
                AP_SSID_MAP.put(item.MacAddr, item.SSID);
        }
        //important. Refreshes the view by calling onDraw function
        invalidate();
    }

    public void updateList(ArrayList<WifiList> APlist){
        for (WifiList item : APlist) {
            if(!AP_SSID_MAP.containsKey(item.MacAddr))
                AP_SSID_MAP.put(item.MacAddr, item.SSID);
        }
        if(currentMAC.equals("")){
            Set ketSet = AP_SSID_MAP.keySet();
            Iterator it = ketSet.iterator();
            if (it.hasNext())
                currentMAC = it.next().toString();
        }
    }

    public void clearCircle() {
        wireLessList.clear();
        invalidate();
    }

    public void changePosition() {
        Set ketSet = AP_SSID_MAP.keySet();
        Iterator it = ketSet.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if(key.equals(currentMAC)){
                if(it.hasNext())
                    currentMAC = it.next().toString();
                else
                    currentMAC = ketSet.iterator().next().toString();
                break;
            }
        }
        invalidate();
    }

    public String getSSID(){
        if(AP_SSID_MAP.containsKey(currentMAC))
            return AP_SSID_MAP.get(currentMAC);
        else
            return "";
    }

    public Pair<Integer, Integer> findRoute() {
        int length = (int) (wireLessList.size() / 3);
        ArrayList<Pair<Integer, Integer>> routers = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            int a = 3 * i, b = 3 * i + 1, c = 3 * i + 2;
            // (r_1^2 (-y_2) + r_1^2 y_3 + r_3^2 (y_2 - y_1) + r_2^2 (y_1 - y_3) - x_2^2 y_1 + x_3^2 y_1 + x_1^2 y_2 - x_3^2 y_2 - x_1^2 y_3 + x_2^2 y_3 - y_1 y_2^2 + y_1 y_3^2 - y_2 y_3^2 + y_1^2 y_2 - y_1^2 y_3 + y_2^2 y_3)/(2 (x_3 (y_1 - y_2) + x_1 (y_2 - y_3) + x_2 (y_3 - y_1)))
            //int x = 0.5 * ()
        }
        return new Pair<>(0, 0);
    }

    //what I want to draw is here
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(mCanvas);
        mPaint.setColor(Color.parseColor("#E9C46A"));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        @SuppressLint("DrawAllocation") Paint textPaint = new Paint();
        textPaint.setTextSize(20);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        @SuppressLint("DrawAllocation") Paint circle = new Paint();
        circle.setColor(Color.parseColor("#2A9D8F"));
        circle.setStyle(Paint.Style.STROKE);
        circle.setAntiAlias(true);
        if(currentMAC.equals("")){
            Set ketSet = wireLessList.keySet();
            Iterator it = ketSet.iterator();
            if (it.hasNext())
                currentMAC = it.next().toString();
        }
        if(wireLessList.containsKey(currentMAC)) {
            for (WireLessList item : Objects.requireNonNull(wireLessList.get(currentMAC))) {
                Log.i(TAG, "level: " + item.level + ", radius: " + item.radius);
                canvas.drawCircle(item.x, item.y, size, mPaint);
                canvas.drawText(String.valueOf(item.level), item.x, item.y - size, textPaint);
            }
            if (Objects.requireNonNull(wireLessList.get(currentMAC)).size() >= 2) {
                @SuppressLint("DrawAllocation") Path path = new Path();
                for(WireLessList item : Objects.requireNonNull(wireLessList.get(currentMAC))){
//                    if(30 >= item.level)
//                        RadialGradient gradient = new RadialGradient()
//                    else if(item.level <= 45)
//                        mPaint.setShadowLayer(10, item.x, item.y, Color.YELLOW);
//                    else
//                        mPaint.setShadowLayer(10, item.x, item.y, Color.RED);
                    if (path.isEmpty()) {
                        Log.i(TAG, "onDraw: IsEmpty");
                        path.moveTo(item.x, item.y);
                    }
                    path.lineTo(item.x, item.y);
                }
                canvas.drawPath(path, mPaint);
            }
            if (Objects.requireNonNull(wireLessList.get(currentMAC)).size() >= 3) {
                Pair<Integer, Integer> router = findRoute();
                @SuppressLint("DrawAllocation") Paint rect_paint = new Paint();
                rect_paint.setColor(Color.parseColor("#E9C46A"));
                rect_paint.setStyle(Paint.Style.FILL_AND_STROKE);
                rect_paint.setAntiAlias(true);
                @SuppressLint("DrawAllocation") Rect rect = new Rect(router.first - size / 2, router.second - size / 2, size / 2 + router.first, size / 2 + router.second);
                canvas.drawRect(rect, rect_paint);
            }
        }
    }
}
