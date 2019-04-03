/** An instance contains the probabilities that a person reshares a post
 * based on the interestingness of the original post and whether or not
 * they see it from a person in the same clique. 
 * 
 * @author Mshnik and ebirrell*/
public class Statistics {

	private int contentInterest; // Interestingness of content 0...10
	private double cliqueChance; // Probability of sharing content from friend in clique
	private double otherChance; // Probability of sharing content from friend outside clique

    /** Constructor: an instance with clique probability cp and
     * other probability ip.
     * Precondition: 0 <= cp, op <= 1. */
    public Statistics(int i, double cp, double op) {
    		contentInterest = i;
        cliqueChance= cp;
        otherChance= op;
    }
    
    /** Return content interestingness level */
    public int getContentInterest() {
    		return contentInterest;
    }

    /** Return true if a new random number is less than the probability
     * of contagion. */
    public boolean shareFromClique(double interest){
    		assert interest < 1;
      return Math.random() < (cliqueChance * interest);
    }

    /** Return true if a new random number is less than the probability
     * of becoming immune. */
    public boolean shareFromNonClique(double interest){
    		assert interest < 1;
      return Math.random() < (otherChance * interest);
    }
}
