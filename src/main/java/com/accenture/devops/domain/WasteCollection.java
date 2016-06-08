package com.accenture.devops.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import com.accenture.devops.domain.enumeration.WasteLevel;

/**
 * A WasteCollection.
 */
@Entity
@Table(name = "waste_collection")
@Document(indexName = "wastecollection")
public class WasteCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "server_timestamp", nullable = false)
    private ZonedDateTime serverTimestamp;

    @NotNull
    @Column(name = "truck_timestamp", nullable = false)
    private ZonedDateTime truckTimestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "waste_level", nullable = false)
    private WasteLevel wasteLevel;

    @ManyToOne
    private Truck truckCode;

    @ManyToOne
    private TrashBin trashBinCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(ZonedDateTime serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    public ZonedDateTime getTruckTimestamp() {
        return truckTimestamp;
    }

    public void setTruckTimestamp(ZonedDateTime truckTimestamp) {
        this.truckTimestamp = truckTimestamp;
    }

    public WasteLevel getWasteLevel() {
        return wasteLevel;
    }

    public void setWasteLevel(WasteLevel wasteLevel) {
        this.wasteLevel = wasteLevel;
    }

    public Truck getTruckCode() {
        return truckCode;
    }

    public void setTruckCode(Truck truck) {
        this.truckCode = truck;
    }

    public TrashBin getTrashBinCode() {
        return trashBinCode;
    }

    public void setTrashBinCode(TrashBin trashBin) {
        this.trashBinCode = trashBin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WasteCollection wasteCollection = (WasteCollection) o;
        if(wasteCollection.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, wasteCollection.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "WasteCollection{" +
            "id=" + id +
            ", serverTimestamp='" + serverTimestamp + "'" +
            ", truckTimestamp='" + truckTimestamp + "'" +
            ", wasteLevel='" + wasteLevel + "'" +
            '}';
    }
}
