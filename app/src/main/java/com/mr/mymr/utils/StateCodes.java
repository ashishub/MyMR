package com.mr.mymr.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;
public enum StateCodes {
    JAMMU_AND_KASHMIR("01", "JAMMU AND KASHMIR"),
    HIMACHAL_PRADESH("02", "HIMACHAL PRADESH"),
    PUNJAB("03", "PUNJAB"),
    CHANDIGARH("04", "CHANDIGARH"),
    UTTARAKHAND("05", "UTTARAKHAND"),
    HARYANA("06", "HARYANA"),
    DELHI("07", "DELHI"),
    RAJASTHAN("08", "RAJASTHAN"),
    UTTAR_PRADESH("09", "UTTAR PRADESH"),
    BIHAR("10", "BIHAR"),
    SIKKIM("11", "SIKKIM"),
    ARUNACHAL_PRADESH("12", "ARUNACHAL PRADESH"),
    NAGALAND("13", "NAGALAND"),
    MANIPUR("14", "MANIPUR"),
    MIZORAM("15", "MIZORAM"),
    TRIPURA("16", "TRIPURA"),
    MEGHLAYA("17", "MEGHLAYA"),
    ASSAM("18", "ASSAM"),
    WEST_BENGAL("19", "WEST BENGAL"),
    JHARKHAND("20", "JHARKHAND"),
    ODISHA("21", "ODISHA"),
    CHATTISGARH("22", "CHATTISGARH"),
    MADHYA_PRADESH("23", "MADHYA PRADESH"),
    GUJARAT("24", "GUJARAT"),
    DADRA_AND_NAGAR_HAVELI_AND_DAMAN_AND_DIU("26", "DADRA AND NAGAR HAVELI AND DAMAN AND DIU"),
    MAHARASHTRA("27", "MAHARASHTRA"),
    ANDHRA_PRADESH_BEFORE_DIVISION("28", "ANDHRA PRADESH(BEFORE DIVISION)"),
    KARNATAKA("29", "KARNATAKA"),
    GOA("30", "GOA"),
    LAKSHWADEEP("31", "LAKSHWADEEP"),
    KERALA("32", "KERALA"),
    TAMIL_NADU("33", "TAMIL NADU"),
    PUDUCHERRY("34", "PUDUCHERRY"),
    ANDAMAN_AND_NICOBAR_ISLANDS("35", "ANDAMAN AND NICOBAR ISLANDS"),
    TELANGANA("36", "TELANGANA"),
    ANDHRA_PRADESH("37", "ANDHRA PRADESH"),
    LADAKH("38", "LADAKH");

    private String code;
    private String name;

    StateCodes(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static StateCodes getCodeFromGST(String gstNumber){
        StateCodes stateCodes = null;
        String stateCodeStringFromGst = null;
        if (gstNumber != null && !gstNumber.equals("")) {
            stateCodeStringFromGst = gstNumber.substring(0,2);
            for (StateCodes sc : StateCodes.values()) {
                if (sc.getCode().equals(stateCodeStringFromGst)) {
                    stateCodes = sc;
                }
            }
        }
        return stateCodes;
    }

    public static StateCodes getCodeFromStateName(String stateName){
        StateCodes stateCodes = null;
        if (!TextUtils.isEmpty(stateName)) {
            for (StateCodes sc : StateCodes.values()) {
                if (sc.getName().equalsIgnoreCase(stateName)) {
                    stateCodes = sc;
                }
            }
        }
        return stateCodes;
    }

    public static String[] getAllStates() {
        int size = StateCodes.values().length;
        StateCodes[] stateCodes = StateCodes.values();
        String[] states = new String[size];
        for (int i=0; i < size; i++) {
            states[i] =  stateCodes[i].getName();
        }
        return states;
    }

}
