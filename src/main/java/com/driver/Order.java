package com.driver;

import java.sql.Time;
import java.util.TimeZone;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        this.id = id;

        // given that each order has its delivery time in HH:MM format
        String[] arr = deliveryTime.split(":");
        int hour = Integer.parseInt(arr[0]);
        int minute = Integer.parseInt(arr[1]);
        int actualDeliveryTime = hour * 60 + minute;
        this.deliveryTime = actualDeliveryTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {    // HH:MM
        String[] arr = deliveryTime.split(":");
        int hour = Integer.parseInt(arr[0]);
        int minute = Integer.parseInt(arr[1]);
        int actualDeliveryTime = hour * 60 + minute;
        this.deliveryTime = actualDeliveryTime;
    }
}
