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
     * Factory method that constructs and populates a graph from the given
     * input file. The file is assumed to have the following format:
     *
     * <# of vertices>
     * <first vertex>
     * <second vertex>
     * ...
     * <nth vertex>
     * <# of edges>
     * <source>,<destination>,<weight>
     * <source>,<destination>,<weight>
     * ...
     *
     * such as:
     *
     * 3
     * v1
     * v2
     * v3
     * 2
     * v1,v3,5
     * v2,v1,1
     *
     * @param isDirected if the graph to be constructed should be directed or
     *                  not
     * @param inputFile the file to construct a graph from. it should be
     *                  formatted as shown in the main javadoc.
     * @return graph constructed as per the CSV file's contents
     * @throws IOException if an error is detected with the user's file
     */
    public static Graph<String> fromCSVFile(boolean isDirected,
                                            Scanner inputFile)
                                                throws IOException {
        if (inputFile == null) {
            throw new IOException();
        }

        Scanner lineReader;
        int numVertices;
        int numEdges ;
        Graph<String> csvGraph = new Graph<>(isDirected);

        try {
            // add vertices
            // first value in the list should be the number of vertices
            numVertices = inputFile.nextInt();
            inputFile.nextLine();

            // iterate over the number of vertices in the graph
            for (int i = 0; i < numVertices; i++) {
                csvGraph.addVertex(inputFile.nextLine());
            }

            // add edges
            // next value should be the number of edges
            if (inputFile.hasNextInt()) {
                numEdges = inputFile.nextInt();
                inputFile.nextLine();

                // iterate over the number of vertices in the graph
                for (int i = 0; i < numEdges; i++) {
                    lineReader = new Scanner(inputFile.nextLine());
                    lineReader.useDelimiter(",");
                    csvGraph.addEdge(lineReader.next(), lineReader.next(),
                            lineReader.nextInt());
                }
            }
            // if it has more elements but not a starting int, then they have
            // more vertices that they said they would.
            else if (inputFile.hasNext()) {
                throw new IOException();
            }
        }
        catch (NumberFormatException | ClassCastException |
                NoSuchElementException e) {
            throw new IOException();
        }

        return csvGraph;
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
     * Returns the edge from the given edge than the given destination.
     *
     * @param source source of the edge to return
     * @param destination destination of the edge to return
     * @return null if no such edge exists, otherwise returns an edge object
     */
    public Edge<T> getEdge(T source, T destination) {
        Edge<T> returnEdge = null;

        // if the edge exists, create an edge from the source and destination
        // given.
        if (edgeExists(source, destination)) {
            returnEdge = new Edge<>(source, destination,
                    getEdgeWeight(source, destination));
        }

        // returns null if no edge exists
        return returnEdge;
    }


    /**
     * Returns a list of all the edges in the graph. For an undirected graph,
     * this only returns one direction instead of both.
     *
     * @return list of the edges in the graph.
     */
    public List<Edge<T>> getEdges() {
        // DIRECTED
        // for every source vertex
        // for every destination vertex
        // append source, dest, weight as Edge

        // UNDIRECTED
        // for every source vertex
        // for every destination vertex
        // if not already added
        // - edgeList.contains -- time O(n) space O(1)
        // - hashSet addedEdges.contains -- time O(1) space O(n)++
        // append source, dest, weight as Edge

        HashSet<Edge<T>> edgeList = new HashSet<>();

        // for every source vertex
        for (T source : _graph.keySet()) {
            // for every destination vertex
            for (T dest : _graph.get(source).keySet()) {
                // if this is a directed graph or this edge's reverse hasn't
                // been added already
                if (_isDirected || !edgeList.contains(new Edge<>(dest, source,
                        getEdgeWeight(dest, source)))) {
                    // add edge to edge list
                    edgeList.add(new Edge<>(source, dest,
                            getEdgeWeight(source, dest)));
                }
            }
        }

        // now turn that hashSet into a List
        return new ArrayList<>(edgeList);
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


    // EDIT!!!!
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
                || source == destination) {
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


    // EDIT!!!!
    /**
     * Returns the length of the given set of vertices if they are a path
     * through the graph. If they are not a path or the list is empty, then
     * this returns -1.
     *
     * @param pathList list of vertices
     * @return -1 for an empty list or a nonexistent path, length of path
     * otherwise
     */
    public long pathLength(List<T> pathList) {
        final long NO_PATH_EXISTS = -1;
        long length = NO_PATH_EXISTS;
        boolean isPath = true;
        int pathIndex = 0;
        T currentVertex = null;
        T lastVertex = null;

        // for each element in the path list
        while (isPath && pathIndex < pathList.size()) {
            currentVertex = pathList.get(pathIndex);

            // if the vertex does not exist in the graph
            if (!_graph.containsKey(currentVertex)) {
                isPath = false;
                length = -1;
            }
            // if this is the first element, set length to 0
            else if (pathList.get(0) == currentVertex) {
                length = 0;
            }
            // if an edge exists from the last vertex to this one, add its
            // weight to the length
            else if (_graph.get(lastVertex).containsKey(currentVertex)) {
                length += getEdgeWeight(lastVertex, currentVertex);
            }
            else {
                isPath = false;
                length = -1;
            }

            pathIndex++;
            lastVertex = currentVertex;
        }


        return length;
    }

    // EDIT EDIT EDIT!!!!!!!!!!!!!
    /**
     * Finds the shortest path between the source and the destination
     * vertices. Returns null if no path exists.
     *
     * @param source vertex to try to find the shortest path from
     * @param destination vertex to try to find the shortest path to
     * @return null if no path exists, otherwise list of edges that
     * constitute the shortest path from the given source to the given
     * destination
     * @throws NoSuchElementException
     */
    public List<Edge<T>> shortestPathBetween(T source, T destination)
            throws NoSuchElementException {
        // set up variables
        List<Edge<T>> shortestPath = new ArrayList<>();
        HashMap<T, Edge<T>> connectedElements = new HashMap<>();
        PriorityQueue<Edge<T>> frontier = new PriorityQueue<>();
        T currentVertex = null;
        Edge<T> currentEdge = new Edge<>(null, source, 0);
        int pathWeight;

        // if source and destination vertices not in graph, throw exception
        if (!_graph.containsKey(source) || !_graph.containsKey(destination)) {
            throw new NoSuchElementException();
        }


        // otherwise, start with the source and begin finding neighbors.
        connectedElements.put(source, currentEdge);
        currentVertex = source;

        // graph has been searched if:
            // the frontier is empty after we've gone through once
            // the frontier only contains vertices that already exist in the
                // connected elements list
        // destination has been found if
            // source == destination
            // destination is in connected elements

        if (source != destination) {
            do {
                // add every edge connected to the recent new addition to the
                // connectedElements set
                for (T vertex : _graph.get(currentVertex).keySet()) {
                    if (!connectedElements.containsKey(vertex)) {
                        // add the weight of the nearest edge added to the weight
                        // of the path taken to reach this point
                        pathWeight = getEdgeWeight(currentVertex, vertex) +
                                connectedElements.get(currentVertex).getWeight();

                        // add the edge to the frontier with the correct weight
                        frontier.offer(new Edge<>(currentVertex, vertex,
                                pathWeight));
                    }
                }

                // the edge with the least total pathWeight is the next to add to
                // connectedVertices
                do {
                        // define it as the next edge to work with
                        currentEdge = frontier.poll();
                }
                while (!frontier.isEmpty() && connectedElements.containsKey(
                        frontier.peek().getDestination()));

                // add the edge and its destination vertex to the connected elements
                currentVertex = currentEdge.getDestination();
                connectedElements.put(currentVertex, currentEdge);
            }
            while (!frontier.isEmpty() &&
                    !connectedElements.containsKey(destination));


            // when we first hit this, the current vertex will be the destination
            // and the current edge will be the path taken to get there.
            while (currentEdge.getSource() != null &&
                    connectedElements.containsKey(destination)) {
                // add the current edge to shortest path, but with the proper
                // weight from the graph itself.
                shortestPath.add(new Edge<>(currentEdge.getSource(), currentVertex,
                        getEdgeWeight(currentEdge.getSource(), currentVertex)));

                // reset current edge as the edge connecting to it
                currentVertex = currentEdge.getSource();
                currentEdge = connectedElements.get(currentVertex);
            }

            Collections.reverse(shortestPath);

            // if no destination was ever found, then return null
            if (!connectedElements.containsKey(destination)) {
                shortestPath = null;
            }
        }

        return shortestPath;
    }


    /**
     * Returns the minimum spanning tree of this graph, if undirected. If
     * directed, it throws an IllegalStateException. This uses Prim's
     * Algorithm, and as such is better for dense graphs. Returns null if no
     * spanning tree exists.
     *
     * @return a graph representation of a minimum spanning tree of this
     * graph. null if no spanning tree exists
     * @throws IllegalStateException if this graph is directed.
     */
    public Graph<T> minimumSpanningTree() throws IllegalStateException {
        // PRIM
        // minSpanTree graph contains first vertex
        // priority queue contains every edge from every vertex in minSpanTree
        // repeat |V| - 1 times
            // poll priority queue, getting the next nearest edge
                // if no edge found, this is an unconnected graph
            // add the destination vertex to the minSpanTree
            // add all edges that go from the destination vertex to vertices
                // not already in the minSpanTree to the priority queue

        // cannot be directed
        if (_isDirected) {
            throw new IllegalStateException();
        }

        Graph<T> minSpanTree = new Graph<>(false);
        PriorityQueue<Edge<T>> edgeQueue = new PriorityQueue<>();
        T currentVertex = null;
        Edge<T> currentEdge = null;
        boolean graphUnconnected = false;

        // if the graph isn't empty
        if (_graph.size() > 0) {
            // pick a vertex to add to the min span tree
            currentVertex = getVertices().get(0);
            // add that vertex to the minimum spanning tree
            minSpanTree.addVertex(currentVertex);

            // for as many vertices as there are in the graph
            for (int i = 0; i < _graph.keySet().size(); i++) {

                // for every edge connected to the most recently added vertex
                for (T destination : _graph.get(currentVertex).keySet()) {
                    // if the destination of the edge coming from the current
                    // vertex is not already in the minimum spanning tree
                    if (!minSpanTree.vertexExists(destination)) {
                        // add the edge from the most recently added vertex to a
                        // vertex not in the minSpanTree to the priority queue.
                        edgeQueue.offer(new Edge<>(currentVertex, destination,
                                getEdgeWeight(currentVertex, destination)));
                    }
                }

                // the next edge to add to the minimum spanning tree is the edge
                // currently connected to the min span tree by only one vertex
                // that also has the lowest weight.

                while (!edgeQueue.isEmpty() && !minSpanTree.vertexExists(
                            edgeQueue.peek().getDestination())) {
                    currentEdge = edgeQueue.poll();
                }


                //if (currentEdge != null) {
                if (currentEdge != null && !minSpanTree.vertexExists(
                        currentEdge.getDestination())) {
                    // add this edge to the min span tree
                    minSpanTree.addVertex(currentEdge.getDestination());
                    minSpanTree.addEdge(currentEdge);

                    // the most recently added vertex to the min span tree is now the
                    // destination of the recently added edge.
                    currentVertex = currentEdge.getDestination();
                }
                else {
                    graphUnconnected = true;
                }
            }
        }

        if (graphUnconnected) {
            minSpanTree = null;
        }

        return minSpanTree;
    }


    /**
     * String representation of the graph.
     *
     * @return representation of graph in string form
     */
    public String toString() {
        return "Directed: " + _isDirected + " " + _graph.toString();
    }


    /**
     * Returns true if the vertex is in the graph, false otherwise.
     *
     * @param vertex vertex to check existence of
     * @return true if vertex is in the graph, false otherwise.
     */
    private boolean vertexExists(T vertex) {
        return _graph.containsKey(vertex);
    }


    /**
     * Overloads addEdge to work with the Edge class
     *
     * @param edgeToAdd Edge<T> to add
     * @throws IllegalArgumentException when null is given as the edge to add
     */
    private void addEdge(Edge<T> edgeToAdd) throws IllegalArgumentException{
        if (edgeToAdd == null) {
            throw new IllegalArgumentException();
        }
        else {
            addEdge(edgeToAdd.getSource(), edgeToAdd.getDestination(),
                    edgeToAdd.getWeight());
        }
    }

    /**
     * Inner class that simplifies the external representation of edges.
     * Originally intended for use with the Graph class.
     *
     * @param <E> parameter type of vertices in this edge
     */
    public static class Edge<E> implements Comparable<Edge<E>>{
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


        /**
         * Compares this edge with another, returning a negative number if
         * the other's weight is larger, zero if they are equal, and a
         * positive number if the other's weight is smaller.
         *
         * @param otherEdge the edge to compare this one with.
         * @return negative if this edge's weight is less that the other's
         * weight, zero if they are equal, positive if the other's is less
         */
        public int compareTo(Edge<E> otherEdge) {
            if (otherEdge == null) {
                throw new NullPointerException();
            }

            return (_weight - otherEdge._weight);
        }
    }
}