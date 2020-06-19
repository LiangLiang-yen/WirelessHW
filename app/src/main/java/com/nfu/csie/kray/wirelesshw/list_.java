package com.nfu.csie.kray.wirelesshw;

class WireLessList{
    int x;
    int y;
    int level;
    double radius;

    WireLessList(int x, int y, int level, double radius){
        this.x = x;
        this.y = y;
        this.level = level;
        this.radius = radius;
    }
}

class WifiList{
    String SSID;
    String MacAddr;
    int level;

    WifiList(String SSID, String MacAddr, int level){
        this.SSID = SSID;
        this.MacAddr = MacAddr;
        this.level = -level;
    }
}
