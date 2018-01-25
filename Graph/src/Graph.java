import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Simple templated graph with some basic functionality, such as adding and
 * removing edges or vertices. Works with non-negative weights for edges
 * between vertices.
 *
 * @param <T>
 */
public class Graph<T> {
    // private variables
    HashMap<T, HashMap<T, Integer>> _graph;
    boolean _isDirected;

    /**
     * Constructs either a directed or undirected graph. If the graph is
     * directed, then creating an edge only connects the source to the
     * destination, not vice-versa. If it is undirected, then effectively two
     * edges are created for every one, connecting the source to the
     * destination as well as vice-versa.
     *
     * @param isDirected
     */
    public Graph(boolean isDirected) {
        _graph = new HashMap<>();
        _isDirected = isDirected;
    }


    /**
     * Returns a sequence of the graph's vertices.
     *
     * @return a sequence of the graph's vertices.
     */
    public List<T> getVertices() {
        return new ArrayList<>(_graph.keySet());
    }


    /**
     * Returns the weight of the edge that goes from the given source vertex
     * to the given destination vertex, or -1 if no such edge is present.
     *
     * @param source vertex that the given edge begins from
     * @param destination vertex that the given edge ends at
     * @return integer weight of the given edge, or -1 if it does not exist
     */
    public int getEdgeWeight(T source, T destination) {
        int returnWeight = -1;

        if (edgeExists(source, destination)) {
            if (_isDirected || _graph.get(source).containsKey(destination)) {
                returnWeight = _graph.get(source).get(destination);
            }
            else {
                returnWeight = _graph.get(destination).get(source);
            }
        }

        return returnWeight;
    }


    /**
     * Returns true if the edge exists, false if not.
     *
     * @param source vertex that the given edge begins from
     * @param destination vertex that the given edge ends at
     * @return boolean representing existence
     */
    public boolean edgeExists(T source, T destination) {
        boolean edgeExists = false;

        // CHECK BOTH DIRECTIONS IF UNDIRECTED, otherwise normal

        // both vertices exist
        if (_graph.containsKey(source) && _graph.containsKey(destination)) {
            // if there is an edge from source to destination, the edge exists
            if (_graph.get(source).containsKey(destination)) {
                edgeExists = true;
            }
            // if there is an edge from destination to source and the graph
            // is undirected, the edge exists
            else {
                if (_graph.get(destination).containsKey(source) &&
                        !_isDirected) {
                    edgeExists = true;
                }
            }
        }

//        if (_graph.containsKey(source) && _graph.containsKey(destination)) {
//            // edge exists
//            if (_graph.get(source).containsKey(destination)) {
//                // edge isn't a reference
//                if (getEdgeWeight(source, destination) > 0) {
//                    edgeExists = true;
//                }
//            }
//        }

        return edgeExists;
    }


    /**
     * Add a vertex to the graph. This new vertex will be unconnected to
     * every edge. Nulls are not accepted as new vertices.
     *
     * @param vertex Object of type T to serve as a vertex
     * @throws IllegalArgumentException if a null is provided as the vertex,
     * or if a duplicate vertex is added.
     */
    public void addVertex(T vertex) throws IllegalArgumentException {
        if (vertex == null) {
            throw new IllegalArgumentException();
        }
        else if (_graph.containsKey(vertex)) {
            throw new IllegalArgumentException();
        }

        // add vertex to the uber-hashMap
        _graph.put(vertex, new HashMap<>());
        // it will sit there with an empty hashmap, b/c it has no edges
    }


    /**
     * Remove the given vertex from the graph. If there are edges connected
     * to this vertex, they are also removed.
     *
     * @param vertex Object of type T to remove from graph
     * @throws NoSuchElementException if the vertex is not found in the graph
     */
    public void removeVertex(T vertex) throws NoSuchElementException {
        // remove the vertex, with all its edges (can't do otherwise, really)
        // go through every vertex and remove the vertex from their interior
        // hashmaps
        if (!_graph.containsKey(vertex)) {
            throw new NoSuchElementException();
        }
        else {
            // check every vertex to see if it has an edge going to this vertex
            for (T source : _graph.keySet()) {
                // checks the vertex's hashMap of edges to see if any connect
                // to the vertex we are removing
                if (_graph.get(source).containsKey(vertex)) {
                    // removes the edge
                    removeEdge(source, vertex);
                }
            }
//
//
//            // trace back from the edges that are connected to this vertex
//            for (T destination : _graph.get(vertex).keySet()) {
//                // remove the edges that connect to the vertex
//                removeEdge(destination, vertex);
//            }

            // remove the vertex itself, along with any edges originating
            // from it
            _graph.remove(vertex);
        }
    }


    /**
     * Add an edge to the graph from the source vertex to the destination
     * with the given weight. If this is an undirected graph, than another
     * edge is effectively created between the destination and the source. If
     * a duplicate edge is added with a different weight, the new weight
     * replaces the old.
     *
     * @param source vertex that the edge to add begins from.
     * @param destination vertex that the edge to add ends at
     * @param weight weight of the edge
     * @throws IllegalArgumentException if the weight is less than zero.
     * @throws NoSuchElementException if one or more vertices are not found
     */
    public void addEdge(T source, T destination, int weight)
            throws IllegalArgumentException, NoSuchElementException {

        // if the weight is less than one or this would be a loop, throw an
        // exception
        if (source == null || destination == null
                || source.equals(destination)) {
            throw new IllegalArgumentException();
        }
        else if (!_graph.containsKey(source) ||
                !_graph.containsKey(destination)) {
            throw new NoSuchElementException();
        }

        // if the edge does not yet exist
        if (!edgeExists(source, destination)) {
            _graph.get(source).put(destination, weight);
        }
        // if the edge does exist, update the weight
        else {
            // if the graph is undirected, we must check both this edge and
            // the other to update the correct weight
            if (!_isDirected) {
                if  (_graph.get(source).containsKey(destination)) {
                    _graph.get(source).replace(destination, weight);
                }
                else {
                    _graph.get(destination).replace(source, weight);
                }
            }
            else {
                _graph.get(source).replace(destination, weight);
            }
        }

//        if (!edgeExists(source, destination)) {
//            // add an entry to the edge hashMap corresponding to the source
//            // vertex
//            _graph.get(source).put(destination, weight);
//
//            if (!edgeExists(destination, source)) {
//                if (_isDirected) {
//                    // add an entry to the destination's hashMap to show that there
//                    // is an edge connecting to it
//                    _graph.get(destination).put(source, -1);
//                } else {
//                    // undirected graphs require duplicates from destination to source
//                    _graph.get(destination).put(source, weight);
//                }
//            }
//        }
//        else {
//            // update the weight
//            _graph.get(source).replace(destination, weight);
//
//            // if undirected, the weight must be updated from destination to
//            // source as well.
//            if (!_isDirected) {
//                _graph.get(destination).replace(source, weight);
//            }
//        }
    }


    /**
     * Removes the edge from the source vertex to the destination. If this is
     * an undirected graph, then the corresponding edge from the destination
     * to the source is also effectively removed.
     *
     * @param source vertex that the edge to remove begins from.
     * @param destination vertex that the edge to add ends at.
     * @throws NoSuchElementException if one or more vertices are not found
     */
    public void removeEdge(T source, T destination) throws
            NoSuchElementException {

        if (!_graph.containsKey(source) || !_graph.containsKey(destination)
                || !edgeExists(source, destination)) {
            throw new NoSuchElementException();
        }

        // remove destination vertex from source in base hashMap. Vice-versa
        // as well if undirected.
        if (_isDirected || _graph.get(source).containsKey(destination)) {
            _graph.get(source).remove(destination);
        }
        else {
            _graph.get(destination).remove(source);
        }
    }


    /**
     * String representation of the graph.
     *
     * @return representation of graph in string form
     */
    public String toString() {
        // will always be O(n) unless HashMap has super fancy functions
        return null;
    }
}