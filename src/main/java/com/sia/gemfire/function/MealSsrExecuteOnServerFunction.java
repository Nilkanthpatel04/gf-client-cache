package com.sia.gemfire.function;

import com.sia.gemfire.client.object.PassengerItinerary;
import com.sia.gemfire.client.object.PassengerSSR;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.query.*;
import org.apache.geode.cache.query.internal.StructImpl;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.PdxInstance;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MealSsrExecuteOnServerFunction implements Function, Serializable {

    private static final org.apache.logging.log4j.Logger logger = LogService.getLogger();

    private static final long serialVersionUID = -3150906692853575839L;


    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public void execute(FunctionContext fc) {

        Cache cache = CacheFactory.getAnyInstance();

        /*
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
        */

        Object[] requestParams = (Object[])fc.getArguments();
        //List<Object> requestParams = new ArrayList<>();
        final String passengerId = (String)requestParams[0];
        final boolean isKF = (boolean)requestParams[1];
        final Timestamp departureDate = (Timestamp) requestParams[2];
        final String cabinClass = (String)requestParams[3];
        final String carrierCode = (String) requestParams[4];
        final String channel = (String) requestParams[5];

        logger.info("TEST Request Params ["+ passengerId + ", " + isKF + ", " + departureDate + ", " + cabinClass + ", " + carrierCode + ", " + channel + "]");

        String commonFilter =  "((src_stm_id=12 and ssr_tp like '%ML') OR (src_stm_id = 43 and ssr_supr_type in set('2','4')))";
        String query1 = null;
        //RUID case
        if(isKF == false) {
            query1 = "select  b.acc_nbr, b.sk_free_txt, b.cst_nbr, b.pnr_loc, b.pnr_crtn_dt, b.tattoo_nbr, " +
                    "b.seg_tattoo_nbr,  b.flt_nbr ,   b.dep_dt ,   b.bd_pnt ,   b.off_pnt, " +
                    "b.cb_cls " +
                    "from   /passenger b  where b.sk_keyword= 'RUID'" + " AND " +
                    "b.sk_free_txt = " + "'" + passengerId + "'";
        }else {
            //KF query
            query1 = "select  b.acc_nbr, b.sk_free_txt, b.cst_nbr, b.pnr_loc, b.pnr_crtn_dt, b.tattoo_nbr, " +
                    "b.seg_tattoo_nbr,  b.flt_nbr ,   b.dep_dt ,   b.bd_pnt ,   b.off_pnt, " +
                    "b.cb_cls " +
                    "from   /passenger b  where " +
                    "b.acc_nbr = " + "'" + passengerId + "'";
        }

        if(departureDate != null){
            query1 = query1 + " AND " + "dep_dt >=" +  "'" + departureDate + "'";
        }

        if(cabinClass != null){
           query1 = query1 + " AND " + "cb_cls = " + "'" + cabinClass + "'";
        }

        if(carrierCode != null){
            query1 = query1 + " AND " + "cr_code = " + "'" + carrierCode + "'";
        }

        query1 = query1 + " AND " + commonFilter;
        logger.info("NNN generated OQL for /passenger => " + query1);

        List<PassengerItinerary> pItinList = retrievePaxItins(query1, cache, fc);

        pItinList = updatePaxItinsWithSSRDetails(pItinList, cache, fc);

        fc.getResultSender().lastResult(pItinList);

    }

    private List<PassengerItinerary> updatePaxItinsWithSSRDetails(List<PassengerItinerary> pItinList, Cache cache, FunctionContext fc) {
        for(PassengerItinerary pItin : pItinList){
            List<PassengerSSR> ssrList = retrieveSSRListForItin(pItin, cache, fc);

            //retrieve the SSR details for ITIN and set it in Itinery.
            pItin.setPassengerSSR(ssrList);

        }
        return pItinList;
    }

    private List<PassengerSSR> retrieveSSRListForItin(PassengerItinerary pItin, Cache cache, FunctionContext fc) {

        logger.info("Filter params on passengerSSR = > [" + pItin.getPnrLoc()
                + ",  " + pItin.getPnrCrtnDt()
                + ",  " + pItin.getTattooNbr()
                + ",  " + pItin.getSegTattooNbr());

        String pnrLoc = pItin.getPnrLoc();
        Timestamp pnrCrtnDt = Timestamp.valueOf(pItin.getPnrCrtnDt());
        Integer tattooNbr = Integer.parseInt(pItin.getTattooNbr());
        Integer segTattooNbr = Integer.parseInt(pItin.getSegTattooNbr());

        String oql = null;
        if(tattooNbr == null && segTattooNbr == null){
            oql = "select * from /passengerSSR c where " +
                    "c.pnr_loc = " + "'" + pnrLoc + "'" + " AND " +
                    "c.pnr_crtn_dt=" + "to_date('" + pnrCrtnDt + "','yyyy-MM-dd HH:mm:ss.SSS')";
        }else if(segTattooNbr == null){
            oql = "select * from /passengerSSR c where " +
                    "c.pnr_loc = " + "'" + pnrLoc +  "'" + " AND " +
                    "c.pnr_crtn_dt=" + "to_date('" + pnrCrtnDt + "','yyyy-MM-dd HH:mm:ss.SSS')" + " AND " +
                    "c.tattoo_nbr=" + tattooNbr ;
        }else if (tattooNbr == null){
            oql = "select * from /passengerSSR c where " +
                    "c.pnr_loc = " +  "'" + pnrLoc + "'" + " AND " +
                    "c.pnr_crtn_dt=" + "to_date('" + pnrCrtnDt + "','yyyy-MM-dd HH:mm:ss.SSS')" + " AND " +
                    "c.seg_tattoo_nbr=" + segTattooNbr ;
        }else {
            oql = "select * from /passengerSSR c where " +
                    "c.pnr_loc = " + "'" +  pnrLoc + "'" + " AND " +
                    "c.pnr_crtn_dt=" + "to_date('" + pnrCrtnDt + "','yyyy-MM-dd HH:mm:ss.SSS')" + " AND " +
                    "c.tattoo_nbr=" + tattooNbr + " AND " +
                    "c.seg_tattoo_nbr=" + segTattooNbr ;
        }

        logger.info("OQL on passengerSSR = > " + oql);
        // Get QueryService from Cache.
        QueryService queryService = cache.getQueryService();

        // Create the Query Object.
        Query query = queryService.newQuery(oql);

        // Execute Query locally. Returns results set.
        SelectResults results = null;
        //OutputData output = new OutputData();
        //List<PassengerItinerary> itineraryList = output.getPassengerItinerary();
        List<PassengerSSR> ssrList = new ArrayList<>();

        try {
            //results = (SelectResults) query.execute();
            //If function is executed on region, context is RegionFunctionContext
            //RegionFunctionContext rContext = (RegionFunctionContext) fc;

            results = (SelectResults) query.execute(/*rContext*/);

            // Find the Size of the ResultSet.
            int size = results.size();

            logger.info("NNNN SSRResult Size = " + size);
            Iterator ssrIterator = results.iterator();

            while (ssrIterator.hasNext()) {
                Object value = ssrIterator.next();

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
                }
                else{
                    PdxInstance instance = (PdxInstance) value;

                    passengerSSR.setSsrTp(String.valueOf(instance.getField("ssr_tp")));
                    passengerSSR.setSsrSt(String.valueOf(instance.getField("ssr_st")));
                    passengerSSR.setSsrFreeTxt(String.valueOf(instance.getField("ssr_free_txt")));
                    passengerSSR.setSsrDescription(String.valueOf(instance.getField("ssr_description")));
                    passengerSSR.setSrcStmId(String.valueOf(instance.getField("src_stm_id")));
                    passengerSSR.setOtTattoNbr(String.valueOf(instance.getField("ot_tattoo_nbr")));
                    passengerSSR.setLegNo(String.valueOf(instance.getField("leg_no")));
                    passengerSSR.setLegBdPnt(String.valueOf(instance.getField("leg_bd_pnt")));
                    passengerSSR.setLegOffPnt(String.valueOf(instance.getField("leg_off_pnt")));
                    passengerSSR.setSsrNbr(String.valueOf(instance.getField("ssr_number")));
                    passengerSSR.setSsrCode(String.valueOf(instance.getField("ssr_code")));

                    logger.info("NNNN MealSSR Value is PdxInstance ");
                }
                ssrList.add(passengerSSR);
            }
        } catch (TypeMismatchException e) {
            e.printStackTrace();
        } catch (FunctionDomainException e) {
            e.printStackTrace();
        } catch (QueryInvocationTargetException e) {
            e.printStackTrace();
        } catch (NameResolutionException e) {
            e.printStackTrace();
        }
        return ssrList;
    }

    private List<PassengerItinerary> retrievePaxItins(String query1, Cache cache, FunctionContext fc){
        // Get QueryService from Cache.
        QueryService queryService = cache.getQueryService();

        // Create the Query Object.
        Query query = queryService.newQuery(query1);

        // Execute Query locally. Returns results set.
        SelectResults results = null;
        List<PassengerItinerary> itineraryList = new ArrayList<>();

        try {
            //results = (SelectResults) query.execute();
            //If function is executed on region, context is RegionFunctionContext
            //RegionFunctionContext rContext = (RegionFunctionContext)fc;

            results = (SelectResults) query.execute();

            // Find the Size of the ResultSet.
            int size = results.size();

            logger.info("NNNN Result Size = " + size);
            Iterator pItinIterator = results.iterator();


            while (pItinIterator.hasNext()) {
                Object value = pItinIterator.next();
                //Customer cust = new Customer();
                PassengerItinerary passengerItin = new PassengerItinerary();
                ////PassengerSSR passengerSSR = new PassengerSSR();

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
                }
                else{
                    PdxInstance instance = (PdxInstance) value;
                    logger.info("NNNN Unexpected Case -Error Value is PdxInstance ");
                }
                itineraryList.add(passengerItin);
            }

            logger.info("NNNN Total Passenger Itin fetched =  " + itineraryList.size());
        } catch (FunctionDomainException e1) {
            e1.printStackTrace();
        } catch (TypeMismatchException e2) {
            e2.printStackTrace();
        } catch (NameResolutionException e3) {
            e3.printStackTrace();
        } catch (QueryInvocationTargetException e4) {
            e4.printStackTrace();
        }

        return itineraryList;
    }

    @Override
    public String getId() {
        return "MealSsrExecuteOnServerFunction";
    }

    @Override
    public boolean optimizeForWrite() {
        return true;
    }

    @Override
    public boolean isHA() {
        return false;
    }

    /*
    @Override
    public Collection<ResourcePermission> getRequiredPermissions(String regionName) {
        return null;
    }
    */
}
