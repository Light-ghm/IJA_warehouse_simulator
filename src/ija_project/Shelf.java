package ija_project;

import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

/**
 * Shelf class representing shelfs (umiestenie itemov)
 * @author Vojtěch Soukenka (xsouke01)
 * @author Marián Backa (xbacka01)
 */
public class Shelf {
    private String id;
    private List<Item> itemsInShelf;
    private List<Item> bookedItems;
    private static int capacity = 15;
    private Coordinate  coordinate;
    private Label label;

    /**
     * Constructor Shelf
     *
     * @param id shelf ID.
     */
    public Shelf(String id) {
        this.id = id;
        this.itemsInShelf = new ArrayList<>();
        this.bookedItems = new ArrayList<>();
    }
    /**
     *  Get shelf ID
     *
     * @return ID as string.
     */
    public String getId(){ return this.id; }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setLabel(Label labelka){
        this.label = labelka;
    }

    public Label getLabel() {
        return this.label;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * return item from shelf found by name
     * @param name searched items name
     * @return Item
     */
    public Item getItem(String name){
        for(int i =0; i < this.itemsInShelf.size(); i++){
            if(this.itemsInShelf.get(i).getName().equals(name)){
               return this.itemsInShelf.get(i);
            }
        }
        return null;
    }
    /**
     *  Add item to shelf
     *
     *  @param item object.
     *  adds item to #itemsInShelf
     */
    public void addItem(Item item){
        this.itemsInShelf.add(item);
    }

    /**
     *  Removes item from shelf
     *
     *  @param name of item.
     *  removes item from #itemsInShelf
     */
    public void removeItem(String name){  //not tested for now but probably works
        for(int i =0; i < this.itemsInShelf.size(); i++){
            if(this.itemsInShelf.get(i).getName().equals(name)){
                this.itemsInShelf.remove(i);
            }
        }
    }
    public List<Item> getItemsInShelf(){
        return this.itemsInShelf;
    }

    /**
     *  @return items in shelf as list of strings
     */
    public String retItemsInShelf(){
        StringBuilder ret = new StringBuilder();
        for(Item item : this.itemsInShelf){
                ret.append(item.toString());
        }
        return ret.toString();
    }

    /**
     *  @return total number of all items in shelf
     */

    public int countOfItemsInShelf(){
        int count = 0;
        for(Item item : this.itemsInShelf){
            count += Integer.parseInt(item.getQuantity());
        }
        return count;
    }

    /**
     * @param itemname is name of searched item
     *  @return total number of a item in shelf
     */

    public int numOfItemInShelf(String itemname){
        int count = 0;
        for(Item item : this.itemsInShelf){
            if(item.getName().equals(itemname)) {
                count += Integer.parseInt(item.getQuantity());
            }
        }
        return count;
    }

    public int getCapacity(){
        return capacity;
    }

    public void bookItem(String name, int amount) {
        Item item = getItem(name);
        item.setQuantity(item.getQuantityInt() - amount);

        Item found = isInBooked(name);
        if (found == null) {
            Item booked = new Item(item.getName(), item.getId(), String.valueOf(amount));
            this.bookedItems.add(booked);
        } else {
            found.setQuantity(found.getQuantityInt() + amount);
        }
    }

    private Item isInBooked(String name) {
        for (Item item : this.bookedItems) {
            if (item.getName() == name) {
                return item;
            }
        }
        return null;
    }

    public void removeBooked(String name, int amount) {
        Item item = isInBooked(name);
        if (item == null)
            return;
        item.setQuantity(item.getQuantityInt() - amount);
    }

    @Override
    public String toString() {
        return "Shelf id: " + this.id + "\n\nItems in this shelf: \n" + this.retItemsInShelf() + "\n";
    }
}
