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
 * Map class representing warehouse layout
 * @author Vojtěch Soukenka (xsouke01)
 * @author Marián Backa (xbacka01)
 */
public class Map {

    /**
     * Hash map for coordinates for quick access.
     */
    private HashMap<Integer, Coordinate> coordinateHashMap;
    /**
     * Hash map for aisles for quick access.
     */
    private HashMap<Integer, Aisle> aisleHashMap;
    private Aisle locked = null;
    /**
     * Parking coordinate.
     */
    private Coordinate parking;
    /**
     * Departure coordinate.
     */
    private Coordinate departure;

    public List<Coordinate> loadingpoints;

    /**
     * Constructor Map.
     * Calls {@link #loadMap(File) method }.
     * @param file File to load from.
     * @throws Exception Exception while reading from file.
     */
    Map(File file) throws Exception {
        this.parking = null;
        this.departure = null;
        this.coordinateHashMap = new HashMap<>();
        this.aisleHashMap = new HashMap<>();
        this.loadingpoints = new ArrayList<>();
        loadMap(file);
    }
    public HashMap<Integer, Coordinate> getCoordinateHashmap(){
        return this.coordinateHashMap;
    }
    public HashMap<Integer, Aisle> getAisleHashMap(){
        return this.aisleHashMap;
    }

    public Coordinate getParking(){
        return this.parking;
    }
    public Coordinate getDeparture(){
        return this.departure;
    }

    /**
     * Load map from XML file.
     * @param file File to load from.
     * @throws Exception Exception while reading from file.
     */
    private void loadMap(File file) throws Exception {
        // Create XML parser
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        int parkingCoordId = -1;
        int departureCoordId = -1;
        NodeList nList;
        // get parking and departure coord ids
        nList = doc.getElementsByTagName("parking");
        Node parkingNode = nList.item(0); // only one node
        if (parkingNode.getNodeType() == Node.ELEMENT_NODE) {
            Element parkingElement = (Element) parkingNode;
            parkingCoordId = Integer.parseInt(
                    parkingElement.getAttribute("coord")
            );
        }
        nList = doc.getElementsByTagName("departure");
        Node departNode = nList.item(0); // only one node
        if (departNode.getNodeType() == Node.ELEMENT_NODE) {
            Element departElement = (Element) departNode;
            departureCoordId = Integer.parseInt(
                    departElement.getAttribute("coord")
            );
        }

        // Load coordinates
        nList = doc.getElementsByTagName("coord");

        // get all coords
        for (int i = 0; i < nList.getLength(); i++) {
            Node coordN = nList.item(i);
            // is element
            if (coordN.getNodeType() == Node.ELEMENT_NODE) {
                Element coordE = (Element) coordN;
                // get id
                int coordId = Integer.parseInt(
                        coordE.getAttribute("coordid")
                );
                // get x
                int coordX = Integer.parseInt(
                        coordE.getAttribute("x")
                );
                // get y
                int coordY = Integer.parseInt(
                        coordE.getAttribute("y")
                );
                Coordinate coord = new Coordinate(coordX, coordY, coordId);
                this.coordinateHashMap.put(coordId, coord);

                // is parking
                if (coordId == parkingCoordId){
                    this.parking = coord;
                }
                // is departure
                if (coordId == departureCoordId){
                    this.departure = coord;
                }
            }
        }

        // load aisles
        nList = doc.getElementsByTagName("aisle");

        for (int i = 0; i < nList.getLength(); i++) {
            Node aisleNode = nList.item(i);
            // aisle is element
            if ( aisleNode.getNodeType() == Node.ELEMENT_NODE) {
                Element aisleElement = (Element)  aisleNode;
                // get id
                int aisleId = Integer.parseInt(
                        aisleElement.getAttribute("aisleid")
                );
                // get coord ids
                int coord1Id = Integer.parseInt(
                        aisleElement.getAttribute("coord1")
                );
                int coord2Id = Integer.parseInt(
                        aisleElement.getAttribute("coord2")
                );
                // create aisle and put in hash map
                Aisle aisle = new Aisle(
                        aisleId,
                        this.coordinateHashMap.get(coord1Id),
                        this.coordinateHashMap.get(coord2Id)
                );
                // add aisles to its coordinates
                this.coordinateHashMap.get(coord1Id).addAisle(aisle);
                this.coordinateHashMap.get(coord2Id).addAisle(aisle);
                // put to hashmap
                this.aisleHashMap.put(aisleId, aisle);
            }
        }

        // load loadingpoints
        nList = doc.getElementsByTagName("loadingpoint");
        for (int i = 0; i < nList.getLength(); i++) {
            Node loadNode = nList.item(i);
            // loading point is element
            if ( loadNode.getNodeType() == Node.ELEMENT_NODE ) {
                Element loadElement = (Element) loadNode;
                // get aisle id
                int aisleId = Integer.parseInt(
                        loadElement.getAttribute("aisle")
                );
                // get coord id
                int coordId = Integer.parseInt(
                        loadElement.getAttribute("coord")
                );
                // add shelf ids to coordinates and add those coordinates to aisle
                Coordinate coord = null;
                NodeList nodeShelfIds = loadElement.getElementsByTagName("shelf");
                for (int n = 0; n < nodeShelfIds.getLength(); n++) {
                    Node shelfIdNode = nodeShelfIds.item(n);
                    // is element
                    if (shelfIdNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element shelfIdElement = (Element) shelfIdNode;
                        // get shelf id
                        int shelfId = Integer.parseInt(
                                shelfIdElement.getAttribute("id")
                        );
                        // add to coord
                        coord = this.coordinateHashMap.get(coordId);
                        coord.addShelf(shelfId);
                    }
                }
                if (coord != null) {
                    // add coord to aisle as loadingpoint
                    this.aisleHashMap.get(aisleId).addLoadingPoint(coord);
                    // add aisle to coord
                    coord.addAisle(this.aisleHashMap.get(aisleId));
                    // add to list of loadingpoints
                    this.loadingpoints.add(coord);
                }


            }
        }

        // find shortest paths
        mapShortestPaths();
    }

    /**
     * Map all shortest paths for every point.
     * Calculating is run in parallel.
     */
    public void mapShortestPaths() {
        // loop all coordinates
        List<Thread> threads = new ArrayList<>();
        for (Coordinate coord : coordinateHashMap.values()) {
            // create new thread to calculate Dijsktra
            Thread t = new Thread(() -> {
                // append graph to according coord and calculate
                coord.graph = new DijsktraGraph(coordinateHashMap, coord);
                coord.graph.calculate();
            });
            // add thread to list
            threads.add(t);
            // run thread function
            t.start();
        }
        // wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            }catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void lockAisle(int aisleid) {
        if (this.locked == null) {
            this.locked = this.aisleHashMap.get(aisleid);
            locked.locked = true;
            this.aisleHashMap.remove(aisleid);
        } else {
            locked.locked = false;
            aisleHashMap.put(locked.getId(), locked);
            locked = aisleHashMap.get(aisleid);
            locked.locked = true;
            aisleHashMap.remove(aisleid);
        }
        mapShortestPaths();
    }

    public void unlockAisle() {
        if(this.locked != null) {
            locked.locked = false;
            aisleHashMap.put(locked.getId(), locked);
            locked = null;
            mapShortestPaths();
        }
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("Map:\n--------------------------\n");
        ret.append("Coordinates:\n--------------------------\n");
        Iterator it = this.coordinateHashMap.entrySet().iterator();
        while(it.hasNext()){
            java.util.Map.Entry pair = (java.util.Map.Entry)it.next();
            ret.append(pair.getValue().toString());
            ret.append("\n");
        }
        ret.append("Parking coordinates: ");
        ret.append(this.parking.toString());
        ret.append("\n");
        ret.append("Departure coordinate: ");
        ret.append(this.departure.toString());
        ret.append("\n");

        ret.append("Aisles:\n--------------------------\n");
        it = this.aisleHashMap.entrySet().iterator();
        while(it.hasNext()){
            java.util.Map.Entry pair = (java.util.Map.Entry)it.next();
            ret.append(pair.getValue().toString());
            ret.append("\n");
        }

        return ret.toString();
    }
}
