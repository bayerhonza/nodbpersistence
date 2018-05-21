package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;


public class Test1ClassPrimitives {

    @ObjectId
    public Long objectId;

    private Boolean aBooleanWrap;
    private boolean aBoolean;

    private Byte aByteWrap;
    private byte aByte;

    private Short aShortWrap;
    private short aShort;

    private Integer anIntWrap;
    private int anInt;

    private long aLong;
    private Long aLongWrap;

    private float aFloat;
    private Float aFloatWrap;

    private double aDouble;
    private Double aDoubleWrap;

    private String aString;

    public Boolean getaBooleanWrap() {
        return aBooleanWrap;
    }

    public void setaBooleanWrap(Boolean aBooleanWrap) {
        this.aBooleanWrap = aBooleanWrap;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public Byte getaByteWrap() {
        return aByteWrap;
    }

    public void setaByteWrap(Byte aByteWrap) {
        this.aByteWrap = aByteWrap;
    }

    public byte getaByte() {
        return aByte;
    }

    public void setaByte(byte aByte) {
        this.aByte = aByte;
    }

    public Short getaShortWrap() {
        return aShortWrap;
    }

    public void setaShortWrap(Short aShortWrap) {
        this.aShortWrap = aShortWrap;
    }

    public short getaShort() {
        return aShort;
    }

    public void setaShort(short aShort) {
        this.aShort = aShort;
    }

    public Integer getAnIntWrap() {
        return anIntWrap;
    }

    public void setAnIntWrap(Integer anIntWrap) {
        this.anIntWrap = anIntWrap;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    public long getaLong() {
        return aLong;
    }

    public void setaLong(long aLong) {
        this.aLong = aLong;
    }

    public Long getaLongWrap() {
        return aLongWrap;
    }

    public void setaLongWrap(Long aLongWrap) {
        this.aLongWrap = aLongWrap;
    }

    public float getaFloat() {
        return aFloat;
    }

    public void setaFloat(float aFloat) {
        this.aFloat = aFloat;
    }

    public Float getaFloatWrap() {
        return aFloatWrap;
    }

    public void setaFloatWrap(Float aFloatWrap) {
        this.aFloatWrap = aFloatWrap;
    }

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public Double getaDoubleWrap() {
        return aDoubleWrap;
    }

    public void setaDoubleWrap(Double aDoubleWrap) {
        this.aDoubleWrap = aDoubleWrap;
    }

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }
}
