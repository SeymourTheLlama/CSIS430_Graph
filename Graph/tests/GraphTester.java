import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.junit.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

public class GraphTester {

    private Graph<String> _testDirGraph;
    private Graph<String> _testUndirGraph;

    @Before
    public void setup() {
        _testDirGraph = new Graph<>(true);
        _testUndirGraph = new Graph<>(false);
    }

    // these are separated for easier debugging
    @Test
    public void testAddVerticesDir() {
        testAddVertices(_testDirGraph);
    }

    @Test
    public void testAddVerticesUndir() {
        testAddVertices(_testUndirGraph);
    }

    @Test
    public void testRemoveVerticesDir() {
        testRemoveVertices(_testDirGraph);
    }

    @Test
    public void testRemoveVerticesUndir() {
        testRemoveVertices(_testUndirGraph);
    }

    @Test
    public void testRemoveVerticesWithEdgesDir() {
        _testDirGraph.addVertex("Sing");
        _testDirGraph.addVertex("song");

        _testDirGraph.addEdge("Sing", "song", 4);
        _testDirGraph.addEdge("song", "Sing", 5);

        _testDirGraph.removeVertex("Sing");

        assertFalse(_testDirGraph.edgeExists("Sing", "song"));
        assertFalse(_testDirGraph.edgeExists("song", "Sing"));
    }

    @Test
    public void testRemoveVerticesWithEdgesUndir() {
        _testUndirGraph.addVertex("Sing");
        _testUndirGraph.addVertex("song");

        _testUndirGraph.addEdge("Sing", "song", 4);

        _testUndirGraph.removeVertex("Sing");

        assertFalse(_testUndirGraph.edgeExists("Sing", "song"));
        assertFalse(_testUndirGraph.edgeExists("song", "Sing"));
    }

    @Test
    public void testAddEdgesDir() {
        _testDirGraph.addVertex("Sing");
        _testDirGraph.addVertex("that");
        _testDirGraph.addVertex("song");

        _testDirGraph.addEdge("Sing", "that", 5);

        // make sure no equivalent edge was created from destination to source
        assertFalse(_testDirGraph.edgeExists("that", "Sing"));
        assertEquals(5, _testDirGraph.getEdgeWeight("Sing", "that"));

        _testDirGraph.addEdge("that", "Sing", 4);

        // make sure the original weight is not rewritten by adding a reverse
        // edge
        assertEquals(5, _testDirGraph.getEdgeWeight("Sing", "that"));

        // MORE could be done here

        _testDirGraph.removeVertex("Sing");
        _testDirGraph.removeVertex("that");
        _testDirGraph.removeVertex("song");

        testAddEdges(_testDirGraph);
    }

    @Test
    public void testAddEdgesUndir() {
        _testUndirGraph.addVertex("Sing");
        _testUndirGraph.addVertex("that");
        _testUndirGraph.addVertex("song");

        _testUndirGraph.addEdge("Sing", "that", 5);

        // make sure an equivalent edge was created from destination to source
        assertTrue(_testUndirGraph.edgeExists("that", "Sing"));

        _testUndirGraph.addEdge("that", "Sing", 4);

        // make sure the original weight is rewritten by adding a reverse edge
        assertEquals(4, _testUndirGraph.getEdgeWeight("Sing", "that"));

        // MORE could be done here

        _testUndirGraph.removeVertex("Sing");
        _testUndirGraph.removeVertex("that");
        _testUndirGraph.removeVertex("song");

        testAddEdges(_testUndirGraph);
    }

    @Test
    public void testRemoveEdgesDir() {
        _testDirGraph.addVertex("Sing");
        _testDirGraph.addVertex("that");
        _testDirGraph.addVertex("song");

        _testDirGraph.addEdge("Sing", "that", 5);
        _testDirGraph.addEdge("Sing", "song", 3);

        try {
            _testDirGraph.removeEdge("that", "Sing");
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            _testDirGraph.removeEdge("song", "that");
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        testRemoveEdges(_testDirGraph);
    }

    @Test
    public void testRemoveEdgesUndir() {
        _testUndirGraph.addVertex("Sing");
        _testUndirGraph.addVertex("that");
        _testUndirGraph.addVertex("song");

        _testUndirGraph.addEdge("Sing", "that", 5);
        _testUndirGraph.addEdge("Sing", "song", 3);

        _testUndirGraph.removeEdge("that", "Sing");

        assertFalse(_testUndirGraph.edgeExists("Sing", "that"));

        _testUndirGraph.removeEdge("song", "Sing");

        assertFalse(_testUndirGraph.edgeExists("Sing", "song"));

        testRemoveEdges(_testUndirGraph);
    }

    @Test
    public void testGettingEdgeWeight() {
        _testDirGraph.addVertex("Jedi");
        _testDirGraph.addVertex("Party");
        _testDirGraph.addVertex("Dance");

        _testUndirGraph.addVertex("Jedi");
        _testUndirGraph.addVertex("Party");
        _testUndirGraph.addVertex("Dance");

        _testDirGraph.addEdge("Jedi", "Party", 5);
        _testDirGraph.addEdge("Party", "Jedi", 2);
        _testDirGraph.addEdge("Dance", "Jedi", 3);

        assertEquals(5,_testDirGraph.getEdgeWeight("Jedi","Party"));
        assertEquals(2, _testDirGraph.getEdgeWeight("Party", "Jedi"));
        assertEquals(3, _testDirGraph.getEdgeWeight("Dance", "Jedi"));

        _testUndirGraph.addEdge("Jedi", "Party", 5);

        assertTrue(_testUndirGraph.edgeExists("Jedi", "Party"));
        assertTrue(_testUndirGraph.edgeExists("Party", "Jedi"));

        _testUndirGraph.addEdge("Party", "Jedi", 2);

        assertTrue(_testUndirGraph.edgeExists("Jedi", "Party"));
        assertTrue(_testUndirGraph.edgeExists("Party", "Jedi"));

        _testUndirGraph.addEdge("Dance", "Jedi", 3);

        assertEquals(2, _testUndirGraph.getEdgeWeight("Jedi","Party"));
        assertEquals(2, _testUndirGraph.getEdgeWeight("Party", "Jedi"));
        assertEquals(3, _testUndirGraph.getEdgeWeight("Dance", "Jedi"));
    }

    @Test
    public void testEdgeExists() {
        _testDirGraph.addVertex("Jedi");
        _testDirGraph.addVertex("Party");
        _testDirGraph.addVertex("Dance");

        _testUndirGraph.addVertex("Jedi");
        _testUndirGraph.addVertex("Party");
        _testUndirGraph.addVertex("Dance");

        _testDirGraph.addEdge("Jedi", "Party", 5);

        assertTrue(_testDirGraph.edgeExists("Jedi", "Party"));
        assertFalse(_testDirGraph.edgeExists("Party", "Jedi"));

        _testDirGraph.addEdge("Party", "Jedi", 3);

        assertTrue(_testDirGraph.edgeExists("Party", "Jedi"));

        _testDirGraph.removeEdge("Jedi", "Party");

        assertFalse(_testDirGraph.edgeExists("Jedi", "Party"));
        assertTrue(_testDirGraph.edgeExists("Party", "Jedi"));
    }

    @Test
    public void testPathLength() {
        List<String> pathListTest = new ArrayList<>();

        assertEquals(-1,_testUndirGraph.pathLength(pathListTest));

        pathListTest.add("Below");

        assertEquals(-1,_testUndirGraph.pathLength(pathListTest));

        testFillGraph(_testUndirGraph);

        assertEquals(-1,_testUndirGraph.pathLength(pathListTest));

        pathListTest.add("Hello");

        assertEquals(-1, _testUndirGraph.pathLength(pathListTest));

        pathListTest.remove("Below");

        assertEquals(0, _testUndirGraph.pathLength(pathListTest));

        pathListTest.add("my");
        pathListTest.add("old");
        pathListTest.add("friend.");
        pathListTest.add("Perhaps");
        pathListTest.add("it");
        pathListTest.add("has");
        pathListTest.add("been too long.");

        assertEquals(259,_testUndirGraph.pathLength(pathListTest));
    }

    @Test
    public void testFromCSVFile() throws IOException {
        Graph<String> csvDirGraph;
        Scanner csvScan = new Scanner("");
        String csvBadForm1 = "foo\n";
        String csvBadForm2 = "foo\nbar\n";
        String csvBadForm3 = "3\nfoo\nbar\nsparg\nprary";
        String csvNoEdge = "3\nfoo\nbar\nblarg\n";
        String csvGoodVBadE = "3\nfoo\nbar\nblarg\n5\nfoo,spar,1";
        String csvGoodVBadE2 = "3\nfoo\nbar\nblarg\n1\nfoo,spar,1";
        String csvGood = "3\nfoo\nbar\nblarg\n2\nfoo,bar,1\nblarg,foo,5";
        boolean directedness = false;

        try {
            csvDirGraph = Graph.fromCSVFile(directedness, csvScan);
            fail();
        }
        catch (IOException e) {
            // all good my dudes
        }

        csvScan = new Scanner(csvBadForm1);

        try {
            csvDirGraph = Graph.fromCSVFile(directedness, csvScan);
            fail();
        }
        catch (IOException e) {
            // all good my dudes
        }

        csvScan = new Scanner(csvBadForm2);

        try {
            csvDirGraph = Graph.fromCSVFile(directedness, csvScan);
            fail();
        }
        catch (IOException e) {
            // all good my dudes
        }

        csvScan = new Scanner(csvBadForm3);

        try {
            csvDirGraph = Graph.fromCSVFile(directedness, csvScan);
            fail();
        }
        catch (IOException e) {
            // all good my dudes
        }

        csvScan = new Scanner(csvNoEdge);
        csvDirGraph = Graph.fromCSVFile(directedness, csvScan);

        System.out.println(csvDirGraph);
        System.out.println();

        csvScan = new Scanner(csvGoodVBadE);

        try {
            csvDirGraph = Graph.fromCSVFile(directedness, csvScan);
            fail();
        }
        catch (IOException e) {
            // all good my dudes
        }

        csvScan = new Scanner(csvGoodVBadE2);

        try {
            csvDirGraph = Graph.fromCSVFile(directedness, csvScan);
            fail();
        }
        catch (IOException e) {
            // all good my dudes
        }

        csvScan = new Scanner(csvGood);
        csvDirGraph = Graph.fromCSVFile(directedness, csvScan);

        System.out.println(csvDirGraph);

        csvScan = new Scanner(new File("Campus_Map.csv"));
        csvDirGraph = Graph.fromCSVFile(false, csvScan);

        System.out.println(csvDirGraph);
    }

    @Test
    public void testShortestPathBetween() {
        try {
            _testDirGraph.shortestPathBetween(null, null);
            fail();
        }
        catch (NoSuchElementException e) {} // boom baby

        testFillGraph(_testDirGraph);

        List<Graph.Edge<String>> shortPath = _testDirGraph.shortestPathBetween("Hello", "Hello");

        int totalPathWeight = 0;
        for (Graph.Edge<String> edge : shortPath) {
            totalPathWeight += edge.getWeight();
            System.out.println(edge);
        }

        assertEquals(0, totalPathWeight);

        shortPath = _testDirGraph.shortestPathBetween
                ("Hello", "been too long.");

        totalPathWeight = 0;
        for (Graph.Edge<String> edge : shortPath) {
            totalPathWeight += edge.getWeight();
            System.out.println(edge);
        }

        assertEquals(68, totalPathWeight);

        shortPath = _testDirGraph.shortestPathBetween
                ("been too long.", "Hello");

        assertEquals(null, shortPath);
    }

    @Test
    public void testMinimumSpanningTree() {
        System.out.println(_testUndirGraph.minimumSpanningTree());

        try {
            System.out.println(_testDirGraph.minimumSpanningTree());
            fail();
        }
        catch (IllegalStateException e) {} // all is well

        testFillGraph(_testUndirGraph);

        System.out.println(_testUndirGraph.minimumSpanningTree());
    }


    @Test
    public void testOptimalPath() {
        testFullyConnectGraph(_testUndirGraph);
        System.out.println(_testUndirGraph.getOptimalTour(20, (float)0.02,
                1000));


        try {
            _testUndirGraph = Graph.fromTSPFile(new FileInputStream(new File
                    ("eil101.xml")));
        }
        catch (IOException|SAXException|ParserConfigurationException e) {
            fail();
        } // rur roh

        System.out.println(_testUndirGraph.getOptimalTour(100, (float)0.02,
                5000));

        try {
            _testUndirGraph = Graph.fromTSPFile(new FileInputStream(new File
                    ("kroA100.xml")));
        }
        catch (IOException|SAXException|ParserConfigurationException e) {
            fail();
        } // rur roh

        System.out.println(_testUndirGraph.getOptimalTour(100, (float)0.02,
                5000));
    }

    @Test
    public void testToString() {
        //System.out.println(_testDirGraph.toString());
        //System.out.println(_testUndirGraph.toString());

        testFillGraph(_testDirGraph);
        testFillGraph(_testUndirGraph);

        System.out.println(_testDirGraph.toString());
        System.out.println();
        System.out.println(_testUndirGraph.toString());
    }

    // ADD DUPLICATE VERTICES
    private void testAddVertices(Graph testGraph) {
        // adding a null value should break it
        try {
            testGraph.addVertex(null);
            fail();
        }
        catch (IllegalArgumentException e) {
            // all is well
        }

//        // adding a non-T value should break it
//        try {
//            testGraph.addVertex(5);
//            fail();
//        }
//        catch (IllegalArgumentException e) {
//            // all is well
//        }

        // add a vertex to an empty graph
        testGraph.addVertex("Fumm");
        assertTrue(testGraph.getVertices().contains("Fumm"));

        // fill a graph
        testFillGraph(testGraph);

    }

    private void testRemoveVertices(Graph testGraph) {
        // removing a null value from an empty graph should break it
        try {
            testGraph.removeVertex(null);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        // removing a non-T value from an empty graph should break it
        try {
            testGraph.removeVertex(5);
            fail();
        }
        catch (IllegalArgumentException e) {
            // all is well
        }
        catch (NoSuchElementException e) {
            // that should do it too
        }

        // removing a T value from an empty graph should break it
        try {
            testGraph.removeVertex("Bellow");
            fail();
        }
        catch (NoSuchElementException e) {
            // all is well
        }

        testGraph.addVertex("Hello");

        // removing a null value from a one-element graph should break it
        try {
            testGraph.removeVertex(null);
            fail();
        }
        catch (NoSuchElementException e) {
            // all is well
        }

        // removing a non-T value from a one-element graph should break it
        try {
            testGraph.removeVertex(5);
            fail();
        }
        catch (NoSuchElementException e) {
            // all is well
        }

        // removing a T value from a one-element graph should break it
        try {
            testGraph.removeVertex("Bellow");
            fail();
        }
        catch (NoSuchElementException e) {
            // all is well
        }

        // remove the one vertex
        testGraph.removeVertex("Hello");


        testFillGraph(testGraph);

        // removing a null value from a full graph should break it
        try {
            testGraph.removeVertex(null);
            fail();
        }
        catch (NoSuchElementException e) {
            // all is well
        }

        // removing a non-T value from a full graph should break it
        try {
            testGraph.removeVertex(5);
            fail();
        }
        catch (NoSuchElementException e) {
            // all is well
        }

        // removing a T value from a full graph should break it
        try {
            testGraph.removeVertex("Bellow");
            fail();
        }
        catch (NoSuchElementException e) {
            // all is well
        }

        // remove an existing vertex
        testGraph.removeVertex("Hello");

        // remove every vertex
        for (Object vertex : testGraph.getVertices()) {
            testGraph.removeVertex(vertex);
        }

        assertTrue(testGraph.getVertices().isEmpty());
    }

    // test just the mechanics of adding edges, with no reference to direction
    private void testAddEdges(Graph testGraph) {
        // CONSIDER adding existent with null

        // test nulls and nonexistents with an empty graph
        try {
            testGraph.addEdge(null, null, 2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge(null, "Bellow", 2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge("Bellow", null,2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge("Bellow", "Fellow",2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well


        // test nulls and nonexistents with a single vertex graph
        testGraph.addVertex("Hello");

        try {
            testGraph.addEdge(null, null, 2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge(null, "Bellow", 2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge("Bellow", null,2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge("Bellow", "Fellow",2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well


        // test a loop with a single vertex graph
        try {
            testGraph.addEdge("Hello", "Hello", 2);
            fail();
        }
        catch (IllegalArgumentException e) {} // all is well

        // test connecting a nonexistent to the only vertex
        try {
            testGraph.addEdge("Hello", "Bellow", 2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge("Bellow", "Hello",2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well




        testGraph.removeVertex("Hello");


        // testFillGraph adds many edges, so this tests adding edges normally
        testFillGraph(testGraph);


        // test nulls and nonexistents with a full graph

        try {
            testGraph.addEdge(null, null, 2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge(null, "Bellow", 2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge("Bellow", null,2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge("Bellow", "Fellow",2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well


        // test a loop and connecting to nonexistents with a full graph
        try {
            testGraph.addEdge("Hello", "Hello", 2);
            fail();
        }
        catch (IllegalArgumentException e) {} // all is well
        try {
            testGraph.addEdge("Hello", "Bellow", 2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.addEdge("Bellow", "Hello",2);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well


        // test adding a duplicate edge, should reset weight
        testGraph.addEdge("Hello", "friend.", 2);
        testGraph.addEdge("Hello", "friend.", 4);

        assertEquals(4,testGraph.getEdgeWeight("Hello", "friend."));


        // test adding valid edges with invalid weight
        try {
            testGraph.addEdge("Perhaps", "Hello", -1);
        }
        catch (IllegalArgumentException e) {} // all is well

    }

    // test just the mechanics of removing edges, with no reference to direction
    private void testRemoveEdges(Graph testGraph) {
        // remove nonexistent edges from an empty graph
        try {
            testGraph.removeEdge(null, null);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.removeEdge("Yo", "Rabbit");
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.removeEdge(2, new Graph(false));
            fail();
        }
        catch (NoSuchElementException e) {} // all is well


        // remove nonexistent edges from a single-element graph
        testGraph.addVertex("Hairball");

        try {
            testGraph.removeEdge(null, null);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.removeEdge("Yo", "Rabbit");
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.removeEdge(2, new Graph(false));
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.removeEdge("Hairball", "Hairball");
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        testGraph.removeVertex("Hairball");

        // remove nonexistent edges from a full graph
        testFillGraph(testGraph);

        try {
            testGraph.removeEdge(null, null);
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.removeEdge("Yo", "Rabbit");
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.removeEdge(2, new Graph(false));
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        try {
            testGraph.removeEdge("Hairball", "Hairball");
            fail();
        }
        catch (NoSuchElementException e) {} // all is well

        // test removing actual things from a full graph
        testGraph.removeEdge("Hello", "my");

        assertFalse(testGraph.edgeExists("Hello", "my"));

        assertTrue(testGraph.edgeExists("Hello", "old"));
    }

    private void testFillGraph(Graph testGraph) {
        testGraph.addVertex("Hello");
        testGraph.addVertex("my");
        testGraph.addVertex("old");
        testGraph.addVertex("friend.");
        testGraph.addVertex("Perhaps");
        testGraph.addVertex("it");
        testGraph.addVertex("has");
        testGraph.addVertex("been too long.");

        testGraph.addEdge("Hello", "my", 1);
        testGraph.addEdge("Hello", "old", 4);
        testGraph.addEdge("friend.", "it", 44);
        testGraph.addEdge("been too long.", "Perhaps", 2);
        testGraph.addEdge("my", "old", 2);
        testGraph.addEdge("old", "friend.", 6);
        testGraph.addEdge("friend.", "Perhaps", 11);
        testGraph.addEdge("Perhaps", "it", 224);
        testGraph.addEdge("it", "has", 7);
        testGraph.addEdge("has", "been too long.", 8);
    }

    private void testFullyConnectGraph(Graph testGraph) {
        testGraph.addVertex("Hello");
        testGraph.addVertex("my");
        testGraph.addVertex("old");
        testGraph.addVertex("friend.");
        testGraph.addVertex("Perhaps");
        testGraph.addVertex("it");
        testGraph.addVertex("has");
        testGraph.addVertex("been too long.");

        testGraph.addEdge("Hello", "my", 2);
        testGraph.addEdge("Hello", "old", 5);
        testGraph.addEdge("Hello", "friend.", 33);
        testGraph.addEdge("Hello", "Perhaps", 1);
        testGraph.addEdge("Hello", "it", 5);
        testGraph.addEdge("Hello", "has", 60);
        testGraph.addEdge("Hello", "been too long.", 19);
        testGraph.addEdge("my", "old", 22);
        testGraph.addEdge("my", "friend.", 9);
        testGraph.addEdge("my", "Perhaps", 11);
        testGraph.addEdge("my", "it", 24);
        testGraph.addEdge("my", "has", 8);
        testGraph.addEdge("my", "been too long.", 6);
        testGraph.addEdge("old", "friend.", 4);
        testGraph.addEdge("old", "Perhaps", 31);
        testGraph.addEdge("old", "it", 17);
        testGraph.addEdge("old", "has", 63);
        testGraph.addEdge("old", "been too long.", 2);
        testGraph.addEdge("Perhaps", "it", 1);
        testGraph.addEdge("Perhaps", "has", 3);
        testGraph.addEdge("Perhaps", "been too long.", 1);
        testGraph.addEdge("it", "has", 8);
        testGraph.addEdge("it", "been too long.", 44);
        testGraph.addEdge("has", "been too long.", 100);
    }
}
