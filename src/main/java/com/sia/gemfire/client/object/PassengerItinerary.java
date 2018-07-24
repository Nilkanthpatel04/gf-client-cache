package com.sia.gemfire.client.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PassengerItinerary implements Serializable {
    private String pnrLoc;
    private String pnrCrtnDt;
    private String tattooNbr;
    private String segTattooNbr;
    private String fltNbr;
    private String depDt;
    private String bdPt;
    private String offPnt;

    @Override
    public String toString() {
        return "PassengerItinerary{" +
                "pnrLoc='" + pnrLoc + '\'' +
                ", pnrCrtnDt='" + pnrCrtnDt + '\'' +
                ", tattooNbr='" + tattooNbr + '\'' +
                ", segTattooNbr='" + segTattooNbr + '\'' +
                ", fltNbr='" + fltNbr + '\'' +
                ", depDt='" + depDt + '\'' +
                ", bdPt='" + bdPt + '\'' +
                ", offPnt='" + offPnt + '\'' +
                ", cbCls='" + cbCls + '\'' +
                ", passengerSSR=" + passengerSSR +
                '}';
    }

    private String cbCls;
    private List<PassengerSSR> passengerSSR;


    public PassengerItinerary() {
        passengerSSR = new ArrayList<PassengerSSR>();
    }

    public String getPnrLoc() {
        return pnrLoc;
    }

    public void setPnrLoc(String pnrLoc) {
        this.pnrLoc = pnrLoc;
    }

    public String getPnrCrtnDt() {
        return pnrCrtnDt;
    }

    public void setPnrCrtnDt(String pnrCrtnDt) {
        this.pnrCrtnDt = pnrCrtnDt;
    }

    public String getTattooNbr() {
        return tattooNbr;
    }

    public void setTattooNbr(String tattooNbr) {
        this.tattooNbr = tattooNbr;
    }

    public String getSegTattooNbr() {
        return segTattooNbr;
    }

    public void setSegTattooNbr(String segTattooNbr) {
        this.segTattooNbr = segTattooNbr;
    }

    public String getFltNbr() {
        return fltNbr;
    }

    public void setFltNbr(String fltNbr) {
        this.fltNbr = fltNbr;
    }

    public String getDepDt() {
        return depDt;
    }

    public void setDepDt(String depDt) {
        this.depDt = depDt;
    }

    public String getBdPt() {
        return bdPt;
    }

    public void setBdPt(String bdPt) {
        this.bdPt = bdPt;
    }

    public String getOffPnt() {
        return offPnt;
    }

    public void setOffPnt(String offPnt) {
        this.offPnt = offPnt;
    }

    public String getCbCls() {
        return cbCls;
    }

    public void setCbCls(String cbCls) {
        this.cbCls = cbCls;
    }

    public List<PassengerSSR> getPassengerSSR() {
        return passengerSSR;
    }

    public void setPassengerSSR(List<PassengerSSR> passengerSSR) {
        this.passengerSSR = passengerSSR;
    }

    public void addPassengerSSR(PassengerSSR passengerSSR) {
        if(this.passengerSSR == null){
            throw new IllegalStateException("passengerSSR is NULL..!");
        }
        this.passengerSSR.add(passengerSSR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PassengerItinerary that = (PassengerItinerary) o;

        if (pnrLoc != null ? !pnrLoc.equals(that.pnrLoc) : that.pnrLoc != null) return false;
        if (pnrCrtnDt != null ? !pnrCrtnDt.equals(that.pnrCrtnDt) : that.pnrCrtnDt != null) return false;
        if (tattooNbr != null ? !tattooNbr.equals(that.tattooNbr) : that.tattooNbr != null) return false;
        return segTattooNbr != null ? segTattooNbr.equals(that.segTattooNbr) : that.segTattooNbr == null;
    }

    @Override
    public int hashCode() {
        int result = pnrLoc != null ? pnrLoc.hashCode() : 0;
        result = 31 * result + (pnrCrtnDt != null ? pnrCrtnDt.hashCode() : 0);
        result = 31 * result + (tattooNbr != null ? tattooNbr.hashCode() : 0);
        result = 31 * result + (segTattooNbr != null ? segTattooNbr.hashCode() : 0);
        return result;
    }
    /*
    public boolean contains(String pnrLoc, String pnrCrtnDt, String tattooNbr, String segTattooNbr){

        return false;
    }
    */
}
