package models;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="title")
    private String title;
    @Column(name = "price")
    private int price;
    @OneToMany(mappedBy = "products")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Purchase> purchases;
}