package cz.vutbr.fit.nodbpersistence.core.testclasses;

import cz.vutbr.fit.nodbpersistence.annotations.ObjectId;

import java.net.URI;
import java.util.Date;
import java.util.Locale;

public class Test3 {

    @ObjectId
    private Long objectId;

    private CharSequence charSequence;
    private Number number;
    private Date date;
    private URI uri;
    private Locale locale;

    public CharSequence getCharSequence() {
        return charSequence;
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
