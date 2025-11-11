package edu.univ.erp.domain;

public class Setting {
    private String keyName;
    private String value;

    public Setting(String keyname, String value){
        this.keyName = keyname;
        this.value= value;

    }

    public String getKeyName() { return keyName; }
    public void setKeyName(String keyName) { this.keyName = keyName; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
