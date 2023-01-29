package ija_project;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math.*;

/**
 * Class representing coordinate [x,y]
 * @author Marián Backa (xbacka01)
 * @author Vojtech Soukenka (xsouke01)
 */
public class Coordinate {

    /**
     * Position on X axis.
     */
    private final int x;

    /**
     * Position on Y axis.
     */
    private final int y;

    /**
     * Coordinate id.
     */
    private final int id;

    /**
     * List of aisles containing this coordinate.
     */
    private List<Aisle> aisleList;

    /**
     * Shelf ids of shelfs accessible from coordinate
     */
    private List<Integer> accessToShelfs;

    /**
     * Graph of shortest paths for this coordinate.
     */
    public DijsktraGraph graph = null;

    /**
     * Constructor Coordinate
     * @param x X position
     * @param y Y position
     * @param id ID of coordinate.
     */
    Coordinate(int x, int y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.accessToShelfs = new ArrayList<>();
        this.aisleList = new ArrayList<>();
    }

    /**
     * Add aisle to {@link #aisleList list}.
     * @param aisle Aisle which is added.
     */
    public void addAisle(Aisle aisle) {
        this.aisleList.add(aisle);
    }

    /**
     * Add shelf accessible from coordinate to {@link #accessToShelfs list}.
     * @param id ID of shelf.
     */
    public void addShelf(int id) {
        this.accessToShelfs.add(id);
    }

    /**
     * Get shelf ids.
     * @return  List of shelf ids.
     */
    public List<Integer> getShelfs() {
        return this.accessToShelfs;
    }

    public List<Coordinate> getAllNeighbors() {
        List<Coordinate> retList = new ArrayList<>();
        for (Aisle aisle : this.aisleList) {
            if(!aisle.locked)
                retList.addAll(aisle.getAllNeighborsOf(this));
        }
        return retList;
    }

    /**
     * Calculate distrance from coordinate.
     * @param from Coordinate from the distance is calculated.
     * @return Distance.
     */
    public double distanceFrom(Coordinate from) {
        int fromX = from.getX();
        int fromY = from.getY();
        return Math.sqrt((this.x - fromX)*(this.x - fromX) + (this.y - fromY)*(this.y - fromY));
    }

    /**
     * Get id
     * @return id
     */
    public Integer getId() {
        return this.id;
    }

    /**
     * Get X position
     * @return X
     */
    public Integer getX() {
        return this.x;
    }

    /**
     * Get Y position
     * @return Y
     */
    public Integer getY() {
        return this.y;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        String part = "ID: " + this.id + " [" + this.x + ", " + this.y + "];";
        ret.append(part);
        if (!this.accessToShelfs.isEmpty()) {
            ret.append(" This is loading point for shelfs: ");
            ret.append(this.accessToShelfs);
            ret.append(";");
        }
        if (!this.aisleList.isEmpty()) {
            ret.append(" Endpoint for aisles: [");
            for (Aisle aisle : this.aisleList) {
                ret.append(aisle.getId());
                ret.append(";");
            }
            ret.append("]");
        }
        return ret.toString();
    }
}
