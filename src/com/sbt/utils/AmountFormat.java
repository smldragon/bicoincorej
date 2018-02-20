package com.sbt.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;

public class AmountFormat extends DecimalFormat {

    public AmountFormat(String pattern) {
        super(pattern);
    }
    @Override
    public Amount parse(String text, ParsePosition pos) {
        return  new Amount(super.parse(text,pos).doubleValue());
    }
    @Override
    public Amount parse(String text) throws ParseException {
        return  new Amount(super.parse(text).doubleValue());
    }
}
