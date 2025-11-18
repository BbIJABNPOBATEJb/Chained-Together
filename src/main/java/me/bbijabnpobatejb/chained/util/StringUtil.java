package me.bbijabnpobatejb.chained.util;

import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class StringUtil {

    public String formatDouble(double value, int decimals) {
        val format = "%." + decimals + "f";
        return String.format(format, value);
    }

}
