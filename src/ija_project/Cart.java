package ija_project;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * Class representing carts - objects to transport items
 * @author Marián Backa (xbacka01)
 * @author Vojtech Soukenka (xsouke01)
 */
public class Cart {
    private static int maxCapacity = 15;
    private static int speed = 5;
    private static int loadtime = 50;
    private List<Item> loadedItems;
    private List<Item> orderedItems;
    private String id;
    private Coordinate coordinate;
    private Order order = null;
    private Data data;
    public Coordinate stoppedAt = null;
    private List<Coordinate> destinations = null;
    private int nextDestination = 0;
    private Coordinate currentFrom = null;
    private Coordinate currentNext = null;
    private double currentDistance = 0;
    public int currentWait = 0;
    public boolean orderFinished = false;
    private List<Coordinate> pathToNextDest = null;
    private HashMap<Integer, OrderedItem> bookedOrders = null;
    private Label label;
    private Label currentLabel = null;
    public HashMap<Integer, Label> shelfLabels = null;
    private List<Line> showedLines = null;
    public boolean goHome = false;
    private int currentCapacity = 0;
    /**
     * Constructor Cart
     *
     * @param id cart ID.
     */

    public Cart(String id, Coordinate coor, Data data) {
        this.id = id;
        this.loadedItems = null;
        this. orderedItems = null;
        this.coordinate = coor;
        this.data = data;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
    public Label getLabel(){
        return this.label;
    }


    public void loadItems(Item item, Shelf shelf){
        if(getLoadedItem(item.getName()) == null) {
            if (item.getQuantityInt() < shelf.getItem(item.getName()).getQuantityInt()) {
                this.loadedItems.add(item);
                int quantity = shelf.getItem(item.getName()).getQuantityInt();
                shelf.getItem(item.getName()).setQuantity(quantity - item.getQuantityInt());
                orderedItems.remove(item);
            } else if (item.getQuantityInt() == shelf.getItem(item.getName()).getQuantityInt()) {
                this.loadedItems.add(item);
                orderedItems.remove(item);
                shelf.getItem(item.getName()).setQuantity(0);
            } else {
                this.loadedItems.add(shelf.getItem(item.getName()));
                int quantity = getOrderedItem(item.getName()).getQuantityInt();
                getOrderedItem(item.getName()).setQuantity(quantity - shelf.getItem(item.getName()).getQuantityInt());
                shelf.getItem(item.getName()).setQuantity(0);
            }
        }else{ //pokial uz cast mame a len pridavame mnozstvo
            if (item.getQuantityInt() < shelf.getItem(item.getName()).getQuantityInt()) {
                int quantity = getLoadedItem(item.getName()).getQuantityInt();
                getLoadedItem(item.getName()).setQuantity(quantity + item.getQuantityInt());
                quantity = shelf.getItem(item.getName()).getQuantityInt();
                shelf.getItem(item.getName()).setQuantity(quantity - item.getQuantityInt());
                orderedItems.remove(item);
            } else if (item.getQuantityInt() == shelf.getItem(item.getName()).getQuantityInt()) {
                int quantity = getLoadedItem(item.getName()).getQuantityInt();
                getLoadedItem(item.getName()).setQuantity(quantity + item.getQuantityInt());
                orderedItems.remove(item);
                shelf.getItem(item.getName()).setQuantity(0);//bud dat quantitu na 0 alebo to rovno removnut neviem co je lepsie
            } else {
                int quantity = getLoadedItem(item.getName()).getQuantityInt(); //kolko uz mam nalozenych itemov
                getLoadedItem(item.getName()).setQuantity(quantity + shelf.getItem(item.getName()).getQuantityInt()); //pridam k nalozenym pocet
                getOrderedItem(item.getName()).setQuantity(quantity - shelf.getItem(item.getName()).getQuantityInt());//z order listu odoberieme pocet ktory sme nalozili
                shelf.getItem(item.getName()).setQuantity(0);//bud dat quantitu na 0 alebo to rovno removnut ked v shelfe odkial sme brali nic nenechame neviem co je lepsie
            }
        }
    }

    public Item getOrderedItem(String name){
        for(int i =0; i < this.orderedItems.size(); i++){
            if(this.orderedItems.get(i).getName().equals(name)){
                return this.orderedItems.get(i);
            }
        }
        return null;
    }

    public Item getLoadedItem(String name){
        for(int i =0; i < this.loadedItems.size(); i++){
            if(this.loadedItems.get(i).getName().equals(name)){
                return this.loadedItems.get(i);
            }
        }
        return null;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    /**
     *  @return items to be collected to cart as list of strings
     */
    public String retItemsInCartToCollect(){
        StringBuilder ret = new StringBuilder();
        ret.append("");
        if(this.orderedItems != null){
            if(this.orderedItems.size() > 0){
                for(Item item : this.orderedItems){
                    ret.append(item.toString());
                }
            }
        }else{
            ret.append("Empty");
        }


        return ret.toString();
    }

    /**
     *  @return items in cart as list of strings
     */
    public String retItemsInCartCollected(){
        StringBuilder ret = new StringBuilder();
        ret.append("");
        if(this.loadedItems != null){
            if(this.loadedItems.size() > 0){
                for(Item item : this.loadedItems){
                    ret.append(item.toString());
                }
            }
        }else{
            ret.append("Empty");
        }

        return ret.toString();
    }



    /**
     * Set first stoppedAt coordinate.
     * @param coord Starting coordinate.
     */
    public void setStart(Coordinate coord) {
        this.stoppedAt = coord;
    }

    /**
     * Assign order to cart.
     * @param order
     */
    public void assignOrder(Order order) {
        this.order = order;
        this.orderFinished = false;
        this.bookedOrders = new HashMap<>();
    }

    public Order getCurrentOrder() {
        return this.order;
    }

    /**
     * Get current position of cart in time.
     * @return Position as point
     */
    public Point getPosition(double playback_speed) {
        Point point = new Point();
        if (this.orderFinished) {
            point.x = this.stoppedAt.getX();
            point.y = this.stoppedAt.getY();
            this.orderedItems = null;
            this.loadedItems = null;
            // update label
            String toolTip = this.toString(); // cart to string vypise obsah momentalny cartu a ulozi do stringu
            this.getLabel().setTooltip(new Tooltip(toolTip));//nastavi updatnuty tooltip cartu
            return point;
        }
        // Cart was stopped
        if(this.stoppedAt != null) {
            // And the order was assigned
            if(this.order != null) {
                // get ordered destinations
                List<Item> items = this.order.getItems();
                this.orderedItems = items;
                // update label
                String toolTip = this.toString(); // cart to string vypise obsah momentalny cartu a ulozi do stringu
                this.getLabel().setTooltip(new Tooltip(toolTip));//nastavi updatnuty tooltip cartu

                this.destinations = preparePath(this.stoppedAt, items);
                // set index of item in destinations as next
                this.nextDestination = 1;
                // get path to next destination
                this.pathToNextDest = this.stoppedAt.graph.getShortestPathTo(destinations.get(this.nextDestination));
                this.currentFrom = this.pathToNextDest.get(0);
                this.currentNext = this.pathToNextDest.get(1);
                // set stoppedAt as null
                this.stoppedAt = null;
            } else if (goHome) {
                this.destinations = new ArrayList<>();
                this.destinations.add(this.stoppedAt);
                this.destinations.add(data.map.getParking());
                this.nextDestination = 1;
                this.pathToNextDest = this.stoppedAt.graph.getShortestPathTo(destinations.get(nextDestination));
//                System.out.println(pathToNextDest);
                this.currentFrom = this.pathToNextDest.get(0);
                this.currentNext = this.pathToNextDest.get(1);
                this.goHome = false;
                this.stoppedAt = null;
                this.orderedItems = null;
                this.loadedItems = null;
                // update label
                String toolTip = this.toString(); // cart to string vypise obsah momentalny cartu a ulozi do stringu
                this.getLabel().setTooltip(new Tooltip(toolTip));//nastavi updatnuty tooltip cartu
            } else {
                // No order so cart is staying at the stopped position.
                point.x = this.stoppedAt.getX();
                point.y = this.stoppedAt.getY();
                return point;
            }
        }
        // travel in time
        Coordinate from = this.currentFrom;
        Coordinate next = this.currentNext;
        // get distance from point to next point
        double distance = next.distanceFrom(from);
        double step  = speed * playback_speed; // get size of step
        // IF WAITING FOR LOADING
        if (this.currentWait > 0) {
            if (this.currentWait >= step) {
                this.currentWait -= step;
                step = 0;
            } else {
                step = step - this.currentWait;
                this.currentWait = 0;
            }
        }
        if (this.currentWait == 0 && this.currentLabel != null) {
            currentLabel.setBackground(new Background(new BackgroundFill(Color.rgb(255, 153, 51, 1), new CornerRadii(0), new Insets(0))));
            currentLabel = null;
        }
        // next step is over next point
        if ((this.currentDistance + step) > distance) {
            while((this.currentDistance + step) > distance) {
                // get size of overstep
                double overstep = (this.currentDistance + step) - distance;
                this.currentDistance = overstep;
                // is next point the destination?
                if (next.getId().equals(destinations.get(this.nextDestination).getId())) {
                    // LOADING TIME
                    if (overstep >= loadtime) {
                        this.currentDistance = overstep - loadtime;
                        this.currentWait = 0;
                    } else {
                        this.currentDistance = 0;
                        this.currentWait = (int)(loadtime - overstep);
                    }
//                    System.out.println("DESTINATION: " + destinations.get(this.nextDestination));
                    Coordinate currentDest = destinations.get(this.nextDestination);
                    this.nextDestination++;
                    // DEPARTURE
                    if(currentDest.getId() == data.map.getDeparture().getId()) {
                        this.currentCapacity = 0;
                        this.loadedItems = new ArrayList<>();
                    }
                    // is final destination?
                    if (this.nextDestination == destinations.size()) {
                        point.x = currentDest.getX();
                        point.y = currentDest.getY();
                        this.stoppedAt = currentDest;
                        this.orderFinished = true;
//                        System.out.println("FINAL");
                        return point;
                    }
                    List<Integer> availableShelfs = currentDest.getShelfs();
//                    OrderedItem desired = this.bookedOrders.get(currentDest.getId());
                    OrderedItem desired = null;
                    int shelfId = -1;
                    for (Integer sId : availableShelfs) {
                        desired = this.bookedOrders.get(sId);
                        if (desired != null) {
                            shelfId = sId;
                            break;
                        }
                    }
                    if (desired != null) {
//                        System.out.println("DESIRED AMOUNT: " + desired.count);
                        // HIGHLIGHT LABEL
                        if(this.currentWait > 0 && this.shelfLabels != null) {
                            if(currentLabel != null)
                                currentLabel.setBackground(new Background(new BackgroundFill(Color.rgb(255, 153, 51, 1), new CornerRadii(0), new Insets(0))));
                            currentLabel = this.shelfLabels.get(Integer.parseInt(desired.shelf.getId()));
                            if(currentLabel != null)
                                currentLabel.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(0))));
                        }
                        // TODO load cargo
                        // How much to take
                        int take;
                        if(currentCapacity + desired.count > maxCapacity) {
                            int over = currentCapacity + desired.count - maxCapacity;
                            take = desired.count - over;
                        } else {
                            take = desired.count;
                        }
                        // add to loaded items
                        desired.count -= take;
                        if(desired.count == 0)
                            this.bookedOrders.remove(shelfId);
                        Item item = new Item(desired.item.getName(), desired.item.getId(), Integer.toString(take));
                        if (loadedItems == null)
                            loadedItems = new ArrayList<>();
                        this.loadedItems.add(item);
                        // remove booked
                        desired.shelf.removeBooked(item.getName(), take);

                        // update labels
//                        System.out.println(this.toString());
                        String toolTip = this.toString(); // cart to string vypise obsah momentalny cartu a ulozi do stringu
                        this.getLabel().setTooltip(new Tooltip(toolTip));//nastavi updatnuty tooltip cartu

                        String toolTipS = desired.shelf.toString(); // cart to string vypise obsah momentalny shelfu a ulozi do stringu
                        desired.shelf.getLabel().setTooltip(new Tooltip(toolTipS));//nastavi updatnuty tooltip shelfu
                    }
                    // get new path to next dest
                    this.pathToNextDest = currentDest.graph.getShortestPathTo(destinations.get(this.nextDestination));
                } else {
                    this.pathToNextDest = next.graph.getShortestPathTo(destinations.get(this.nextDestination));
                }
                // Can't get next destination due to lock street
                // Skip to next destination and call again
                if (this.pathToNextDest == null) {
                    System.out.println("SKIPPP");
                    System.out.println(destinations);
                    this.nextDestination++;
                    this.currentFrom = this.currentNext;
                    return getPosition(playback_speed);
                }
                // update points
                if (this.pathToNextDest.size() == 1) {
                    this.currentFrom = this.pathToNextDest.get(0);
                    this.currentNext = this.pathToNextDest.get(0);
                } else {
                    this.currentFrom = this.pathToNextDest.get(0);
                    this.currentNext = this.pathToNextDest.get(1);
                }
                from = this.currentFrom;
                next = this.currentNext;
                distance = next.distanceFrom(from);
            }
        } else {
            this.currentDistance += step;
        }
        // calculate current points
        point.x = (int)(from.getX() + ((next.getX() - from.getX()) / distance) * this.currentDistance);
        point.y = (int)(from.getY() + ((next.getY() - from.getY()) / distance) * this.currentDistance);
//        System.out.println("From id:" + from.getId());
//        System.out.println("Next id:" + next.getId());
//        System.out.println("Current distance:" + this.currentDistance);
        return point;
    }

    private List<Coordinate> preparePath(Coordinate from, List<Item> items) {
        List<Coordinate> destinations = new ArrayList<>();
        destinations.add(from);
        List<Coordinate> bufferList = new ArrayList<>();
        Coordinate current = from;
        int quantityOfAll = 0;
        for (Item item : items) {
            List<Coordinate> shelfsOfItem = findShelfs(item);
            // Over capacity
            if ((item.getQuantityInt() + quantityOfAll) > maxCapacity) {
                for(Coordinate stop : shelfsOfItem) {
                    OrderedItem book = this.bookedOrders.get(stop.getId());
                    if (book.count + quantityOfAll > maxCapacity) {
                        bufferList.add(stop);
                        break;
                    } else {
                        bufferList.add(stop);
                        shelfsOfItem.remove(stop);
                    }
                }
                // get path
                List<Coordinate> ordered = current.graph.createPath(bufferList);
                // add departure
                ordered.add(data.map.getDeparture());
                // set current to be departure
                current = data.map.getDeparture();
                // append to destinations
                destinations.addAll(ordered);
                // empty buffer
                bufferList = new ArrayList<>();
            }
            // add to list
            bufferList.addAll(shelfsOfItem);
        }

        if(bufferList.size() > 0) {
            // get path
            List<Coordinate> ordered = current.graph.createPath(bufferList);
            // add departure
            ordered.add(data.map.getDeparture());
            // append to destinations
            destinations.addAll(ordered);
        }

        return destinations;
    }

    private List<Coordinate> findShelfs(Item item) {
        List<Coordinate>  shelfs = new ArrayList<>();
        int quantity = item.getQuantityInt(); // get required quantity
        for (Shelf shelf : this.data.shelfList) { // loop shelfs
            Item get = shelf.getItem(item.getName());
            // has wanted item
            if (get != null) {
                int taken_quant;
                // amount in shelf is lower than wanted quantity
                if (get.getQuantityInt() < quantity) {
                    quantity = quantity - get.getQuantityInt();
                    taken_quant = get.getQuantityInt();
                    shelf.bookItem(get.getName(), get.getQuantityInt());
                } else { // is enough
                    taken_quant = quantity;
                    shelf.bookItem(get.getName(), quantity);
                    quantity = 0;
                }
                // add coord to shelfs;
                Coordinate shelfCoord = findShelfCoord(shelf);
                shelfs.add(shelfCoord);
                OrderedItem save = new OrderedItem(shelf, shelfCoord, taken_quant, item);
                this.bookedOrders.put(Integer.parseInt(shelf.getId()), save);
                // check quantity left
                if (quantity == 0) {
                    break;
                }
            }
        }

        return shelfs;
    }

    private Coordinate findShelfCoord(Shelf shelf) {
        int id = Integer.parseInt(shelf.getId());
        for (Coordinate coord : this.data.map.loadingpoints) {
            List<Integer> shelfids = coord.getShelfs();
            if (shelfids.contains(id)) {
                return coord;
            }
        }
        return null;
    }

    public void showPath(Parent root) {
        if(destinations == null)
            return;
        this.showedLines = new ArrayList<>();
        for (int i = 1; i < destinations.size(); i++) {
            Coordinate from = destinations.get(i-1);
            Coordinate to = destinations.get(i);
//            Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
//            line.setFill(Color.BLUE);
//            ((Pane) root).getChildren().add(line);
//            this.showedLines.add(line);
            List<Coordinate> points = from.graph.getShortestPathTo(to);
            if(points == null)
                continue;
            for(int k = 1; k < points.size(); k++) {
                Coordinate point1 = points.get(k-1);
                Coordinate point2 = points.get(k);

                Line line = new Line(point1.getX(), point1.getY(), point2.getX(), point2.getY());
                line.setStroke(Color.rgb(20, 20, 250, 0.40));
                line.setStrokeWidth(1);
                ((Pane) root).getChildren().add(line);
                this.showedLines.add(line);
            }
        }
    }

    public void hidePath(Parent root) {
        if(this.showedLines == null)
            return;
        for(Line line : this.showedLines) {
            Pane group = (Pane) line.getParent();
            group.getChildren().remove(line);
        }
        this.showedLines = null;
    }

    @Override
    public String toString() {
        return "Cart id: " + this.id + "\nCart max capacity: " + this.maxCapacity + "\nItems to be collected: \n" + this.retItemsInCartToCollect() + "\nItems in cart: \n"+ this.retItemsInCartCollected();
    }
}

class OrderedItem {
    public Shelf shelf;
    public Item item;
    public int count;
    public Coordinate loadingpoint;
    public OrderedItem(Shelf shelf, Coordinate loadingpoint, int count, Item item) {
        this.shelf = shelf;
        this.loadingpoint = loadingpoint;
        this.count = count;
        this.item = item;
    }
}