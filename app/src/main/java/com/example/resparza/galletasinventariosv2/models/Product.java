package com.example.resparza.galletasinventariosv2.models;



/**
 * Created by resparza on 27/01/2016.
 */
public class Product {

    private long productId;
    private String productName;
    private MeasureType measureType;
    private float quantity;
    private float productMin;
    private boolean isLowerThanMin;
    private float costPerUnit;
    private float totalCost;

    public Product() {

    }

    public Product(String productName, MeasureType measureType, float quantity, float totalCost) {
        this.productName = productName;
        this.measureType = measureType;
        this.quantity = quantity;
        this.totalCost = totalCost;
    }

    public Product(int productId, String productName, MeasureType measureType, float quantity, float totalCost) {
        this.productId = productId;
        this.productName = productName;
        this.measureType = measureType;
        this.quantity = quantity;
        this.totalCost = totalCost;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public MeasureType getMeasureType() {
        return measureType;
    }

    public void setMeasureType(MeasureType measureType) {
        this.measureType = measureType;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(float costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public void setTotalCost(float quantity, float costPerUnit) {

        this.totalCost = quantity * costPerUnit;
    }

    public String getInfo() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", measureType=" + measureType +
                ", measureTypeId=" + measureType.getMeasureTypeId() +
                ", measureTypeEquivalensiId=" + measureType.getMeasureEquivalencyId() +
                ", quantity=" + quantity +
                ", productMin= "+ productMin +
                ", costPerUnit=" + costPerUnit +
                ", totalCost=" + totalCost +
                ", isLowerThanMin="+isLowerThanMin+
                '}';
    }

    public String getQuantityInfo(){

        return     "Cantidad restante: "+quantity + " " + measureType.getMeasureSymbol()+".";
    }

    public String getCostPerUnitInfo(){
        return     "Costo por unidad: "+ String.format("%.2f", getCostPerUnit())+".";
    }

    public boolean isLowerThanMin() {
        return isLowerThanMin;
    }

    public void setLowerThanMin(float quantity, float productMin) {
        if(quantity<productMin){
            isLowerThanMin = true;
        }else{
            isLowerThanMin = false;
        }
    }
    public float getProductMin() {
        return productMin;
    }

    public void setProductMin(float productMin) {
        this.productMin = productMin;
    }

}
