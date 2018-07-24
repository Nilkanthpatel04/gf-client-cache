package com.sia.gemfire.client.impl;

import com.sia.gemfire.client.GFCache;
import com.sia.gemfire.client.config.GFCacheConfig;
import com.sia.gemfire.client.object.PassengerItinerary;
import com.sia.gemfire.client.util.GFCacheUtils;
import com.sia.gemfire.function.MealSsrExecuteOnServerFunction;
import org.apache.geode.cache.execute.FunctionService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class GFCacheImpl implements GFCache {
    @Override
    public List<PassengerItinerary> processRequest(String passengerId, boolean isKF) {
        return _processRequest(passengerId, isKF, null, null, null, null);
    }

    @Override
    public List<PassengerItinerary> processRequest(String passengerId,  boolean isKF, Timestamp departureDate) {
        return _processRequest(passengerId, isKF, departureDate, null, null, null);
    }

    @Override
    public List<PassengerItinerary> processRequest(String passengerId, boolean isKF, Timestamp departureDate, String cabinClass) {
        return _processRequest(passengerId, isKF, departureDate, cabinClass, null, null);
    }

    @Override
    public List<PassengerItinerary> processRequest(String passengerId, boolean isKF, Timestamp departureDate, String cabinClass, String carrierCode) {
        return _processRequest(passengerId, isKF, departureDate, cabinClass, carrierCode, null);
    }

    @Override
    public List<PassengerItinerary> processRequest(String passengerId, boolean isKF, Timestamp departureDate, String cabinClass, String carrierCode, String channel) {
        return _processRequest(passengerId, isKF, departureDate, cabinClass, carrierCode, channel);
    }

    @Override
    public void closeCache() {
        GFCacheProvider.closeCache();
    }

    private List<PassengerItinerary> _processRequest(String passengerId, boolean isKF, Timestamp departureDate, String cabinClass, String carrierCode, String channel){
        /*
        ClientCache cache = new ClientCacheFactory()
                .addPoolLocator("192.168.43.180", 10334).set("log-level", "WARN").create();

        final String regionName = "passenger";

        // create a local region that matches the server region

        Region<Object, Object> region = cache.<Object, Object>createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY)
                .create(regionName);
        */
        /*List<Object> requestParams = new ArrayList<>();
        requestParams.add(passengerId);
        requestParams.add(isKF);
        requestParams.add(departureDate);
        requestParams.add(cabinClass);
        requestParams.add(carrierCode);
        requestParams.add(channel);*/

        Object[] requestParams = new Object[]{passengerId, isKF, departureDate, cabinClass, carrierCode, channel};

        List<Object> output =
                (List<Object>) FunctionService.onServer(GFCacheUtils.getRegion(GFCacheConfig.PASSENGER_REGION)
                        .getRegionService()).setArguments(requestParams)
                        .execute(new MealSsrExecuteOnServerFunction()).getResult();

        /*
        List<Object> output =
                (List<Object>) FunctionService.onRegion(region).setArguments(regionName)
                        .execute(new MeanSSRFunctionWithTwoOQL()).getResult();
        */

        List<PassengerItinerary> response = new ArrayList<>();
        for (Object itinObject :  output) {
            if(itinObject instanceof List){
                System.out.println("LOG FE result is List");
                List<PassengerItinerary> itinList = (List<PassengerItinerary>)itinObject;
                for(PassengerItinerary itinEntry : itinList){
                  response.add(itinEntry);
                }
            }else if (itinObject instanceof PassengerItinerary) {
                System.out.println("LOG FE result is of type PassengerItinerary");
                response.add((PassengerItinerary)itinObject);
            }else {
                System.out.println("Error parsing the FE result");
                //Error
            }

            System.out.println("Result => "+ response.toString());
        }
        return response;
    }
}
