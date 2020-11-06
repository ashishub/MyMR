package com.mr.mymr.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mr.mymr.dto.CustomersDTO;
import com.mr.mymr.dto.ItemsDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class MrUtils {

    private static String ALPHA_NUMBERIC_REGEX = "[A-Za-z0-9]+";
    private static String ALPHA_NUMBERIC_SPACE_REGEX = "[A-Za-z0-9\\s]+";
    private static String ALPHA_SPACE_REGEX = "[A-Za-z\\s]+";
    private static String UOM_REGEX = "^[A-Za-z]{3}$";
    private static String NUMBER_WITH_TWO_DECIMAL_REGEX = "^\\d*(\\.\\d{1,2})?$";
    private static String VEHICLE_NUMBER_REGEX = "^[A-Z|a-z]{2}\\s?[0-9]{1,2}\\s?[A-Z|a-z]{0,3}\\s?[0-9]{4}$";

    //TODO - Add validation to check for valid UOM codes amoung the available list
    public final static boolean isValidUOM(String target) {
        return !TextUtils.isEmpty(target) && target.matches(UOM_REGEX);
//                && Arrays.asList(MrConstants.UOM_CODES_MODE).stream().anyMatch(s -> s.equalsIgnoreCase(target))
    }

    public final static boolean isValidVehicleNumber(String target) {
        return !TextUtils.isEmpty(target) && target.matches(VEHICLE_NUMBER_REGEX);
    }

    public final static boolean isValidAmount(String target) {
        return !TextUtils.isEmpty(target) && target.matches(NUMBER_WITH_TWO_DECIMAL_REGEX);
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isAlphaNumeric(String target) {
        return !TextUtils.isEmpty(target) && target.matches(ALPHA_NUMBERIC_REGEX);
    }

    public final static boolean isAlphaNumericAndSpace(String target) {
        return !TextUtils.isEmpty(target) && target.matches(ALPHA_NUMBERIC_SPACE_REGEX);
    }

    public final static boolean isNotAlphaNumeric(String target) {
        return !isAlphaNumeric(target);
    }

    public final static boolean isAlphabetAndSpace(String target) {
        return !TextUtils.isEmpty(target) && target.matches(ALPHA_SPACE_REGEX);
    }

    public final static boolean isNotAlphabetAndSpace(String target) {
        return !isAlphabetAndSpace(target);
    }

    public static String returnString(String text) {
        return text == null ? "" : text;
    }

    public static String returnStringForNumbersAndText(String text) {
        try {
            if (Double.parseDouble(text) == 0d) {
                return "";
            }
        } catch (NumberFormatException ne) {
            // not a number field
            // ignore the error
        }
        return TextUtils.isEmpty(text) ? "" : text;
    }

    public static String readResource(Context context, int id) throws IOException {
        InputStream is = context.getResources().openRawResource(id); //example id = R.raw.invoice
        String resourceString = null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder out = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            out.append(line);
        }
        resourceString = out.toString();
        Log.d("RESOURCE_READ", resourceString);
        reader.close();
        return resourceString;
    }

    public static ItemsDTO loadItems(Context context, int id) {
        String itemsJsonString;
        ItemsDTO items = null;
        try {
            itemsJsonString = readResource(context, id);
            items = JsonUtil.convertJsonStringToObject(itemsJsonString, ItemsDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public static CustomersDTO loadCustomers(Context context, int id) {
        String itemsJsonString;
        CustomersDTO customersDTO = null;
        try {
            itemsJsonString = readResource(context, id);
            customersDTO = JsonUtil.convertJsonStringToObject(itemsJsonString, CustomersDTO.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customersDTO;
    }
}
