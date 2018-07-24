package com.sia.gemfire.client.object;

import java.io.Serializable;

public class PassengerSSR implements Serializable {
    private String ssrTp;
    private String ssrSt;
    private String ssrFreeTxt;
    private String ssrDescription;
    private String srcStmId;
    private String otTattoNbr;
    private String legNo;
    private String ssrCode;

    @Override
    public String toString() {
        return "PassengerSSR{" +
                "ssrTp='" + ssrTp + '\'' +
                ", ssrSt='" + ssrSt + '\'' +
                ", ssrFreeTxt='" + ssrFreeTxt + '\'' +
                ", ssrDescription='" + ssrDescription + '\'' +
                ", srcStmId='" + srcStmId + '\'' +
                ", otTattoNbr='" + otTattoNbr + '\'' +
                ", legNo='" + legNo + '\'' +
                ", ssrCode='" + ssrCode + '\'' +
                ", legBdPnt='" + legBdPnt + '\'' +
                ", legOffPnt='" + legOffPnt + '\'' +
                ", ssrNbr='" + ssrNbr + '\'' +
                '}';
    }

    public String getLegBdPnt() {
        return legBdPnt;
    }

    public void setLegBdPnt(String legBdPnt) {
        this.legBdPnt = legBdPnt;
    }

    private String legBdPnt;

    public String getLegOffPnt() {
        return legOffPnt;
    }

    public void setLegOffPnt(String ledOffPnt) {
        this.legOffPnt = ledOffPnt;
    }

    private String legOffPnt;

    public String getSsrNbr() {
        return ssrNbr;
    }

    public void setSsrNbr(String ssrNbr) {
        this.ssrNbr = ssrNbr;
    }

    private String ssrNbr;

    public String getSsrTp() {
        return ssrTp;
    }
    public void setSsrTp(String ssrTp) {
        this.ssrTp = ssrTp;
    }
    public String getSsrSt() {
        return ssrSt;
    }
    public void setSsrSt(String ssrSt) {
        this.ssrSt = ssrSt;
    }
    public String getSsrFreeTxt() {
        return ssrFreeTxt;
    }
    public void setSsrFreeTxt(String ssrFreeTxt) {
        this.ssrFreeTxt = ssrFreeTxt;
    }
    public String getSsrDescription() {
        return ssrDescription;
    }
    public void setSsrDescription(String ssrDescription) {
        this.ssrDescription = ssrDescription;
    }
    public String getSrcStmId() {
        return srcStmId;
    }
    public void setSrcStmId(String srcStmId) {
        this.srcStmId = srcStmId;
    }
    public String getOtTattoNbr() {
        return otTattoNbr;
    }
    public void setOtTattoNbr(String otTattoNbr) {
        this.otTattoNbr = otTattoNbr;
    }
    public String getLegNo() {
        return legNo;
    }
    public void setLegNo(String legNo) {
        this.legNo = legNo;
    }
    public String getSsrCode() {
        return ssrCode;
    }
    public void setSsrCode(String ssrCode) {
        this.ssrCode = ssrCode;
    }
}
