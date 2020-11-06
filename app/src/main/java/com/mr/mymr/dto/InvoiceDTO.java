package com.mr.mymr.dto;

import com.mr.mymr.utils.MrConstants;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class InvoiceDTO implements Serializable {
    private String invoiceNbr;
    private String invoiceDate;
    private String consigneeName;
    private String addressLine1Consignee;
    private String addressLine2Consignee;
    private String addressLine3Consignee;
    private String stateCodeConsignee;
    private String consigneeState;
    private String gstNbrConsignee;
    private String poNbr;
    private String poDate;
    private String placeOfSupply;
    private String deliveryComp;
    private String addressLine1Delivery;
    private String addressLine2Delivery;
    private String addressLine3Delivery;
    private String gstNbrDelivery;
    private String transportMode;
    private String vehicleNbr;
    private String deliveryState;
    private String lrNbr;
    private String lrDate;
    private String deliveryStateCode;
    private String amountInWords;
    private String totalTaxableAmt;
    private String totalGst;
    private String invoiceTotal;
    private String addnlChargeTitle;
    private List<ItemDTO> items;

    // Freight or additional charge details
    private String freightTaxableAmt;
    private String fcgst;
    private String fcgstAmount;
    private String fsgst;
    private String fsgstAmount;
    private String figst;
    private String figstAmount;
    private String freightTotalAmount;
    private String cgstTotal;
    private String sgstTotal;
    private String igstTotal;

    public void setTransportMode(String transportMode) {
        if (transportMode == null || transportMode.equals("")){
            this.transportMode = MrConstants.DEFAULT_ROAD_TRANSPORT;
        } else {
            this.transportMode = transportMode;
        }
    }
}
