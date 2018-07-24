package com.sia.gemfire.function;

import com.sia.gemfire.client.object.PassengerItinerary;
import com.sia.gemfire.client.object.PassengerSSR;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Execute 0nRegion 2 oqls sequentially on /passenger and /passengerSSR regions respectively.
 * First get the Itin details from /passenger region and then
 *
 */
public class MealSsrExecuteOnRegionFunctionV2 implements Function, Serializable {

    private static final org.apache.logging.log4j.Logger logger = LogService.getLogger();

    private static final long serialVersionUID = -3150906692851875839L;


    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public void execute(FunctionContext fc) {

        Cache cache = CacheFactory.getAnyInstance();

        String query1 = "select  b.acc_nbr, b.sk_free_txt, b.cst_nbr, b.pnr_loc, b.pnr_crtn_dt, b.tattoo_nbr, " +
                "b.seg_tattoo_nbr,  b.flt_nbr ,   b.dep_dt ,   b.bd_pnt ,   b.off_pnt, " +
                "b.cb_cls "+
                "from   /passenger b  where " +
                "b.acc_nbr = '8985434342'";

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
            RegionFunctionContext rContext = (RegionFunctionContext) fc;

            results = (SelectResults) query.execute(rContext);

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
            RegionFunctionContext rContext = (RegionFunctionContext)fc;

            results = (SelectResults) query.execute(rContext);

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
        return "MealSsrExecuteOnRegionFunctionV2";
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
