package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {
    // mapped orderId to Order
    private Map<String, Order> orderMap;

    // DeliveryPartner is created by id only,
//    List<DeliveryPartner> deliveryPartner;   // wrong

    // map partner with its object
    private Map<String, DeliveryPartner> deliveryPartnerMap;

    // mapped partnerId with all his orderIds
    private Map<String, List<String>> partnerOrderMap;

//    private Map<String, List<Order>> partnerOrderObjectMap;

    int countOfOrdersAssignedToAllPartners = 0;

    //for Optimization
    Map<String, String> orderAssignedToAPartner;

    public OrderRepository() {
        this.orderMap = new HashMap<String, Order>();
        this.deliveryPartnerMap = new HashMap<String, DeliveryPartner>();
        this.partnerOrderMap = new HashMap<String, List<String>>();
        this.orderAssignedToAPartner = new HashMap<String,String>();
    }


    public void addOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public void addPartner(String id) {
        deliveryPartnerMap.put(id, new DeliveryPartner(id));
    }


    // Assign an Order to a Partner
    public void addOrderPartnerPair(String orderId, String partnerId) {
        if(orderMap.containsKey(orderId) && deliveryPartnerMap.containsKey(partnerId)) {
            List<String> orderList = new ArrayList<>();
            if(partnerOrderMap.containsKey(partnerId)) {
                orderList = partnerOrderMap.get(partnerId);
                orderList.add(orderId);
                countOfOrdersAssignedToAllPartners++;
            }
            partnerOrderMap.put(partnerId, orderList);
            orderAssignedToAPartner.put(orderId, partnerId); //this order is assigned to this partner

            // now i have to increase the orders count also
            // ek order bad gaya to numberOfOrders me bhi 1 badana padega
            // is partnerId wale partner ke numberOfOrders badana padega
            DeliveryPartner deliveryPartner = deliveryPartnerMap.get(partnerId);
            deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders() + 1);
        }
    }

    public Order getOrderById(String orderId) {
        return orderMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartnerMap.get(partnerId);
    }

    //Get number of orders assigned to given partnerId
    public int getOrderCountByPartnerId(String partnerId) {
        return deliveryPartnerMap.get(partnerId).getNumberOfOrders();
    }

    //Get List of all orders assigned to given partnerId
    public List<String> getOrdersByPartnerId(String partnerId) {
        List<String> orderList = new ArrayList<>();
        if(partnerOrderMap.containsKey(partnerId)) {
            orderList = partnerOrderMap.get(partnerId);
        }
        return orderList;
    }


    //Get List of all orders in the system
    public List<String> getAllOrders() {              // check 2 --> for String instead of Order Object
        return new ArrayList<>(orderMap.keySet());
    }


    //Get count of orders which are not assigned to any partner
    public int getCountOfUnassignedOrders() {
//        int totalOrders = orderMap.size();              // check this approach
//        int assignedOrders = 0;
//
//        for (String orders : partnerOrderMap.keySet()) {
//            assignedOrders += partnerOrderMap.get(orders).size();
//        }
//
//        int unassignedOrders = totalOrders - assignedOrders;
//        return unassignedOrders;

//        int totalOrders = orderMap.size();
//        int unassignedOrders = totalOrders - orderAssignedToAPartner.size();
//        return unassignedOrders;
        int totalOrders = orderMap.size();
        int unassignedOrders = totalOrders - countOfOrdersAssignedToAllPartners;
        return unassignedOrders;
    }


    //Get count of orders which are left undelivered by partnerId after given time
    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        List<String> orders = new ArrayList<>();
        if(partnerOrderMap.containsKey(partnerId))
            orders = partnerOrderMap.get(partnerId);
        // now i have all the order id's of partnerId

        //Note : time is in HH:MM format, so we will first convert it into int
        String[] arr = time.split(":");
        int hour = Integer.parseInt(arr[0]);
        int min = Integer.parseInt(arr[1]);
        int deliveryTime = hour * 60 + min;

        int undeliveredOrdersByPartnerId = 0;
        for (String orderId : orders) {
            int orderTime = orderMap.get(orderId).getDeliveryTime();
            if(orderTime > deliveryTime) undeliveredOrdersByPartnerId++;
        }
        return undeliveredOrdersByPartnerId;
    }


    //Get the time at which the last delivery is made by given partner
    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        List<String> orders = new ArrayList<>();
        if(partnerOrderMap.containsKey(partnerId))
            orders = partnerOrderMap.get(partnerId);

        String lastOrderId = orders.get(orders.size()-1);
        int time = orderMap.get(lastOrderId).getDeliveryTime();

        // formula : to get hours divide time by 60
        //           to get minutes take modulo of time with 60

        int hour = time/60;
        int min = time%60;

        String hourString = String.valueOf(hour);
        String minString = String.valueOf(min);
        if(hourString.length() == 1){
            hourString = "0" + hourString;
        }
        if(minString.length() == 1){
            minString = "0" + minString;
        }

        return hourString + ":" + minString;
    }


    //Delete a partner and the corresponding orders should be unassigned
    public void deletePartnerById(String partnerId) {
        if(deliveryPartnerMap.containsKey(partnerId))
            deliveryPartnerMap.remove(partnerId);
        if(partnerOrderMap.containsKey(partnerId)) {
            partnerOrderMap.get(partnerId).clear();
            partnerOrderMap.remove(partnerId);
        }
    }


    //Delete an order and the corresponding partner should be unassigned
    public void deleteOrderById(String orderId) {
        if(orderMap.containsKey(orderId))
            orderMap.remove(orderId);

        //O(N*N) Approach
//        for (String partnerId : partnerOrderMap.keySet()) {
//            List<String> ordersList = partnerOrderMap.get(partnerId);
//            for (String order : ordersList) {
//                if(order.equals(orderId)) {
//                    ordersList.remove(orderId);
//                    partnerOrderMap.put(partnerId, ordersList);
//                    DeliveryPartner deliveryPartner = deliveryPartnerMap.get(partnerId);
//                    deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders() - 1);
//                    return;
//                }
//            }
//        }

        //Optimized O(N)
        String partnerId = orderAssignedToAPartner.get(orderId); //this is a partner of this order
        List<String> ordersList = partnerOrderMap.get(partnerId);
            for (String order : ordersList) {
                if(order.equals(orderId)) {
                    ordersList.remove(orderId);
                    partnerOrderMap.put(partnerId, ordersList);
                    DeliveryPartner deliveryPartner = deliveryPartnerMap.get(partnerId);
                    deliveryPartner.setNumberOfOrders(deliveryPartner.getNumberOfOrders() - 1);
                    return;
                }
            }

    }

}
