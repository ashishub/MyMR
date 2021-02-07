package com.mr.mymr.dto;

import android.text.TextUtils;

import java.io.Serializable;

import lombok.Data;

import static com.mr.mymr.utils.MrUtils.returnGSTRateInString;

@Data
public class ItemDTO implements Serializable {

    private static final long serialVersionUID = 3039591949217617L;

    String sNo;
    String itemDesc;
    String uom;
    String hsnCode;
    String gstPerctage;
    String quantity;
    double rate;
    double taxableValue;
    double csgt;
    double sgst;
    double igst;
    double lineTotal;
    boolean interState;

    public String getCGstPerctage() {
        return interState || TextUtils.isEmpty(gstPerctage) ? "" : returnGSTRateInString((Double.parseDouble(gstPerctage) / 2));
    }

    public String getSGstPerctage() {
        return getCGstPerctage();
    }

    public String getIGstPerctage() {
        return !interState || TextUtils.isEmpty(gstPerctage) ? "" : returnGSTRateInString(Double.parseDouble(gstPerctage));
    }
}
