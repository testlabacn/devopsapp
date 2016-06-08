package com.accenture.devops.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TrashBin.
 */
@Entity
@Table(name = "trash_bin")
@Document(indexName = "trashbin")
public class TrashBin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "trash_bin_code", nullable = false)
    private String trashBinCode;

    @NotNull
    @Column(name = "latitute", nullable = false)
    private Double latitute;

    @NotNull
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @NotNull
    @Lob
    @Column(name = "bar_code", nullable = false)
    private byte[] barCode;

    @Column(name = "bar_code_content_type", nullable = false)
    private String barCodeContentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrashBinCode() {
        return trashBinCode;
    }

    public void setTrashBinCode(String trashBinCode) {
        this.trashBinCode = trashBinCode;
    }

    public Double getLatitute() {
        return latitute;
    }

    public void setLatitute(Double latitute) {
        this.latitute = latitute;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public byte[] getBarCode() {
        return barCode;
    }

    public void setBarCode(byte[] barCode) {
        this.barCode = barCode;
    }

    public String getBarCodeContentType() {
        return barCodeContentType;
    }

    public void setBarCodeContentType(String barCodeContentType) {
        this.barCodeContentType = barCodeContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrashBin trashBin = (TrashBin) o;
        if(trashBin.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, trashBin.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TrashBin{" +
            "id=" + id +
            ", trashBinCode='" + trashBinCode + "'" +
            ", latitute='" + latitute + "'" +
            ", longitude='" + longitude + "'" +
            ", barCode='" + barCode + "'" +
            ", barCodeContentType='" + barCodeContentType + "'" +
            '}';
    }
}
