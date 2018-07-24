package com.sia.gemfire.client;

import com.sia.gemfire.client.object.PassengerItinerary;

import java.sql.Timestamp;
import java.util.List;

public interface GFCache {
    /**
     *
     * @param passengerId kfNumber or RUID of user.
     * @param isKF flag to determine whether a KF (set true) or RU (set false).
     * @return List<PassengerItinerary> Itinerary list for the specified passenger.
     */
    List<PassengerItinerary> processRequest(String passengerId, boolean isKF);

    /**
     *
     * @param passengerId kfNumber or RUID of user.
     * @param isKF flag to determine whether a KF (set true) or RU (set false).
     * @param departureDate filter param to retrieve selected records.
     * @return List<PassengerItinerary> Itinerary list for the specified passenger
     */
    List<PassengerItinerary> processRequest(String passengerId, boolean isKF, Timestamp departureDate);

    /**
     *
     * @param passengerId kfNumber or RUID of user.
     * @param isKF flag to determine whether a KF (set true) or RU (set false).
     * @param departureDate filter param to retrieve selected records.
     * @param cabinClass cabin class specified in the request.
     * @return List<PassengerItinerary> Itinerary list for the specified passenger
     */
    List<PassengerItinerary> processRequest(String passengerId, boolean isKF, Timestamp departureDate, String cabinClass);

    /**
     *
     * @param passengerId kfNumber or RUID of user.
     * @param isKF flag to determine whether a KF (set true) or RU (set false).
     * @param departureDate filter param to retrieve selected records.
     * @param cabinClass cabin class specified in the request.
     * @param carrierCode carrier code specified in the request.
     * @return List<PassengerItinerary> Itinerary list for the specified passenger
     */
    List<PassengerItinerary> processRequest(String passengerId, boolean isKF, Timestamp departureDate, String cabinClass, String carrierCode);

    /**
     *
     * @param passengerId kfNumber or RUID of user.
     * @param isKF flag to determine whether a KF (set true) or RU (set false).
     * @param departureDate filter param to retrieve selected records.
     * @param cabinClass cabin class specified in the request.
     * @param carrierCode carrier code specified in the request.
     * @param channel
     * @return List<PassengerItinerary> Itinerary list for the specified passenger
     */
    List<PassengerItinerary> processRequest(String passengerId, boolean isKF, Timestamp departureDate, String cabinClass, String carrierCode, String channel);

    /**
     * close gemfire client cache. Call this when you exist from application.
     */
    void closeCache();

}
