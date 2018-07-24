package com.sia.gemfire;

import com.sia.gemfire.client.GFCache;
import com.sia.gemfire.client.impl.GFCacheImpl;
import com.sia.gemfire.client.object.PassengerItinerary;

import java.util.List;

public class GFCacheTest {
    public static void main(String[] args){
        GFCache gfCache = new GFCacheImpl();

        System.out.println("------------ TEST-1 ---------------");
        List<PassengerItinerary> itineraryList_1 = gfCache.processRequest("8985434342", true);

        System.out.println("NNN response = " + itineraryList_1);

        List<PassengerItinerary> itineraryList_2 = gfCache.processRequest("898500000", true);

        System.out.println("NNN response = " + itineraryList_2);


        gfCache.closeCache();
    }
}
