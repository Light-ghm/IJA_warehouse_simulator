package ija_project;

import java.util.List;
/**
 * Item class representing items (stock)
 * @author Vojtěch Soukenka (xsouke01)
 * @author Marián Backa (xbacka01)
 */
public class Item {
    private String name;
    private String id;
    private String quantity;


    /**
     * Constructor Item
     *
     * @param name Item name.
     * @param id Item ID.
     * @param quantity quantity of item in stock.
     */
    public Item(String name, String id, String quantity) {
        this.name = name;
        this.id = id;
        this.quantity = quantity;
    }


    /**
     *  Get item name
     *
     * @return name as string.
     */
    public String getName(){ return this.name; }

    /**
     *  Get item ID
     *
     * @return ID as string.
     */
    public String getId(){ return this.id; }

    /**
     *  Get item quantity
     *
     * @return quantity as string.
     */
    public String getQuantity(){ return this.quantity; }
    /**
     *  Get item quantity
     *
     * @return quantity as integer.
     */
    public int getQuantityInt(){ return Integer.parseInt(this.quantity); }
    /**
     *  Set item quantity
     *
     */
    public void setQuantity(int number){ this.quantity = String.valueOf(number); }



    @Override
    public String toString() {
        return "Item name: " + this.name + "\nItem id: " + this.id + "\nItem quantity: " + this.quantity + "\n";
    }

}
