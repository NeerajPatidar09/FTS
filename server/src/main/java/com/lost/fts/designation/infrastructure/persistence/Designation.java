package com.lost.fts.designation.infrastructure.persistence;

import jakarta.persistence.*;

@Entity
@Table(name = "designations")
public class Designation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "designation_code",
            nullable = false, unique = true)
    private String designationCode;

    @Column(name = "designation_name",
            nullable = false, unique = true)
    private String designationName;

    private boolean active = true;

    public Designation() {}

    public Long getId() {
        return id;
    }

    public String getDesignationCode() {
        return designationCode;
    }

    public void setDesignationCode(String designationCode) {
        this.designationCode = designationCode;
    }

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}