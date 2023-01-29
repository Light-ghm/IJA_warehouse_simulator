package ija_project;

import java.util.List;

/**
 * Order class representing orders (systém požadavků)
 * @author Vojtěch Soukenka (xsouke01)
 * @author Marián Backa (xbacka01)
 */
public class Order {
    private List<Item> items;
    private String id;

    /**
     * Constructor Order
     * @param id Order ID.
     * @param items List of items.
     */
    public Order(String id, List<Item> items) {
        this.id = id;
        this.items = items;
    }

    /**
     *  Get order ID
     * @return ID as string.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Get list of products
     * @return products as list of strings
     */
    public List<Item> getItems() {
        return this.items;
    }

    @Override
    public String toString() {
        return "Order id: " + this.id + "\nList of items:\n" + items + "\n";
    }
}
