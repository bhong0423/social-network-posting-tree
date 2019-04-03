import java.util.Set;

import common.StringUtil;
import common.types.Tuple1;

/** A instance represents a person and their interest. 
 * @author ebirrell*/
public class Person extends Tuple1<String>{

    /** The possible Content-related states of a person. */
    enum State{  // The names indicate the state.
    		NEVER_SAW,
    		THINKING,
        SHARED,
        BORED
    }
    
    private State state; // State of this person

    private final Network graph; // The network to which this person belongs
    private final int clique; // The clique this person belongs to 

    private boolean sawFromClique= false; // Whether someone in their clique shared the content
    private int interest= -1; 	// How interested the person is
    						 		// 0 means bored, 10 means totally into it
    								// -1 if has not seen content


 
    /** Constructor: a Person with name n in clique c, added to graph g.
     * Precondition: The new person is not in g, and their name is distinct 
     *               from the name of any other person in g. This is because 
     *               the name is used for equality and hashing. */
    public Person(String n, Network g, int c) {
        super(StringUtil.toPronounCase(n));
        clique= c;
        state= State.NEVER_SAW;
        graph= g;
        graph.addVertex(this);
    }

    /** Return a representation of this Person. */
    public @Override String toString(){
        return super.toString() + " - " + state;
    }

    /** Return the name of this person. */
    public String getName(){
        return _1;
    }
    
    /** Return the clique this person is in */
    public int getClique() {
    		return clique;
    }
    
    /** Return current interest level */
    public int getInterest() {
    		if(interest < 0) {
    			return 0;
    		}
    		
    		return interest;
    }
    
    /** Return "saw content from friend in same clique" */
    public boolean sawFromClique() {
    		return sawFromClique;
    }
    
    /** Make this person see the content during step currentStep */
    public void seeContent(Person source, int currentStep, int contentInterest) {
    		if (state == State.NEVER_SAW) {
    			state= State.THINKING;	
    			interest= contentInterest;
    		} 
    		if (state == State.THINKING && source.clique == clique) {
    			sawFromClique= true;
    		}
    }
    
    /** Make this person see the content during step currentStep */
    public void share(int currentStep, int contentInterest) {
    		state= State.SHARED;
    		interest= 0;

    		for (Person p: this.getNeighbors()) {
    			p.seeContent(this, 0, contentInterest);
    		}
    }


     /** Decrement the interest of this person in step currentStep.*/
     public void reduceInterest(int currentStep) {
        interest--;
        if (interest == 0) {
        		state= State.BORED;
        }
    }

    /** Return the state of this person. */
    public State getState() {
        return state;
    }

    /** Return "This person is still interested". */
    public boolean isInterested() {
        return interest > 0;
    }

    /** @return true iff this person is bored */
    public boolean isBored() {
        return state == State.BORED;
    }

    /** Return the neighbors of this person. */
    public Set<Person> getNeighbors() {
        return graph.neighborsOf(this);
    }

}
