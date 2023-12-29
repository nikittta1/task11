package models;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "purchase")
@Data
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "person_id")
    public Person persons;

    public Person getPersons() {
        return persons;
    }
    @ManyToOne
    @JoinColumn(name = "product_id")
    public Product products;

    public Product getProducts() {
        return products;
    }
    @Column(name = "purchase_price")
    private int purchasePrice;
    public int getPrice() {
        return purchasePrice;
    }
    @Override

    public String toString() {
        return getPersons().getName() + " купил " + getProducts().getTitle() + " за " + getProducts().getPrice() + "₽";
    }
}