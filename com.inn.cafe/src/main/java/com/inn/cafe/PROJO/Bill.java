package com.inn.cafe.PROJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;



import java.io.Serializable;
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name="bill")

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "paymentMethode")
    private String paymentMethode;

    @Column(name = "total")
    private Integer total;

    @Column(name = "prductDetails",columnDefinition = "json")
    private String prductDetails;

    @Column(name = "createdBy")
    private String createdBy;



}
