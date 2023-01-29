package ija_project;

import java.util.*;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * Data class for loading and working with lists of data.
 * @author Vojtěch Soukenka (xsouke01)
 * @author Marián Backa (xbacka01)
 */
public class Data {
    /**
     * Queue of pending orders.
     */
    private final Queue<Order> orders;
    /**
     * List of orders currently being served.
     */
    private final List<Order> ordersServed;
    /**
     * List of finished orders.
     */
    private final List<Order> ordersFinished;
    /**
     * List of loaded not yet distributed items.
     */
    public List<Item> itemList;
    /**
     * List of shelfs.
     */
    public List<Shelf> shelfList;
    private ParkingZone parkingZone;
    /**
     * Map data.
     */
    public Map map;

    public List<BigShelf> bigShelfList;

    /**
     * Constructor Data
     * Initialize lists of data.
     */
    public Data() {
        this.orders = new LinkedList<>();
        this.ordersServed = new ArrayList<>();
        this.ordersFinished = new ArrayList<>();
        this.itemList = new ArrayList<>();
        this.shelfList = new ArrayList<>();
        this.bigShelfList = new ArrayList<>();
        this.map = null;
        this.parkingZone = null;
    }

    /**
     * Loads orders from XML file and adds them to {@link #orders list}.
     *
     * @param file Load from this file.
     * @throws Exception Thrown by XML parser
     */
    public void loadOrders(File file) throws Exception {
        // Create XML parser
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        // Load orders
        NodeList nList = doc.getElementsByTagName("order");

        // get all orders
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            // order is element
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                // get order id
                String id = eElement.getAttribute("orderid");
                // get items
                NodeList nodeItems = eElement.getElementsByTagName("item");
                List<Item> items = new ArrayList<>();
                for (int n = 0; n < nodeItems.getLength(); n++) {
                    Node itemNode = nodeItems.item(n);
                    // item is element
                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element iElement = (Element) itemNode;
                        // get item id
                        String item_id = iElement.getAttribute("itemid");
                        // get quantity
                        String item_quantity = iElement.getAttribute("quantity");
                        // check if item type exists in itemList and get name
                        String item_name = getItemNameById(item_id);
                        if(item_name != null) {
                            // create new item object
                            Item item = new Item(item_name, item_id, item_quantity);
                            // add items
                            items.add(item);
                        }
                    }
                }
                // create order object and add it to list
                Order order = new Order(id, items);
                this.orders.add(order);
            }
        }
    }

    /**
     * Adds order to orderQueue
     * @param item
     * @param id
     */
    public void addOrder(Item item, String id){
        List<Item> itemik = new ArrayList<>();
        itemik.add(item);
        Order order = new Order(id, itemik);
        orders.add(order);
    }
    /**
     * Get name of Item by id.
     * @param id ID of item to get name of it.
     * @return Returns name as String or null if item doesn't exist.
     */
    public String getItemNameById (String id) {
        for (Item item : this.itemList) {
            if(item.getId().equals(id)) {
                return item.getName();
            }
        }
        return null;
    }
    /**
     * Returns item by id
     * @param id ID of item to get name of it.
     * @return Returns Item or null if item doesn't exist.
     */
    public Item getItemById (String id) {
        for(BigShelf bigS :  bigShelfList)
        {
            for(Shelf sh : bigS.getShelfsinBigshelfL()){
                for(Item it : sh.getItemsInShelf()){
                    if(it.getId().equals(id)){
                        return it;
                    }
                }
            }
            for(Shelf sh : bigS.getShelfsinBigshelfR()){
                for(Item it : sh.getItemsInShelf()){
                    if(it.getId().equals(id)){
                        return it;
                    }
                }
            }
        }

        return null;
    }
    /**
     * Loads items from XML file and adds them to {@link #itemList list}.
     *
     * @param file Load from this file.
     * @throws Exception Thrown by XML parser
     */

    public void loadItems(File file) throws Exception {
        // Create XML parser
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        // Load items
        NodeList iList = doc.getElementsByTagName("item");
        NodeList sList = doc.getElementsByTagName("shelf");
        // get all items
        for (int i = 0; i < iList.getLength(); i++) {
            Node iNode = iList.item(i);
            // item is element
            if (iNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) iNode;
                // get item id
                String id = eElement.getAttribute("itemid");
                // get name and quantity  //akoze toto este poupravit si niesom isty ze treba listy ked je tam v jednom iteme len 1 meno vzdy ale neviem teraz jak ich nahradit
                NodeList nodeNames = eElement.getElementsByTagName("name");
                NodeList nodeQuantity = eElement.getElementsByTagName("quantity");
                List<String> names = new ArrayList<>();
                List<String> quantities = new ArrayList<>();
                for (int n = 0; n < nodeNames.getLength(); n++) {
                    names.add(nodeNames.item(n).getTextContent());
                    quantities.add(nodeQuantity.item(n).getTextContent());
                }
                // create order object and add it to list
                Item item = new Item(names.get(0), id, quantities.get(0));
                this.itemList.add(item);
            }
        }

        for (int i = 0; i < sList.getLength(); i++) {
            Node sNode = sList.item(i);
            // shelf is element
            if (sNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) sNode;
                // get shelf id
                String id = eElement.getAttribute("shelfid");
                // create shelf object and add it to list
                Shelf shelf = new Shelf(id);
                this.shelfList.add(shelf);
            }
        }
    }
    public void CreateCarts(int num){
        this.parkingZone = new ParkingZone(map.getParking(), this);
        this.parkingZone.CreateCarts(num,map.getParking());
    }
    public void loadBigShelfs(File file) throws Exception {
        // Create XML parser
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        NodeList nList;
        // get parking and departure coord ids
        nList = doc.getElementsByTagName("bigshelf");
        for (int i = 0; i < nList.getLength(); ++i) {
            Node bigShelfNode = nList.item(i);
            if (bigShelfNode.getNodeType() == Node.ELEMENT_NODE) {
                Element bigShelfElement = (Element) bigShelfNode;
                String id = bigShelfElement.getAttribute("id");
                int coordX = Integer.parseInt(bigShelfElement.getAttribute("coordinatik1"));
                int coordY = Integer.parseInt(bigShelfElement.getAttribute("coordinatik2"));
                BigShelf bigShelfka = new BigShelf(id, new Coordinate(coordX,coordY,Integer.parseInt(id)));
                this.bigShelfList.add(bigShelfka);
            }
        }

    }

    public void fillBigShelfs(){
        int counter = 0;
        for(BigShelf bs : bigShelfList){
            for(int i = 0; i<10; i++){
                if(shelfList.size() > 0){
                    bs.addShelfsL(shelfList.get(counter));
                    counter++;
                }
                if(shelfList.size() > 0){
                    bs.addShelfsR(shelfList.get(counter));
                    counter++;
                }
            }
        }
    }
    /**
     * Fill shelfs.
     *
     * Takes items from  #itemList and fills them into #itemsInShelf
     * 15 is capacity of every shelf
     */
    //musim prepisat lebo je viac shelfov ako druhov itemov takze stym som akosi nepocital ked som to pisal...
    public void fillShelfs() {


        int itemsToShelf = (totalNumberOfItemsToBeDistributed() / (shelfList.size()) +2); //number of items to put into each shelf to be distributed equally
        if(itemsToShelf > 15){  //if more than 15 then warehouse capacity is smaller than number of items we want to store
            System.out.println("More items than space in shelfs!!!");
            return;
        };
    //    System.out.println("shelfList.size() value is: " + shelfList.size());
    //    System.out.println("totalNumberOfItemsToBeDistributed value is: " + totalNumberOfItemsToBeDistributed());
    //    System.out.println("itemsToShelf value is: " + itemsToShelf);
        for (Shelf shelf : shelfList) {       //fills shelfs equally more or less
            if(itemList.isEmpty() == false) {
                if (itemList.get(0).getQuantityInt() <= 15) {  //if all remaining items of this categhory fit in one shelf put them in
                    shelf.addItem(itemList.get(0));
                    itemList.remove(0);
                } else { //put recommended amount of items on shelf for equal distribution - more or less equal...
                    int totalItemQuantity = itemList.get(0).getQuantityInt();
                    Item item = new Item(itemList.get(0).getName(),itemList.get(0).getId(),String.valueOf(itemsToShelf));
                    shelf.addItem(item);
                    itemList.get(0).setQuantity(totalItemQuantity - itemsToShelf);
                }
            }
        }

    }



    /**
     * Request order from queue.
     *
     * Removes order from {@link #orders queue} and adds it to {@link #ordersServed list}.
     * @return Order or null if queue empty.
     */
    public Order requestOrder() {
        if (orders.size() == 0)
            return null;
        Order requested = orders.remove();
        ordersServed.add(requested);
        return requested;
    }

    /**
     * Finish order.
     *
     * Removes order from {@link #ordersServed list} and adds it to {@link #ordersFinished finished list}.
     * @param order Order to be finished.
     */
    public void finishOrder(Order order) {
        if (order == null)
            return;
        ordersServed.remove(order);
        ordersFinished.add(order);
    }

    /**
     * Write Shelfs.
     *
     * Returns every shelf as a String
     * @return shelfs as String
     */
    public String writeShelfs(){
        StringBuilder ret = new StringBuilder();
        for (Shelf shelf : shelfList) {
            ret.append(shelf.toString());
        }
        return ret.toString();
    }
    /**
     * totalNumberOfItemsInShelfs
     *
     * Returns total number of items in warehouse
     * @return numOfItems as integer
     */
    public int totalNumberOfItemsInShelfs(){
        int numOfItems = 0;
        for(Shelf shelf : shelfList){
            numOfItems += shelf.countOfItemsInShelf();
        }
        return numOfItems;
    }

    public ParkingZone getParkingZone() {
        return this.parkingZone;
    }

    /**
     * totalNumberOfItemsToBeDistributed
     *
     * Returns total number of items we want to store into shelfs
     * @return numOfItems as integer
     */
    public int totalNumberOfItemsToBeDistributed(){
        int numOfItems = 0;
        for(Item item : itemList){
            numOfItems += Integer.parseInt(item.getQuantity());
        }
        return numOfItems;
    }

    public String writeItems(){
        if(itemList.isEmpty() == true){
            return "No items waiting to be added to shelfs.\n";
        }
        StringBuilder ret = new StringBuilder();
        ret.append("Items waiting to be distributed to shelfs:\n");
        for (Item item : itemList) {
            ret.append(item.toString());
        }
        return ret.toString();
    }

    /**
     * Loads map from XML file.
     * @param file File to load from.
     * @throws Exception Exception while loading file.
     */
    public void loadMap(File file) throws Exception {
        this.map = new Map(file);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        // Pending orders
        ret.append("Orders:\n----------------------------\n");
        ret.append("Pending orders:\n-----\n");
        for (Order order : orders) {
            ret.append(order.toString());
        }
        ret.append("Number of pending orders: ");
        ret.append(this.orders.size());
        // Orders being served
        ret.append("\n\nOrders currently being served:\n-----\n");
        for (Order order : ordersServed) {
            ret.append(order.toString());
        }
        ret.append("Number of orders served: ");
        ret.append(this.ordersServed.size());
        // Finished orders
        ret.append("\n\nFinished orders:\n-----\n");
        for (Order order : ordersFinished) {
            ret.append(order.toString());
        }
        ret.append("Number of finished orders: ");
        ret.append(this.ordersFinished.size());

        return ret.toString();
    }
}
