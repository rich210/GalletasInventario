package com.example.resparza.galletasinventariosv2.models;

/**
 * Created by resparza on 27/01/2016.
 */
public class MeasureType extends Object {

    private long measureTypeId;
    private String measureTypeName;
    private String measureSymbol;
    private long measureEquivalencyId;
    private int quantityEquivalency;
    private boolean isMeasureBase;


    public MeasureType() {
    }

    public MeasureType(int measureTypeId, String measureTypeName, String measureSymbol, long measureEquivalencyId, int quantityEquivalency, boolean isMeasureBase) {
        this.measureTypeId = measureTypeId;
        this.measureTypeName = measureTypeName;
        this.measureSymbol = measureSymbol;
        this.measureEquivalencyId = measureEquivalencyId;
        this.quantityEquivalency = quantityEquivalency;
        this.isMeasureBase = isMeasureBase;
    }

    public long getMeasureTypeId() {
        return measureTypeId;
    }

    public void setMeasureTypeId(long measureTypeId) {
        this.measureTypeId = measureTypeId;
    }

    public String getMeasureTypeName() {
        return measureTypeName;
    }

    public void setMeasureTypeName(String measureTypeName) {
        this.measureTypeName = measureTypeName;
    }

    public String getMeasureSymbol() {
        return measureSymbol;
    }

    public void setMeasureSymbol(String measureSymbol) {
        this.measureSymbol = measureSymbol;
    }

    public long getMeasureEquivalencyId() {
        return (isMeasureBase)?measureTypeId:measureEquivalencyId;
    }

    public void setMeasureEquivalencyId(long measureEquivalencyId) {
        this.measureEquivalencyId = measureEquivalencyId;
    }

    public boolean isMeasureBase() {
        return isMeasureBase;
    }

    public void setMeasureBase(boolean measureBase) {
        isMeasureBase = measureBase;
    }

    public int getQuantityEquivalency() {
        return quantityEquivalency;
    }

    public void setQuantityEquivalency(int quantityEquivalency) {
        this.quantityEquivalency = quantityEquivalency;
    }

    @Override
    public String toString() {
        return measureTypeName + "(" +measureSymbol+")";
    }

    public String getInfo(){
        return "Measure Type Id: "+ measureTypeId + "\n"
                + "Measure Type Name: " +measureTypeName + "\n"
                + "Measure Type Symbol: " + measureSymbol + "\n"
                + "Measure Type Equivalency Id: " + measureEquivalencyId + "\n"
                + "Quantity: " + quantityEquivalency;
    }
}
