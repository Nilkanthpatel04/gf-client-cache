package com.sia.gemfire;

import com.sia.gemfire.client.GFCache;
import com.sia.gemfire.client.impl.GFCacheImpl;
import com.sia.gemfire.client.object.PassengerItinerary;

import java.sql.Timestamp;
import java.util.List;

public class GFCacheTest {
    public static void main(String[] args){
        GFCache gfCache = new GFCacheImpl();

        System.out.println("------------ TEST-1 ---------------");
        List<PassengerItinerary> itineraryList_1 = gfCache.processRequest("8985434342", true);

        System.out.println("NNN response = " + itineraryList_1);

        List<PassengerItinerary> itineraryList_2 = gfCache.processRequest("898500000", true);
        System.out.println("NNN response = " + itineraryList_2);

        //Additional test-cases for all possible queries.

        Timestamp departureDate = Timestamp.valueOf("2018-02-30 04:31:59");
        List<PassengerItinerary> itinList1 =  gfCache.processRequest("passengerId", true, departureDate);

        List<PassengerItinerary> itinList2 =gfCache.processRequest("passengerId", true, departureDate, "cabinClass");


        List<PassengerItinerary> itinList3 =gfCache.processRequest("passengerId", true, departureDate, "cabinClass", "carrierCode");


        List<PassengerItinerary> itinList4 = gfCache.processRequest("passengerId", true, departureDate, "cabinClass", "carrierCode", "channel");

        //RU test-cases
        List<PassengerItinerary> itinList5 =  gfCache.processRequest("passengerId", false, departureDate);

        List<PassengerItinerary> itinList6 =gfCache.processRequest("passengerId", false, departureDate, "cabinClass");


        List<PassengerItinerary> itinList7 =gfCache.processRequest("passengerId", false, departureDate, "cabinClass", "carrierCode");


        List<PassengerItinerary> itinList8 = gfCache.processRequest("passengerId", false, departureDate, "cabinClass", "carrierCode", "channel");

        gfCache.closeCache();
    }
}
