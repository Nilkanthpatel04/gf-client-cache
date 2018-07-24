package com.sia.gemfire.client.util;

import java.io.Serializable;

public class ItineraryIndex implements Serializable {
    private String pnrLoc;
    private String pnrCrtnDt;
    private String tattooNbr;
    private String segTattooNbr;

    public ItineraryIndex(){

    }

    public ItineraryIndex(String pnrLoc, String pnrCrtnDt, String tattooNbr, String segTattooNbr) {
        this.pnrLoc = pnrLoc;
        this.pnrCrtnDt = pnrCrtnDt;
        this.tattooNbr = tattooNbr;
        this.segTattooNbr = segTattooNbr;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItineraryIndex that = (ItineraryIndex) o;

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
}

