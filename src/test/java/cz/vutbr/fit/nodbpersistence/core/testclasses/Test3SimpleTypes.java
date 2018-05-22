package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

import java.net.URI;
import java.util.Date;
import java.util.Locale;

public class Test3SimpleTypes {

    @ObjectId
    private Long objectId;

    private Test3Enum1 enum1;
    private CharSequence charSequence;
    private Number number;
    private Date date;
    private URI uri;
    private Locale locale;

    public CharSequence getCharSequence() {
        return charSequence;
    }

    public Test3Enum1 getEnum1() {
        return enum1;
    }

    public void setEnum1(Test3Enum1 enum1) {
        this.enum1 = enum1;
    }

    public void setCharSequence(CharSequence charSequence) {
        this.charSequence = charSequence;
    }

    public Number getNumber() {
        return number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
