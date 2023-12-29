package models;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "persons")
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "persons")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Purchase> purchases;
    public List<Purchase> getPurchases() {
        return purchases;
    }
    @Override
    public String toString() {
        List<Purchase> purchases = getPurchases();
        if (!purchases.isEmpty()) {
            Map<String, Integer> productTotalAmounts = new HashMap<>();

            for (Purchase p : purchases) {
                String productTitle = p.getProducts().getTitle();
                if (productTitle != null) {
                    int purchaseAmount = productTotalAmounts.getOrDefault(productTitle, 0);
                    purchaseAmount += p.getPurchasePrice();
                    productTotalAmounts.put(productTitle, purchaseAmount);
                }
            }

            StringBuilder result = new StringBuilder("Список товаров, который покупал(а) " + getName() + ":\n");
            for (Map.Entry<String, Integer> entry : productTotalAmounts.entrySet()) {
                result.append(entry.getKey()).append(": Общая сумма - ").append(entry.getValue()).append("\n");
            }
            return result.toString();
        } else {
            return "Данный покупатель ничего не приобретал.";
        }
    }
}
