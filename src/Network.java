import io.TextIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import graph.Graph;

/** An instance represents a network of people, used with a Sharing tree.
 * It is known that the names of all Person's will be distinct --no duplicates. 
 * @author Mshnik and ebirrell*/
public class Network extends Graph<Person, PersonConnection> {
    
    protected static String[] names; // Names of all people
    
    /** Read in the array of names from the names text file */
    static {
        try {
            names= TextIO.read(new File("data/names.txt"));
        } catch (IOException e) {
            System.err.println("Error reading names file, should be located at data/names.txt");
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /** Constructor: an instance with no people and no connections. */
    public Network() {
        super();
    }

    /** Constructor: a graph of size people with edges generated
     * randomly based on number of cliques nc, cliqueConnectionProbability ccp
     * and noncliqueConnectionProbability ncp.
     * Preconditions: 0 <= size, 1<= nc<=size, 0 <= ccp <= 1, 0 <= ncp <= 1. */
    public Network(int size, int nc, double ccp, double ncp) {
        super();
        assert 0 <= size  &&  1 <= nc && nc <= size &&0 <= ccp  &&  ccp <= 1   
        			&&  0 <= ncp  &&  ncp <= 1;
        
        for (int i = 0; i < size; i++) {
            //Add itself to this as part of construction
            new Person(names[i], this, i % nc);
        }
        
        for (Person p1 : vertexSet()) {
            for (Person p2 : vertexSet()) {
                if (p1 != p2  && p1.getClique() == p2.getClique()	
                		&& Math.random() < ccp) {
                    addEdge(p1, p2, new PersonConnection());
                } else if (p1 != p2 && p1.getClique() != p2.getClique()
                		&& Math.random() < ncp){	
                		addEdge(p1,p2, new PersonConnection());
                }
            }
        }
    }

    /** Constructor: an instance generated for the people in st.
     * There is an edge from each parent to each of its children. */
    public Network(PostingTree st) {
        super();
        addVertex(st.getRoot());
        recCreate(st);
    }

    /** Add to this Network the people in children trees of st,
     * adding edges from each root to its children.
     * Precondition: st.getRoot is already in the graph. */
    private void recCreate(PostingTree st) {
        Person dtRoot= st.getRoot();
        for (PostingTree child : st.getChildren()){
            addVertex(child.getRoot());
            addEdge(dtRoot, child.getRoot(), new PersonConnection());
            recCreate(child);
       }
    }

    /** Return a list of people in state s in this Network. */
    public List<Person> getPeopleOfType(Person.State s) {
        ArrayList<Person> lst= new ArrayList<>();
        for (Person p : vertexSet()) {
            if (p.getState() == s) {
                lst.add(p);
            }
        }
        return lst;
    }

}
