package ija_project;
import java.util.*;
import javafx.util.Pair;

/**
 * DijsktraGraph Class for creating graph of shortest paths from point
 * based on Dijsktra's algorithm.
 * @author Vojtech Soukenka (xsouke01)
 * @author Marián Backa (xbacka01)
 */
public class DijsktraGraph {
    private Set<Integer> settled; // settled vertices
    private HashMap<Integer, Coordinate> coordinateHashMap; // vertices
    private HashMap<Integer, Integer> dist;
    private HashMap<Integer, Integer> previous;
    private int V; // Number of vertices
    private Coordinate start;

    public DijsktraGraph(HashMap<Integer, Coordinate> vertices, Coordinate start) {
        this.coordinateHashMap = vertices;
        this.V = vertices.size();
        this.dist = new HashMap<>();
        this.previous = new HashMap<>();
        this.settled = new HashSet<>();
        this.start = start;
    }

    /**
     * Calculate Dijkstra's algorithm.
     */
    public void calculate() {
        // init distances
        for (int id : coordinateHashMap.keySet()) {
            dist.put(id,Integer.MAX_VALUE);
        }
        // set distance for starting vertice as 0
        dist.put(start.getId(), 0);
        previous.put(start.getId(), -1);

        HashMap<Integer, Integer> unsettled = new HashMap<>();

        unsettled.put(start.getId(), 0);
        while(settled.size() != V) {
            // get shortest
            Coordinate u = getLowestDistanceNode(unsettled);
            // unavailable points left;
            if( u == null)
                break;
            // add current node
            settled.add(u.getId());
            //remove from unsettled
            unsettled.remove(u.getId());
            // get neighbours
            List<Coordinate> neighbours = u.getAllNeighbors();
            // unavailable point
            if(neighbours.isEmpty())
                break;
            for (Coordinate neighbour : neighbours) {
                // not settled yet
                if (!settled.contains(neighbour.getId())) {
                    // get distance
                    double edgeDist = neighbour.distanceFrom(u);
                    int newDist = dist.get(u.getId()) + (int) edgeDist;
                    // Update distance if is shorter
                    if (newDist < dist.get(neighbour.getId())) {
                        dist.put(neighbour.getId(), newDist);
                        previous.put(neighbour.getId(), u.getId()); // set previous coord
                    }

                    // add to unsettled
                    unsettled.put(neighbour.getId(), dist.get(neighbour.getId()));
                }
            }
        }
    }

    /**
     * Find next unsettled coordinate which is the closest.
     * @param unsettled Unsettled coordinates.
     * @return Closest coordinate from unsettled.
     */
    private Coordinate getLowestDistanceNode(HashMap<Integer, Integer> unsettled) {
        Coordinate lowestDistanceCoord = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (HashMap.Entry<Integer, Integer> entry : unsettled.entrySet()) {
             int distance = entry.getValue();
             if(distance < lowestDistance) {
                 lowestDistance = distance;
                 lowestDistanceCoord = coordinateHashMap.get(entry.getKey());
             }
        }
        return lowestDistanceCoord;
    }

    /**
     * Get shortes path to coordinate.
     * @param coord Destination
     * @return List of coordinates creating path to destination.
     */
    public List<Coordinate> getShortestPathTo(Coordinate coord) {
        // create new list
        List<Coordinate> path = new ArrayList<>();
        // get current coordinates
        if(coord == null)
            return null;
        int current = coord.getId();
        if(previous.get(current) == null)
            return null;
        // search from end to start
        while(current != -1) {
            path.add(coordinateHashMap.get(current));
            if(previous == null) {
                current = -1;
            } else {
                current = previous.get(current);
            }
        }
        // reverse order
        Collections.reverse(path);
        return path;
    }

    public int getDistanceTo(Coordinate coord) {
        return dist.get(coord.getId());
    }

    /**
     * Simple TSP solution based on nearest neighbour algorithm.
     * @param destinations All desired destinations
     * @return
     */
    public List<Coordinate> createPath(List <Coordinate> destinations) {
        List<Coordinate> visited = new ArrayList<>();
        List<Coordinate> unvisited = new ArrayList<>(destinations);
        Coordinate current = this.start;
        while(visited.size() < destinations.size()) {
            Pair<Integer, Integer> shortest = new Pair<>(-1, Integer.MAX_VALUE);
            for (Coordinate neighbour : unvisited) {
                int distance = current.graph.getDistanceTo(neighbour);
                if (distance < shortest.getValue()) {
                    shortest = new Pair<>(neighbour.getId(), distance);
                }
            }
            current = coordinateHashMap.get(shortest.getKey());

            unvisited.remove(current);
            visited.add(current);
        }

        return visited;
    }
}
