import models.Person;
import models.Product;
import models.Purchase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.*;

public class MainApp {
    public static void main(String[] args) {
        System.out.println("/showProductsByPerson <name> -  посмотреть какие товары купил покупатель\n" +
                "/findPersonByProductTitle <title>\n" +
                "/removeProduct <title> - удалить продукт\n" +
                "/removePerson <name> - удалить пользователя\n" +
                "/updatePrice <title> <price> - обновить цену товара\n" +
                "/buy <name> <title> - покупка товара\n" +
                "/quit - выйти\n");
        SessionFactory factory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Product.class)
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Purchase.class)
                .buildSessionFactory();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String str = scanner.nextLine();
                Session session = factory.getCurrentSession();
                switch (str.split(" ")[0]) {
                    case "/help":
                        System.out.println("/showProductsByPerson <name> -  посмотреть какие товары купил покупатель\n" +
                                "/findPersonByProductTitle <title> - посмотреть кто купил данный товар\n" +
                                "/removeProduct <title> - удалить продукт\n" +
                                "/removePerson <name> - удалить пользователя\n" +
                                "/updatePrice <title> <price> - обновить цену товара\n" +
                                "/buy <name> <title> - покупка товара\n" +
                                "/quit - выйти\n");
                        break;
                    case "/quit":
                        System.exit(0);
                        session.close();
                        factory.close();
                        break;
                    case "/showProductsByPerson":
                        show(factory, Integer.parseInt(remove(str,"/showProductsByPerson ")));
                        break;
                    case "/findPersonByProductTitle":
                        find(factory, remove(str, "/findPersonByProductTitle "));
                        break;
                    case "/removePerson":
                        removePerson(factory, remove(str, "/removePerson "));
                        break;
                    case "/removeProduct":
                        removeProduct(factory, remove(str, "/removeProduct "));
                        break;
                    case "/buy":
                        buy(factory, remove(str, "/buy "));
                        break;
                    case "/updatePrice":
                        String[] params = remove(str, "/updatePrice ").split(" ");
                        if (params.length == 2) {
                            String productTitle = params[0];
                            int newPrice = Integer.parseInt(params[1]);
                            updatePrice(factory, productTitle, newPrice);
                        } else {
                            System.out.println("Неверное количество параметров для команды /updatePrice");
                        }
                        break;
                    default:
                        System.out.println("Введена неверная команда!");
                        break;
                }

                session.close();
            }
        } finally {
            factory.close();
        }
    }

    public static String remove(String text, String target) {
        return text.replace(target, "");
    }

    private static void show(SessionFactory factory, int personId) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Person person = session.get(Person.class, personId);
            if (person != null) {
                System.out.println(person);
            } else {
                System.out.println("Покупатель с id " + personId + " не найден.");
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






//    private static void show(SessionFactory factory, String sh) {
//        try (Session session = factory.getCurrentSession()) {
//            session.beginTransaction();
//            List<Purchase> purchases = session.createQuery(
//                            "from Purchase p where p.persons.name = :name", Purchase.class)
//                    .setParameter("name", sh)
//                    .getResultList();
//            String resultMessage = purchases.isEmpty()
//                    ? "Нет купленных товаров для пользователя " + sh
//                    : "Купленные товары для пользователя " + sh + ":\n" + purchases;
//            System.out.println(resultMessage);
//            session.getTransaction().commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private static void find(SessionFactory factory, String fd) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query<Purchase> q = session.createQuery(
                            "from Purchase p where p.products.title = :title", Purchase.class)
                    .setParameter("title", fd);

            List<Purchase> purchases = q.list();
            if (purchases.isEmpty()) {
                System.out.println("Нет информации о покупках товара " + fd);
                return;
            }
            System.out.println("Информация о покупках товара " + fd + " по каждому покупателю:");

            for (Purchase purchase : purchases) {
                String personName = purchase.getPersons().getName();
                int purchasePrice = purchase.getPurchasePrice();
                System.out.println(personName + " купил товар '" + fd + "' за " + purchasePrice + "₽");
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






    private static void removeProduct(SessionFactory factory, String productTitle) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query<Product> productQuery = session.createQuery(
                            "from Product where title = :title", Product.class)
                    .setParameter("title", productTitle);
            Product product = productQuery.uniqueResult();
            if (product != null) {
                Query q = session.createQuery("delete from Purchase where products.id = :productId");
                q.setParameter("productId", product.getId());
                q.executeUpdate();
                session.delete(product);
                System.out.println("Товар '" + productTitle + "' успешно удален.");
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void removePerson(SessionFactory factory, String personName) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query q = session.createQuery("delete Person where name = :paramName");
            q.setParameter("paramName", personName);
            q.executeUpdate();
            System.out.println("Пользователь '" + personName + "' успешно удален.");
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void buy(SessionFactory factory, String g) {
        String[] a = g.split(" ");
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Person p0 = null;
            Product p1 = null;
            List<Person> persons = session.createQuery("from Person where name = :name", Person.class)
                    .setParameter("name", a[0])
                    .getResultList();
            if (!persons.isEmpty()) {
                p0 = persons.get(0);
            }
            List<Product> products = session.createQuery("from Product where title = :title", Product.class)
                    .setParameter("title", a[1])
                    .getResultList();
            if (!products.isEmpty()) {
                p1 = products.get(0);
            }
            if (p0 != null && p1 != null) {
                Purchase purchase = new Purchase();
                purchase.setPersons(p0);
                purchase.setProducts(p1);
                if (a.length > 2) {
                    int purchasePrice = Integer.parseInt(a[2]);
                    purchase.setPurchasePrice(purchasePrice);
                } else {
                    purchase.setPurchasePrice(p1.getPrice());
                }
                session.save(purchase);
            } else {
                System.out.println("Покупатель или продукт не найден.");
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updatePrice(SessionFactory factory, String productTitle, int newPrice) {
        try (Session session = factory.getCurrentSession()) {
            session.beginTransaction();
            Query<Product> query = session.createQuery(
                            "from Product p where p.title = :title", Product.class)
                    .setParameter("title", productTitle);
            List<Product> products = query.list();
            if (!products.isEmpty()) {
                Product product = products.get(0);
                product.setPrice(newPrice);
                session.update(product);
                System.out.println("Цена товара '" + productTitle + "' успешно обновлена.");
            } else {
                System.out.println("Товар с названием '" + productTitle + "' не найден.");
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

