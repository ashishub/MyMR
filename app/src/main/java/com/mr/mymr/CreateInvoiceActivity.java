package com.mr.mymr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.NumberUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mr.mymr.adapter.ConsigneeAdapter;
import com.mr.mymr.adapter.ItemDropDownAdapter;
import com.mr.mymr.dto.CustomerDTO;
import com.mr.mymr.dto.CustomersDTO;
import com.mr.mymr.dto.InvoiceDTO;
import com.mr.mymr.dto.ItemDTO;
import com.mr.mymr.dto.ItemsDTO;
import com.mr.mymr.utils.MrConstants;
import com.mr.mymr.utils.StateCodes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.mr.mymr.utils.GSTINValidator.validGSTIN;
import static com.mr.mymr.utils.MrUtils.isAlphaNumericAndSpace;
import static com.mr.mymr.utils.MrUtils.isNotAlphaNumeric;
import static com.mr.mymr.utils.MrUtils.isNotAlphabetAndSpace;
import static com.mr.mymr.utils.MrUtils.isValidAmount;
import static com.mr.mymr.utils.MrUtils.isValidUOM;
import static com.mr.mymr.utils.MrUtils.isValidVehicleNumber;
import static com.mr.mymr.utils.MrUtils.loadCustomers;
import static com.mr.mymr.utils.MrUtils.loadItems;
import static com.mr.mymr.utils.MrUtils.returnGSTRateInString;
import static com.mr.mymr.utils.MrUtils.returnStringForDouble;

public class CreateInvoiceActivity extends AppCompatActivity {

    private static String TAG = "CreateInvoiceActivity";

    private static final int  INVOICE_DATE = 610;
    private static final int  PO_DATE = 611;
    private static final int  LR_DATE = 612;
    ItemsDTO itemsDTO;
    CustomersDTO customersDTO;
    MaterialButton printPDf;
    InvoiceDTO invoiceDto;
    final Calendar invoiceDateCalendar = Calendar.getInstance();
    final Calendar poDateCalendar = Calendar.getInstance();
    final Calendar lrDateCalendar = Calendar.getInstance();
    TextInputEditText invoiceDateTxtEditText, poDateEditTxt, txtConsigneeGst,
            txtConsigneeAddr, txtDeliveryCompName, txtDeliveryGst,
            txtDeliveryAddr, txtLRDate, txtFreightAmt, txtInvoiceNbr,
            txtInvoiceDate, txtPlaceOfSupply, txtPONbr, txtPODate, txtVehicleNbr, txtLRNbr;
    AutoCompleteTextView consigneeNameSelector, transportModeAutoComplete, stateAutoComplete, freightGstAutoComplete, additionalChargeDtlsAutoComplete;
    MaterialCardView deliveryCard;
    SwitchMaterial deliveryAddSwitch;
    LinearLayout itemsLayout;
    Integer SELECTED_CALENDAR = INVOICE_DATE;
    int countOfItems = 0;
    String[] states = null;
    private FirebaseFirestore mFirestore;


    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            switch (SELECTED_CALENDAR) {
                case INVOICE_DATE: {
                    invoiceDateCalendar.set(Calendar.YEAR, year);
                    invoiceDateCalendar.set(Calendar.MONTH, monthOfYear);
                    invoiceDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    invoiceDateTxtEditText.setText(sdf.format(invoiceDateCalendar.getTime()));
                    invoiceDateTxtEditText.setError(null);
                }
                break;
                case PO_DATE: {
                    poDateCalendar.set(Calendar.YEAR, year);
                    poDateCalendar.set(Calendar.MONTH, monthOfYear);
                    poDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    poDateEditTxt.setText(sdf.format(poDateCalendar.getTime()));
                    poDateEditTxt.setError(null);
                }
                break;
                case LR_DATE: {
                    lrDateCalendar.set(Calendar.YEAR, year);
                    lrDateCalendar.set(Calendar.MONTH, monthOfYear);
                    lrDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    txtLRDate.setText(sdf.format(lrDateCalendar.getTime()));
                    txtLRDate.setError(null);
                }
                break;
            }
        }

    };

    private void initializeFormFields() {

        // Set default Invoice Date
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        invoiceDateTxtEditText.setText(sdf.format(invoiceDateCalendar.getTime()));

        //Load state dropdown
        loadStateDropdown();
    }

    private void initFirestore() {
        // TODO(developer): Implement
        mFirestore = FirebaseFirestore.getInstance();
        Log.i("CreateInvoiceActivity", "initFirestore is triggered");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_invoice);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Initialize Firestore and the main RecyclerView
        initFirestore();


        customersDTO = loadCustomers(this, R.raw.customers);

        invoiceDto = new InvoiceDTO();
        itemsLayout = findViewById(R.id.itemsLayout);
        deliveryCard = findViewById(R .id.deliveryCard);
        printPDf = findViewById(R.id.printPDF);
        consigneeNameSelector = findViewById(R.id.consigneeNameSelector);
        invoiceDateTxtEditText = findViewById(R.id.txtInvoiceDate);
        txtDeliveryCompName = findViewById(R.id.txtDeliveryCompName);
        txtDeliveryGst = findViewById(R.id.txtDeliveryGst);
        txtDeliveryAddr = findViewById(R.id.txtDeliveryAddr);
        txtConsigneeGst = findViewById(R.id.txtConsigneeGst);
        txtConsigneeAddr = findViewById(R.id.txtConsigneeAddr);
        poDateEditTxt = findViewById(R.id.txtPODate);
        deliveryAddSwitch = findViewById(R.id.deliveryAddSwitch);
        txtLRDate = findViewById(R.id.txtLRDate);
        txtFreightAmt = findViewById(R.id.txtFreightAmt);
        txtInvoiceNbr = findViewById(R.id.txtInvoiceNbr);
        txtInvoiceDate = findViewById(R.id.txtInvoiceDate);
        txtPlaceOfSupply = findViewById(R.id.txtPlaceOfSupply);
        txtPONbr = findViewById(R.id.txtPONbr);
        txtPODate = findViewById(R.id.txtPODate);
        txtVehicleNbr = findViewById(R.id.txtVehicleNbr);
        txtLRNbr = findViewById(R.id.txtLRNbr);

        //Creating the instance of ArrayAdapter containing list of supplier names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, MrConstants.CUSTOMER_NAME);
        ArrayAdapter<String> transportAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, MrConstants.TRANSPORT_MODE);
        ArrayAdapter<String> freightAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, MrConstants.GST_RATES);
        ArrayAdapter<String> addnlChargeAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, MrConstants.ADDNL_CHARGE_DETAILS);
        ConsigneeAdapter consigneeAdapter = new ConsigneeAdapter(this, R.layout.dropdown_menu_popup_item, customersDTO.getCustomerDTOList());


        //Add atleast one new Item line to being with invoice.
        addNewItem();

        consigneeNameSelector = findViewById(R.id.consigneeNameSelector);
        consigneeNameSelector.setAdapter(consigneeAdapter);

        transportModeAutoComplete = findViewById(R.id.transportModeAutoComplete);
        transportModeAutoComplete.setAdapter(transportAdapter);
        transportModeAutoComplete.setText(MrConstants.DEFAULT_ROAD_TRANSPORT);
        transportModeAutoComplete.setOnItemClickListener((parent, view, position, id) -> transportModeAutoComplete.setError(null));

        freightGstAutoComplete = findViewById(R.id.freightGstAutoComplete);
        freightGstAutoComplete.setAdapter(freightAdapter);
        freightGstAutoComplete.setOnItemClickListener((parent, view, position, id) -> freightGstAutoComplete.setError(null));

        additionalChargeDtlsAutoComplete = findViewById(R.id.additionalChargeDtlsAutoComplete);
        additionalChargeDtlsAutoComplete.setAdapter(addnlChargeAdapter);
        additionalChargeDtlsAutoComplete.setOnItemClickListener((parent, view, position, id) -> additionalChargeDtlsAutoComplete.setError(null));

        consigneeNameSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomerDTO customerDTO = (CustomerDTO) view.getTag();
                consigneeNameSelector.setText(customerDTO.getCustomer_name());
                txtConsigneeGst.setText(customerDTO.getGstn());
                txtConsigneeAddr.setText(customerDTO.getAddress());
                consigneeNameSelector.setError(null);
                txtConsigneeGst.setError(null);
                txtConsigneeAddr.setError(null);
//                Toast.makeText(CreateInvoiceActivity.this,
//                        "Clicked item from auto completion list "
//                                + parent.getItemAtPosition(position)
//                        , Toast.LENGTH_LONG).show();
            }
        });




        printPDf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    // on error
                    Toast.makeText(CreateInvoiceActivity.this, R.string.erros_on_page, Toast.LENGTH_LONG).show();
                    return;
                }

                invoiceDto = new InvoiceDTO();
                invoiceDto.setInvoiceNbr(txtInvoiceNbr.getText().toString());
                invoiceDto.setInvoiceDate(txtInvoiceDate.getText().toString());
                invoiceDto.setConsigneeName(consigneeNameSelector.getText().toString());
                captureAddress(R.id.txtConsigneeAddr);
                if (!txtConsigneeGst.getText().toString().isEmpty()) {
                    invoiceDto.setStateCodeConsignee(StateCodes.getCodeFromGST(txtConsigneeGst.getText().toString()).getCode());
                    invoiceDto.setConsigneeState(StateCodes.getCodeFromGST(txtConsigneeGst.getText().toString()).getName());
                }
                invoiceDto.setGstNbrConsignee(txtConsigneeGst.getText().toString());
                invoiceDto.setPoNbr(txtPONbr.getText().toString());
                invoiceDto.setPoDate(txtPODate.getText().toString());
                invoiceDto.setPlaceOfSupply(txtPlaceOfSupply.getText().toString());
                invoiceDto.setDeliveryStateCode(StateCodes.getCodeFromStateName(stateAutoComplete.getText().toString()).getCode());
                invoiceDto.setDeliveryState(stateAutoComplete.getText().toString());
                if (!deliveryAddSwitch.isChecked()){
                    //Copy
                    invoiceDto.setDeliveryComp(invoiceDto.getConsigneeName());
                    invoiceDto.setAddressLine1Delivery(invoiceDto.getAddressLine1Consignee());
                    invoiceDto.setAddressLine2Delivery(invoiceDto.getAddressLine2Consignee());
                    invoiceDto.setAddressLine3Delivery(invoiceDto.getAddressLine3Consignee());
                    invoiceDto.setGstNbrDelivery(invoiceDto.getGstNbrConsignee());
                } else {
                    invoiceDto.setDeliveryComp(txtDeliveryCompName.getText().toString());
                    captureAddress(R.id.txtDeliveryAddr);
                    invoiceDto.setGstNbrDelivery(((TextInputEditText) findViewById(R.id.txtDeliveryGst)).getText().toString());
                }
                invoiceDto.setTransportMode(((AutoCompleteTextView)findViewById(R.id.transportModeAutoComplete)).getText().toString());
                invoiceDto.setVehicleNbr(txtVehicleNbr.getText().toString());
                invoiceDto.setLrNbr(txtLRNbr.getText().toString());
                invoiceDto.setLrDate(txtLRDate.getText().toString());

                // TODO - Prepare line items and its totals
                int lineItemsCount = itemsLayout.getChildCount();
                List<ItemDTO> items = new ArrayList<>();
                double gstRate;
                ItemDTO itemDTO;
                MaterialCardView itemCard;
                AutoCompleteTextView itemNameAutoComplete, uomAutoComplete;
                TextView txtVwGstRate;
                double invoiceTotal = 0d, cgstTotal = 0d, igstTotal = 0d, sgstTotal = 0d, totalTaxableAmount = 0d;
                double chargeTotal = 0d, chargeCgstTotal = 0d, chargeIgstTotal = 0d, chargeSgstTotal = 0d, totalTaxableChargeAmount = 0d;
                boolean isTamilNadu = txtConsigneeGst.getText().toString().substring(0,2).equals(getString(R.string.TAMIL_NADU_STATE_GST_CD)) ? true : false;
                TextInputEditText hsnNumber, txtQuantity, txtUnitPrice;
                for (int i = 0; i < itemsLayout.getChildCount(); i++) {
                    itemCard = (MaterialCardView) itemsLayout.getChildAt(i);
                    itemNameAutoComplete = itemCard.findViewById(R.id.itemNameAutoComplete);
                    uomAutoComplete = itemCard.findViewById(R.id.uomAutoComplete);
                    hsnNumber = itemCard.findViewById(R.id.hsnNumber);
                    txtQuantity = itemCard.findViewById(R.id.txtQuantity);
                    txtUnitPrice = itemCard.findViewById(R.id.txtUnitPrice);
                    txtVwGstRate = itemCard.findViewById(R.id.txtVwGstRate);

                    // start setting other values and calculate totals
                    itemDTO = new ItemDTO();
                    itemDTO.setSNo(Integer.toString(i + 1));
                    itemDTO.setItemDesc(itemNameAutoComplete.getText().toString());
                    itemDTO.setUom(uomAutoComplete.getText().toString());
                    itemDTO.setHsnCode(hsnNumber.getText().toString());
                    gstRate = Double.parseDouble(txtVwGstRate.getText().toString().replace("%", ""));
                    itemDTO.setGstPerctage(returnGSTRateInString(gstRate));
                    itemDTO.setInterState(!isTamilNadu);
                    itemDTO.setRate(Double.parseDouble(txtUnitPrice.getText().toString()));
                    itemDTO.setQuantity(txtQuantity.getText().toString());
                    itemDTO.setTaxableValue(itemDTO.getRate() * Double.parseDouble(txtQuantity.getText().toString()));
                    if (isTamilNadu) {
                        itemDTO.setCsgt(itemDTO.getTaxableValue() * ((gstRate / 2) / 100));
                        itemDTO.setSgst(itemDTO.getTaxableValue() * ((gstRate / 2) / 100));
                    } else {
                        itemDTO.setIgst(itemDTO.getTaxableValue() * (gstRate / 100));
                    }
                    itemDTO.setLineTotal(itemDTO.getTaxableValue() + itemDTO.getCsgt() + itemDTO.getSgst() + itemDTO.getIgst());
                    items.add(itemDTO);

                    totalTaxableAmount += itemDTO.getTaxableValue();
                    invoiceTotal += itemDTO.getLineTotal();
                    cgstTotal += itemDTO.getCsgt();
                    sgstTotal += itemDTO.getSgst();
                    igstTotal += itemDTO.getIgst();

                }

                if (!TextUtils.isEmpty(additionalChargeDtlsAutoComplete.getText().toString())) {
                    String addnlChargeName = additionalChargeDtlsAutoComplete.getText().toString();
                    chargeTotal = Double.parseDouble(txtFreightAmt.getText().toString());
                    gstRate = Double.parseDouble(freightGstAutoComplete.getText().toString());
                    if (isTamilNadu) {
                        chargeSgstTotal = chargeTotal * ((gstRate / 2) / 100);
                        chargeCgstTotal = chargeTotal * ((gstRate / 2) / 100);
                        invoiceDto.setFcgst(returnGSTRateInString(Double.valueOf(gstRate / 2)));
                        invoiceDto.setFsgst(returnGSTRateInString(Double.valueOf(gstRate / 2)));
                        invoiceDto.setFcgstAmount(returnStringForDouble(chargeCgstTotal));
                        invoiceDto.setFsgstAmount(returnStringForDouble(chargeSgstTotal));
                    } else {
                        chargeIgstTotal = chargeTotal * (gstRate / 100);
                        invoiceDto.setFigst(returnGSTRateInString(gstRate));
                        invoiceDto.setFigstAmount(returnStringForDouble(chargeIgstTotal));
                    }
                    totalTaxableChargeAmount = chargeTotal + chargeSgstTotal + chargeCgstTotal + chargeIgstTotal;

                    totalTaxableAmount += chargeTotal;
                    invoiceTotal += totalTaxableChargeAmount;
                    cgstTotal += chargeCgstTotal;
                    sgstTotal += chargeSgstTotal;
                    igstTotal += chargeIgstTotal;

                    invoiceDto.setAddnlChargeTitle(addnlChargeName);
                    invoiceDto.setFreightTaxableAmt(returnStringForDouble(chargeTotal));
                    invoiceDto.setFreightTotalAmount(returnStringForDouble(totalTaxableChargeAmount));
                }
                invoiceDto.setItems(items);
                invoiceDto.setInvoiceTotal(returnStringForDouble(invoiceTotal, 0));
                invoiceDto.setCgstTotal(returnStringForDouble(cgstTotal));
                invoiceDto.setSgstTotal(returnStringForDouble(sgstTotal));
                invoiceDto.setIgstTotal(returnStringForDouble(igstTotal));
                invoiceDto.setTotalGst(returnStringForDouble(cgstTotal + sgstTotal + igstTotal));
                invoiceDto.setTotalTaxableAmt(returnStringForDouble(totalTaxableAmount));

                save(invoiceDto);

                Intent intent = new Intent(CreateInvoiceActivity.this, InvoiceActivity.class);
                intent.putExtra(getString(R.string.INVOICE_DATA), invoiceDto);
                startActivity(intent);
            }
        });

        deliveryAddSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // delivery details are different
                deliveryCard.setVisibility(View.VISIBLE);
                copySupplierDetailsInDeliverySection();
            } else {
                deliveryCard.setVisibility(View.GONE);
            }
        });

        invoiceDateTxtEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_CALENDAR = INVOICE_DATE;
                createDatePicker();
            }
        });

        poDateEditTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_CALENDAR = PO_DATE;
                createDatePicker();
            }
        });

        txtLRDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECTED_CALENDAR = LR_DATE;
                createDatePicker();
            }
        });
        initializeFormFields();
    }

    private boolean validateFields(){
        boolean isInvalid = false;
        if (isNotAlphaNumeric(txtInvoiceNbr.getText().toString())) {
            txtInvoiceNbr.setError("Enter valid invoice number");
            isInvalid = true;
        }
        if (TextUtils.isEmpty(txtInvoiceDate.getText())) {
            txtInvoiceDate.setError("Enter valid invoice date");
            isInvalid = true;
        }
        if (!isAlphaNumericAndSpace(txtPlaceOfSupply.getText().toString())) {
            txtPlaceOfSupply.setError("Invalid delivery City/town");
            isInvalid = true;
        }
        if (isNotAlphabetAndSpace(stateAutoComplete.getText().toString())) {
            stateAutoComplete.setError("Invalid delivery state");
            isInvalid = true;
        }

        if (TextUtils.isEmpty(txtPONbr.getText()) && !TextUtils.isEmpty(txtPODate.getText())) {
            txtPONbr.setError("Invalid PO Number.");
            isInvalid = true;
        } else if (!TextUtils.isEmpty(txtPONbr.getText()) && TextUtils.isEmpty(txtPODate.getText())) {
            txtPODate.setError("Invalid PO Date.");
            isInvalid = true;
        }
        if (TextUtils.isEmpty(consigneeNameSelector.getText())) {
            consigneeNameSelector.setError("Invalid Bill To Name");
            isInvalid = true;
        }
        if (TextUtils.isEmpty(txtConsigneeGst.getText()) || !validGSTIN(txtConsigneeGst.getText().toString())) {
            txtConsigneeGst.setError("Invalid Bill To GST");
            isInvalid = true;
        }
        if (TextUtils.isEmpty(txtConsigneeAddr.getText())) {
            txtConsigneeAddr.setError("Invalid Bill To Address");
            isInvalid = true;
        }
        if (deliveryAddSwitch.isChecked()){
            if(TextUtils.isEmpty(txtDeliveryCompName.getText())) {
                txtDeliveryCompName.setError("Invalid Ship To Name");
                isInvalid = true;
            }
            if (TextUtils.isEmpty(txtDeliveryGst.getText()) || !validGSTIN(txtDeliveryGst.getText().toString())) {
                txtDeliveryGst.setError("Invalid Ship To GST");
                isInvalid = true;
            }
            if (TextUtils.isEmpty(txtDeliveryAddr.getText())) {
                txtDeliveryAddr.setError("Invalid Ship To Address");
                isInvalid = true;
            }
        }
        if (!isValidVehicleNumber(txtVehicleNbr.getText().toString())) {
            txtVehicleNbr.setError("Invalid Vehicle Number.");
            isInvalid = true;
        }
        if (TextUtils.isEmpty(txtLRNbr.getText()) && !TextUtils.isEmpty(txtLRDate.getText())) {
            txtLRNbr.setError("Invalid LR Number.");
            isInvalid = true;
        } else if (!TextUtils.isEmpty(txtLRNbr.getText()) && TextUtils.isEmpty(txtLRDate.getText())) {
            txtLRDate.setError("Invalid LR Date.");
            isInvalid = true;
        }
        if (validateLineItems()) {
            isInvalid = true;
        }
        if (!TextUtils.isEmpty(additionalChargeDtlsAutoComplete.getText())) {
            if (TextUtils.isEmpty(txtFreightAmt.getText())) {
                txtFreightAmt.setError("Invalid Charge Amount.");
                isInvalid = true;
            }
            if (!isValidAmount(freightGstAutoComplete.getText().toString())) {
                freightGstAutoComplete.setError("Invalid GST rate.");
                isInvalid = true;
            }
        }
        return  isInvalid;
    }

    private boolean validateLineItems() {
        boolean isInvalid = false;
        MaterialCardView itemCard;
        AutoCompleteTextView itemNameAutoComplete, uomAutoComplete;
//        TextView txtVwGstRate;
        TextInputEditText hsnNumber, txtQuantity, txtUnitPrice;
        for (int i = 0; i < itemsLayout.getChildCount(); i++) {
            itemCard = (MaterialCardView) itemsLayout.getChildAt(i);
            itemNameAutoComplete = itemCard.findViewById(R.id.itemNameAutoComplete);
            uomAutoComplete = itemCard.findViewById(R.id.uomAutoComplete);
            hsnNumber = itemCard.findViewById(R.id.hsnNumber);
            txtQuantity = itemCard.findViewById(R.id.txtQuantity);
            txtUnitPrice = itemCard.findViewById(R.id.txtUnitPrice);
//            txtVwGstRate = itemCard.findViewById(R.id.txtVwGstRate);
            if (TextUtils.isEmpty(itemNameAutoComplete.getText())) {
                itemNameAutoComplete.setError("Invalid Item Name.");
                isInvalid = true;
            }
            if (!isValidUOM(uomAutoComplete.getText().toString())) {
                uomAutoComplete.setError("Invalid Unit for Item.");
                isInvalid = true;
            }
            if (!TextUtils.isDigitsOnly(hsnNumber.getText())) {
                hsnNumber.setError("Invalid HSN Number.");
                isInvalid = true;
            }
            if (!isValidAmount(txtQuantity.getText().toString())) {
                txtQuantity.setError("Invalid Quantity");
                isInvalid = true;
            }
            if (!isValidAmount(txtUnitPrice.getText().toString())) {
                txtUnitPrice.setError("Invalid Unit Price");
                isInvalid = true;
            }
            //GST rate Validation not required.
        }
        return isInvalid;
    }

    private void save(InvoiceDTO invoiceDTO) {
        // Get a reference to the restaurants collection
        if (invoiceDTO != null) {
            Log.d("CreateInvoiceActivity", "########## Before Saving document");
            CollectionReference invoices = mFirestore.collection("invoices");
            invoices.add(invoiceDTO);
            Log.d("CreateInvoiceActivity", "After Saving document ########");
        }

    }

    private void captureAddress(int id) {
        boolean isConsignee = (id == R.id.txtConsigneeAddr) ? true : false;
        String address = ((TextInputEditText) findViewById(id)).getText().toString();
        String[] addressStrings = address.split("\n");
        for (int i = 0; i < addressStrings.length; i++) {
            switch (i){
                case 0:
                {
                    if (isConsignee) {
                        invoiceDto.setAddressLine1Consignee(addressStrings[i]);
                    } else {
                        invoiceDto.setAddressLine1Delivery(addressStrings[i]);
                    }

                }
                break;
                case 1:
                {
                    if (isConsignee) {
                        invoiceDto.setAddressLine2Consignee(addressStrings[i]);
                    } else {
                        invoiceDto.setAddressLine2Delivery(addressStrings[i]);
                    }
                }
                break;
                case 2:
                {
                    if (isConsignee) {
                        invoiceDto.setAddressLine3Consignee(addressStrings[i]);
                    } else {
                        invoiceDto.setAddressLine3Delivery(addressStrings[i]);
                    }
                }
                break;
            }
        }
    }

    /**
     * Add new item component to the view
     */
    private void addNewItem() {
        Log.d("CreateInvoiceActivity", "Starting to add a new item :" +new Date().toString());
        countOfItems += 1;
        itemsDTO = loadItems(this, R.raw.items);
        ArrayAdapter<String> uomAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_popup_item, MrConstants.UOM_CODES_MODE);
        ItemDropDownAdapter itemsDtoArrayAdapter = new ItemDropDownAdapter(this, R.layout.dropdown_menu_popup_item, itemsDTO.getItems());
        AutoCompleteTextView itemNameAutoComplete, uomAutoComplete;
        MaterialButton addBtn, removeBtn;
        TextView txtViewItemNbr, txtVwGstRate;
        TextInputEditText hsnNumber;
        View view = LayoutInflater.from(this).inflate(R.layout.item_layout, null);
        view.setFocusable(true);

        addBtn = view.findViewById(R.id.btnAddNewItem);
        removeBtn = view.findViewById(R.id.btnRemoveItem);
        txtViewItemNbr = view.findViewById(R.id.txtViewItemNbr);
        hsnNumber = view.findViewById(R.id.hsnNumber);
        txtVwGstRate = view.findViewById(R.id.txtVwGstRate);

        // prepare uom dropwdown
        uomAutoComplete = view.findViewById(R.id.uomAutoComplete);
        uomAutoComplete.setAdapter(uomAdapter);
        uomAutoComplete.setOnItemClickListener((parent, view1, position, id) -> uomAutoComplete.setError(null));

        // Prepare list of items drop down and handle its click function
        itemNameAutoComplete = view.findViewById(R.id.itemNameAutoComplete);
        itemNameAutoComplete.setAdapter(itemsDtoArrayAdapter);
        itemNameAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemDTO itemDTO = (ItemDTO) view.getTag();
                itemNameAutoComplete.setText(itemDTO.getItemDesc());
                hsnNumber.setText(itemDTO.getHsnCode());
                uomAutoComplete.setText(itemDTO.getUom());
                txtVwGstRate.setText(itemDTO.getGstPerctage() + "%");

                itemNameAutoComplete.setError(null);
                hsnNumber.setError(null);
                uomAutoComplete.setError(null);
//                Toast.makeText(CreateInvoiceActivity.this,
//                        "Clicked item from auto completion list "
//                                + parent.getItemAtPosition(position)
//                        , Toast.LENGTH_LONG).show();
            }
        });

        txtViewItemNbr.setText("Item "+ (itemsLayout.getChildCount() == 0 ? countOfItems : itemsLayout.getChildCount() + 1));
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem();
            }
        });
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton button = (MaterialButton) v;
                if (itemsLayout.getChildCount() > 1) {
                    countOfItems -= 1;
                    MaterialCardView mcv = (MaterialCardView) button.getParent().getParent().getParent();
                    itemsLayout.removeView(mcv);
                    reassignItemNumbers();
                }
            }
        });
        itemsLayout.addView(view);
        if (countOfItems > 1) {
            view.requestFocus();
        }
        Log.d("CreateInvoiceActivity", "Finished to add a new item :" +new Date().toString());
    }

    private void reassignItemNumbers() {
        int i;
        MaterialCardView materialCardView = null;
        TextView txtViewItemNbr;
        for (i = 0; i < itemsLayout.getChildCount(); i++) {
            materialCardView = (MaterialCardView) itemsLayout.getChildAt(i);
            txtViewItemNbr = materialCardView.findViewById(R.id.txtViewItemNbr);
            txtViewItemNbr.setText("Item "+ (i + 1));
        }
    }

    // TODO - Make this async later.
    private void loadStateDropdown() {
        if (states == null) {
            states = StateCodes.getAllStates();
            Arrays.sort(states);
            ArrayAdapter<String> statesAdapter = new ArrayAdapter<>(CreateInvoiceActivity.this, R.layout.dropdown_menu_popup_item, states);
            stateAutoComplete = findViewById(R.id.stateAutoComplete);
            stateAutoComplete.setAdapter(statesAdapter);
        }
    }

    private void copySupplierDetailsInDeliverySection() {
        txtDeliveryCompName.setText(consigneeNameSelector.getText().toString());
        txtDeliveryGst.setText(txtConsigneeGst.getText().toString());
        txtDeliveryAddr.setText(txtConsigneeAddr.getText().toString());
    }

    private void createDatePicker() {
        switch (SELECTED_CALENDAR) {
            case INVOICE_DATE: {
                new DatePickerDialog(CreateInvoiceActivity.this, dateSetListener, invoiceDateCalendar
                        .get(Calendar.YEAR), invoiceDateCalendar.get(Calendar.MONTH),
                        invoiceDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
            break;
            case PO_DATE: {
                new DatePickerDialog(CreateInvoiceActivity.this, dateSetListener, poDateCalendar
                        .get(Calendar.YEAR), poDateCalendar.get(Calendar.MONTH),
                        poDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
            break;
            case LR_DATE: {
                new DatePickerDialog(CreateInvoiceActivity.this, dateSetListener, lrDateCalendar
                        .get(Calendar.YEAR), lrDateCalendar.get(Calendar.MONTH),
                        lrDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
            break;
        }
    }
}