package ija_project;

import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
/**
 * Class representing aisles - paths in warehouse
 * @author Marián Backa (xbacka01)
 * @author Vojtech Soukenka (xsouke01)
 */
public class Aisle {
    /** ID of aisle */
    private int id;
    /** Aisle coordinate */
    private Coordinate coordinate1;
    /** Aisle coordinate */
    private Coordinate coordinate2;
    /** Coordinates for loading points to access shelfs */
    private List<Coordinate> loadingpoints;
    /**Graphic representation in GUI */
    private Line line;
    public boolean locked = false;
    /**
     * Constructor Aisle
     * @param coordinate1 Coordinate.
     * @param coordinate2 Coordinate.
     */
    Aisle(int id, Coordinate coordinate1, Coordinate coordinate2) {
        this.id = id;
        this.coordinate1 = coordinate1;
        this.coordinate2 = coordinate2;
        this.loadingpoints = new ArrayList<>();
    }
    
    public List<Coordinate> getAllNeighborsOf(Coordinate coord) {
        List<Coordinate> retList = new ArrayList<>();
        if (coord.getId().equals(this.coordinate1.getId())) {
            retList.add(this.coordinate2);
            retList.addAll(this.loadingpoints);
        } else if (coord.getId().equals(this.coordinate2.getId())) {
            retList.add(this.coordinate1);
            retList.addAll(this.loadingpoints);
        } else {
            // coordinate might me one of the loading points
            List<Coordinate> otherpoints = new ArrayList<>();
            boolean found = false;
            for (Coordinate loadpoint : this.loadingpoints) {
                if (loadpoint.getId().equals(coord.getId())) {
                    found = true;
                } else {
                    otherpoints.add(loadpoint);
                }
            }
            if(found) {
                retList.add(this.coordinate1);
                retList.add(this.coordinate2);
                retList.addAll(otherpoints);
            } else {
                return null;
            }
        }
        return retList;
    }

    /**
     * Add loading points.
     * @param point Coordinates of point that is added.
     */
    public void addLoadingPoint(Coordinate point) {
        this.loadingpoints.add(point);
    }

    /**
     * Get id.
     * @return ID
     */
    public int getId() {
        return this.id;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Coordinate getCoordinate1() {
        return this.coordinate1;
    }
    public Coordinate getCoordinate2(){return this.coordinate2;}

    public List<Coordinate> getLoadingpoints() {
        return this.loadingpoints;
    }

    @Override
    public String toString(){
        StringBuilder ret = new StringBuilder();
        ret.append("ID: ");
        ret.append(this.id);
        ret.append(" {[");
        ret.append(coordinate1.getX());
        ret.append(", ");
        ret.append(coordinate1.getY());
        ret.append("], [");
        ret.append(coordinate2.getX());
        ret.append(", ");
        ret.append(coordinate2.getY());
        ret.append("]}");
        if(!this.loadingpoints.isEmpty()) {
            ret.append(" Has loading points: [");
            for (Coordinate coord : this.loadingpoints) {
                ret.append(coord.getId());
                ret.append(";");
            }
            ret.append("]");
        }
        return ret.toString();
    }
}
