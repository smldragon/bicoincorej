package com.sbt.utils;

import java.text.DecimalFormat;
import java.text.ParseException;

public class Amount extends Number {

    private final DecimalFormat formatter = new DecimalFormat(SbtConstants.AmountFormatString);
    private final Double value;
    public Amount(String sValue) throws ParseException {
        value = formatter.parse(sValue).doubleValue();
    }
    public Amount(int iValue) {
        value = new Double(iValue);
    }
    public Amount(float fValue) {
        value = new Double(fValue);
    }
    public Amount(double dValue) {
        value = dValue;
    }
    public Amount(long lValue) {
        value = new Double(lValue);
    }
    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }
}
