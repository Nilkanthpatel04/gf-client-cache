package com.sia.gemfire.function;

import com.sia.gemfire.client.object.PassengerItinerary;
import com.sia.gemfire.client.object.PassengerSSR;
import com.sia.gemfire.client.util.ItineraryIndex;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.*;
import org.apache.geode.cache.query.internal.StructImpl;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.PdxInstance;

import java.io.Serializable;
import java.util.*;

/**
 * Execute onRegion co-located join for passenger and passengerSSR regions
 * Single query doing co-located join.
 */
public class MealSsrExecuteOnRegionFunctionV1 implements Function, Serializable {

    private static final org.apache.logging.log4j.Logger logger = LogService.getLogger();

    private static final long serialVersionUID = -3150906692851375834L;

    public void execute(FunctionContext fc) {
        Cache cache = CacheFactory.getAnyInstance();

        String queryString = "select  b.acc_nbr, b.sk_free_txt, b.cst_nbr, b.pnr_loc, b.pnr_crtn_dt, b.tattoo_nbr, " +
                "b.seg_tattoo_nbr,  b.flt_nbr ,   b.dep_dt ,   b.bd_pnt ,   b.off_pnt, " +
                "b.cb_cls,   c.ssr_tp,   c.ssr_st,   c.ssr_free_txt,   c.ssr_description,   c.src_stm_id, " +
                "c.ot_tattoo_nbr,   c.leg_no,   c.leg_bd_pnt,   c.leg_off_pnt,   c.ssr_number,   c.ssr_code " +
                "from   /passenger b,   /passengerSSR c  where " +
                "b.pnr_loc = c.pnr_loc   and " +
                "b.pnr_crtn_dt = c.pnr_crtn_dt and " +
                "b.tattoo_nbr = c.tattoo_nbr and " +
                "b.seg_tattoo_nbr=c.seg_tattoo_nbr and " +
                "b.acc_nbr = '8985434342'";

        // Get QueryService from Cache.
        QueryService queryService = cache.getQueryService();

        // Create the Query Object.
        Query query = queryService.newQuery(queryString);

        // Execute Query locally. Returns results set.
        SelectResults results = null;
        List<PassengerItinerary> itineraryList = new ArrayList<>();

        try {
            //results = (SelectResults) query.execute();
            //If function is executed on region, context is RegionFunctionContext
            RegionFunctionContext rContext = (RegionFunctionContext)fc;

            results = (SelectResults) query.execute(rContext);

            // Find the Size of the ResultSet.
            int size = results.size();

            logger.info("NNNN Result Size = " + size);
            Iterator meals = results.iterator();

            //Create Index
            Map<ItineraryIndex, PassengerItinerary> itinIndex = new HashMap<ItineraryIndex, PassengerItinerary>();

            while (meals.hasNext()) {
                Object value = meals.next();
                PassengerItinerary passengerItin = new PassengerItinerary();
                PassengerSSR passengerSSR = new PassengerSSR();

                if(value instanceof StructImpl){

                    StructImpl struct = (StructImpl)value;
                    logger.info("NNNN struct size = " + struct.getFieldNames().length);

                    int i =1;
                    logger.info("NNNN Iteration -" + i);
                    for (String field : struct.getFieldNames()) {
                        logger.info("NNNN FieldName [Name, value]- "+ i + " ===> [ " + field + ", " + struct.get(field) + "]" );
                        //logger.info("NNNN Field ValueType - " + i + "===>" + struct.get(field).getClass().getName());
                        i++;
                    }

                    //create index
                    ItineraryIndex index = new ItineraryIndex(
                            String.valueOf(struct.get("pnr_loc")),
                            String.valueOf(struct.get("pnr_crtn_dt")),
                            String.valueOf(struct.get("tattoo_nbr")),
                            String.valueOf(struct.get("seg_tattoo_nbr")));

                    //create the passengerSSR.
                    passengerSSR.setSsrTp(String.valueOf(struct.get("ssr_tp")));
                    passengerSSR.setSsrSt(String.valueOf(struct.get("ssr_st")));
                    passengerSSR.setSsrFreeTxt(String.valueOf(struct.get("ssr_free_txt")));
                    passengerSSR.setSsrDescription(String.valueOf(struct.get("ssr_description")));
                    passengerSSR.setSrcStmId(String.valueOf(struct.get("src_stm_id")));
                    passengerSSR.setOtTattoNbr(String.valueOf(struct.get("ot_tattoo_nbr")));
                    passengerSSR.setLegNo(String.valueOf(struct.get("leg_no")));
                    passengerSSR.setLegBdPnt(String.valueOf(struct.get("leg_bd_pnt")));
                    passengerSSR.setLegOffPnt(String.valueOf(struct.get("leg_off_pnt")));
                    passengerSSR.setSsrNbr(String.valueOf(struct.get("ssr_number")));
                    passengerSSR.setSsrCode(String.valueOf(struct.get("ssr_code")));


                    //Create PaxItin
                    passengerItin.setPnrLoc(String.valueOf(struct.get("pnr_loc")));
                    passengerItin.setPnrCrtnDt(String.valueOf(struct.get("pnr_crtn_dt")));
                    passengerItin.setTattooNbr(String.valueOf(struct.get("tattoo_nbr")));
                    passengerItin.setSegTattooNbr(String.valueOf(struct.get("seg_tattoo_nbr")));
                    passengerItin.setFltNbr(String.valueOf(struct.get("flt_nbr")));
                    passengerItin.setDepDt(String.valueOf(struct.get("dep_dt")));
                    passengerItin.setBdPt(String.valueOf(struct.get("bd_pnt")));
                    passengerItin.setOffPnt(String.valueOf(struct.get("off_pnt")));
                    passengerItin.setCbCls(String.valueOf(struct.get("cb_cls")));
                    passengerItin.addPassengerSSR(passengerSSR);

                    if(itinIndex.containsKey(index)){
                        PassengerItinerary itin = itinIndex.get(index);
                        itin.addPassengerSSR(passengerSSR);
                    }else {
                        //itineraryList.add(passengerItin);
                        itinIndex.put(index, passengerItin);
                    }
                }
                else{
                    PdxInstance instance = (PdxInstance) value;
                    logger.info("Unexpected condition value is PdxInstance..!");
                }
            }

            //Populate List<Itin> from the Map
            itineraryList.addAll(itinIndex.values());

        } catch (FunctionDomainException e1) {
            e1.printStackTrace();
        } catch (TypeMismatchException e2) {
            e2.printStackTrace();
        } catch (NameResolutionException e3) {
            e3.printStackTrace();
        } catch (QueryInvocationTargetException e4) {
            e4.printStackTrace();
        }

        fc.getResultSender().lastResult(itineraryList);
    }


    public boolean optimizeForWrite() {
        return true;
    }

    public boolean isHA() {
        return false;
    }

    public boolean hasResult() { return true; }

    public String getId() { return "MealSsrExecuteOnRegionFunctionV1"; }
}
