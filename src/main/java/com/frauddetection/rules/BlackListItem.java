package com.frauddetection.rules;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class BlackListItem {
    private final StringProperty id = new SimpleStringProperty();
    private final IntegerProperty alertCount = new SimpleIntegerProperty();
    private final ObjectProperty<java.sql.Date> lastAlertDate = new SimpleObjectProperty<>();
    private final StringProperty lastAlertDescription = new SimpleStringProperty();

    // Constructeur
    public BlackListItem(String id, int alertCount, java.sql.Date lastAlertDate, String lastAlertDescription) {
        this.id.set(id);
        this.alertCount.set(alertCount);
        this.lastAlertDate.set(lastAlertDate);
        this.lastAlertDescription.set(lastAlertDescription);
    }

    // Getter et setter pour id
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    // Getter et setter pour alertCount
    public int getAlertCount() {
        return alertCount.get();
    }

    public void setAlertCount(int alertCount) {
        this.alertCount.set(alertCount);
    }

    public IntegerProperty alertCountProperty() {
        return alertCount;
    }

    // Getter et setter pour lastAlertDate
    public java.sql.Date getLastAlertDate() {
        return lastAlertDate.get();
    }

    public void setLastAlertDate(java.sql.Date lastAlertDate) {
        this.lastAlertDate.set(lastAlertDate);
    }

    public ObjectProperty<java.sql.Date> lastAlertDateProperty() {
        return lastAlertDate;
    }

    // Getter et setter pour lastAlertDescription
    public String getLastAlertDescription() {
        return lastAlertDescription.get();
    }

    public void setLastAlertDescription(String lastAlertDescription) {
        this.lastAlertDescription.set(lastAlertDescription);
    }

    public StringProperty lastAlertDescriptionProperty() {
        return lastAlertDescription;
    }

    @Override
    public String toString() {
        return "BlacklistEntry{" +
                "id='" + id.get() + '\'' +
                ", alertCount=" + alertCount.get() +
                ", lastAlertDate=" + lastAlertDate.get() +
                ", lastAlertDescription='" + lastAlertDescription.get() + '\'' +
                '}';
    }
}