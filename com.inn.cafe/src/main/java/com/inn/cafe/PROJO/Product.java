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
@Table(name="product")
public class Product implements Serializable {
    public static final Long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id ;
    @Column(name = "name")
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)//fetch.lazy cest adire chargement des donnes
    //lors quand nous avons besoins de cette objet inversement a .eager
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;
    @Column(name = "description")
    private String description;
    @Column(name="price")
    private int price;
    @Column(name = "status")
    private String status;


}
