package ija_project;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * Class representing group of shelfs (10 and 10 from each side)
 * @author Marián Backa (xbacka01)
 * @author Vojtech Soukenka (xsouke01)
 */
public class BigShelf {
    private String id;
    private List<Shelf> shelfsinBigshelfL = new ArrayList<>();;
    private List<Shelf> shelfsinBigshelfR = new ArrayList<>();;
    private Coordinate coordinate;

    /**
     * Constructor BigShelf
     *
     * @param id BigShelf ID.
     * @param coord BigShelf coordinate.
     */
    public BigShelf(String id, Coordinate coord) {
        this.id = id;
        this.coordinate = coord;
    }

    public List<Shelf> getShelfsinBigshelfL() {
        return this.shelfsinBigshelfL;
    }

    public List<Shelf> getShelfsinBigshelfR() {
        return this.shelfsinBigshelfR;
    }

    public Coordinate getCoordinate() {
        return this.coordinate;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void addShelfsL(Shelf shelf) {
        this.shelfsinBigshelfL.add(shelf);
    }

    public void addShelfsR(Shelf shelf) {
        this.shelfsinBigshelfR.add(shelf);
    }

}