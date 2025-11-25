package com.jargo.app.models;

/**
 * Field model - Chuyên ngành (IT, Medical, Economics)
 */
public class Field {
    private String fieldId;
    private String name;
    private String icon;
    private String description;

    // Constructor rỗng
    public Field() {
    }

    // Constructor đầy đủ
    public Field(String fieldId, String name, String icon, String description) {
        this.fieldId = fieldId;
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    // Getters
    public String getFieldId() {
        return fieldId;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
