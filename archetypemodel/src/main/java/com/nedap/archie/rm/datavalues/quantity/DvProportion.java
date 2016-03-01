package com.nedap.archie.rm.datavalues.quantity;

import javax.annotation.Nullable;

/**
 * TODO: This does not implement PROPORTION KIND, because multiple inheritance - won't work.
 * It does have a type=proportion kind enum
 * Created by pieter.bos on 04/11/15.
 */
public class DvProportion extends DvAmount<Double> {

    private double numerator;
    private double denominator;
    private long type;
    @Nullable
    private Long precision;

    public double getNumerator() {
        return numerator;
    }

    public void setNumerator(double numerator) {
        this.numerator = numerator;
        updateMagnitude();
    }

    private void updateMagnitude() {
        if(denominator != 0.0d) {
            setMagnitude(numerator / denominator);
        } else {
            setMagnitude(Double.MAX_VALUE);//TODO: actually: infinity. Max Double value?
        }
    }

    public double getDenominator() {
        return denominator;
    }

    public void setDenominator(double denominator) {
        this.denominator = denominator;
        updateMagnitude();
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    @Nullable
    public Long getPrecision() {
        return precision;
    }

    public void setPrecision(@Nullable Long precision) {
        this.precision = precision;
    }
}
