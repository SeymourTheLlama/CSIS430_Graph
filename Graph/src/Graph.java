import java.io.IOException;
import java.util.*;

/**
 * Simple templated graph with some basic functionality, such as adding and
 * removing edges or vertices. Works with non-negative weights for edges
 * between vertices.
 *
 * @param <T>
 */
public class Graph<T> {
    private HashMap<T, HashMap<T, Integer>> _graph;
    private boolean _isDirected;

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
        final int NONEXISTENT_EDGE = -1;
        int returnWeight = NONEXISTENT_EDGE;

        // get the edge's weight if it exists. Since undirected graphs only
        // store one side
        if (edgeExists(source, destination)) {
            returnWeight = _graph.get(source).get(destination);
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

        // check to ensure that both vertices exist
        if (_graph.containsKey(source) && _graph.containsKey(destination)) {
            // if there is an edge from source to destination, the edge exists
            if (_graph.get(source).containsKey(destination)) {
                edgeExists = true;
            }
        }
        return edgeExists;
    }


    /**
     * Add a vertex to the graph. This new vertex will be unconnected to
     * every edge. Nulls are not accepted as new vertices. Redundant adds
     * will be rejected.
     *
     * @param vertex Object of type T to serve as a vertex
     * @throws IllegalArgumentException if a null is provided as the vertex,
     * or if a duplicate vertex is added.
     */
    public void addVertex(T vertex) throws IllegalArgumentException {
        // if the vertex is null, throw an exception
        if (vertex == null) {
            throw new IllegalArgumentException();
        }
        // if the graph already contains the vertex, throw an exception
        else if (_graph.containsKey(vertex)) {
            throw new IllegalArgumentException();
        }

        // add vertex to the first level of hashMap
        _graph.put(vertex, new HashMap<>());
    }


    /**
     * Remove the given vertex from the graph. If there are edges connected
     * to this vertex, they are also removed.
     *
     * @param vertex Object of type T to remove from graph
     * @throws NoSuchElementException if the vertex is not found in the graph
     */
    public void removeVertex(T vertex) throws NoSuchElementException {
        // if vertex not in graph, throw exception
        if (!_graph.containsKey(vertex)) {
            throw new NoSuchElementException();
        }

        // check every vertex to see if it has an edge going to this vertex
        for (T source : _graph.keySet()) {
            // checks the vertex's hashMap of edges to see if any connect
            // to the vertex we are removing
            if (_graph.get(source).containsKey(vertex)) {
                // removes the edge
                removeEdge(source, vertex);
            }
        }

        // remove the vertex itself, along with any edges originating
        // from it
        _graph.remove(vertex);
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

        // if this would be a loop or the source or destination is null,
        // throw an exception
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
            if (!_isDirected) {
                _graph.get(destination).put(source, weight);
            }
        }
        // if the edge does exist, update the weight
        else {
            // update the source->destination edge, vice-versa as well if
            // undirected
            _graph.get(source).replace(destination, weight);
            if (!_isDirected) {
                _graph.get(destination).replace(source, weight);
            }
        }
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

        // if the graph doesn't contain the source, destination, or edge
        // between them, throw an exception
        if (!_graph.containsKey(source) || !_graph.containsKey(destination)
                || !edgeExists(source, destination)) {
            throw new NoSuchElementException();
        }

        // remove destination vertex from source in base hashMap. If
        // undirected, check the other direction to remove that one as well
        _graph.get(source).remove(destination);
        if (!_isDirected) {
            _graph.get(destination).remove(source);
        }
    }


    /**
     * Returns the length of the given set of vertices if they are a path
     * through the graph. If they are not a path or the list an empty, then
     * this returns -1.
     *
     * @param pathList
     * @return
     */
    public long pathLength(List<T> pathList) {
        return 0;
    }


    /**
     * Finds the shortest path between the source and the destination
     * vertices. Returns null if no path exists.
     *
     * @param source
     * @param destination
     * @return
     * @throws NoSuchElementException
     */
    public List<Edge<T>> shortestPathBetween(T source, T destination) throws NoSuchElementException {
        return null;
    }


    /**
     *
     * @param isDirected
     * @param inputFile
     * @return
     * @throws IOException
     */
    public static Graph<String> fromCSVFile(boolean isDirected, Scanner inputFile) throws IOException {
        return null;
    }


    /**
     * String representation of the graph.
     *
     * @return representation of graph in string form
     */
    public String toString() {
        return _graph.toString();
    }


    public static class Edge<E> {
        private E _source;
        private E _destination;
        private int _weight;

        /**
         * Constructs an edge.
         *
         * @param source source vertex of the edge
         * @param destination destination vertex of the edge
         * @param weight weight of the edge
         */
        public Edge(E source, E destination, int weight) {
            _source = source;
            _destination = destination;
            _weight = weight;
        }


        /**
         * returns the weight of the edge.
         *
         * @return weight of the edge.
         */
        public int getWeight() {
            return _weight;
        }


        /**
         * returns the source vertex of the edge.
         *
         * @return the source vertex of the edge.
         */
        public E getSource() {
            return _source;
        }


        /**
         * returns the destination vertex of the edge.
         *
         * @return destination vertex of the edge.
         */
        public E getDestination() {
            return _destination;
        }


        /**
         * string representation of the edge
         *
         * @return a string representation of the edge
         */
        public String toString() {
            return String.format("<%s, %s;  %d>",
                    _source.toString(), _destination.toString(), _weight);
        }
    }
}