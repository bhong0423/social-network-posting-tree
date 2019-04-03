import io.ScannerUtils;

import java.util.Scanner;
import java.util.Set;

import common.Util;
import common.types.Tuple;
import common.types.Tuple7;

/** An instance represents simulation of sharing in a social network.
 * <br>
 * The root of a tree represents the first person to share some content.
 * The children of each node represent the people who saw the content
 * posted by the person represented by that node.
 * @author MPatashnik and ebirrell
 */

// Note: actually class Sharing
public class Post implements Runnable {

    private Network network; // The graph in which this Post is shared. 

    private PostingTree tree; // The tree representing how this Post is reposted

    private int steps; // Number of time steps this post took to create st.

    private Statistics statistics; // The sharing model:
    // Statistics that determine the spread of the post.

    /** How many chars to print per line in the running section. */
    private static final int RUNNING_CHAR_COUNT_MAX= 50;

    /** Used in printing the run progress. */
    private int runningCharCount= 7;

    /** Constructor: a new Post on network nw with first poster fp
     *  and sharing model s.    */
    public Post(Network nw, Person fp,  Statistics s){
        steps= 0;
        network= nw;
        fp.share(0,s.getContentInterest());
        tree= new PostingTree(fp);
		for (Person n : fp.getNeighbors()) {
			if (!tree.contains(n)) {
				tree.insert(n, fp);
			}
		}
        statistics= s;
    }

    /** Run the post until no thinking people remain.
     *  Print out info about running.*/
    public @Override void run() {
        System.out.print("Running...");
        while (network.getPeopleOfType(Person.State.THINKING).size() > 0) {
            step();
        }
        System.out.println("Done.\n");
    }


    /** Perform a single step on the post, using sharing model statistics.
     * First, people who have seen the post become less interested by 1, and 
     * if their interest reaches 0, they get bored.
     * Second, people may reshare the post, showing it to all their neighbors.
     */
    private void step() {
        Set<Person> people= network.vertexSet();
        System.out.print(".");
        runningCharCount++;
        if (runningCharCount > RUNNING_CHAR_COUNT_MAX) {
            System.out.print("\n");
        }

        // For each interested person, deduct 1 from interest and make
        // bored if interest becomes 0
        for (Person p : people) {
            if (p.isInterested()) {
                p.reduceInterest(steps);
            }
        }

        // For each exposed person, determine whether they reshare content in this step
        for (Person p : people) {
            if (p.isInterested()) {
            		double interestLevel= (double) p.getInterest()/10;

            		if((p.sawFromClique() && statistics.shareFromClique(interestLevel))
            			|| (!p.sawFromClique() && statistics.shareFromNonClique(interestLevel))) {
            			p.share(steps,statistics.getContentInterest());
            			for(Person n : p.getNeighbors()) {
            				if(!tree.contains(n)) {
            					tree.insert(n, p);
           				}
            			}
            		}
            }
        }

        steps= steps + 1;
    }

    /** Read in the seven statistic arguments from the console.
     * Return a Tuple7, with the following components:
     * 		<br> - size: the number of people in the network
     * 		<br> - nCliques: the number of cliques in the network
     * 		<br> - cliqueConnectionProbability: probability that two people in the same clique are connected in the network
     * 		<br> - noncliqueConnectionProbability: probability that two people in different cliques are connected in the network
     * 		<br> - il: interest level of the modeled content 0...10
     * 		<br> - cliqueSharingProbability: probability that an exposed person re-shares content from a person in the same clique
     * 		<br> - noncliqueSharingProbabiity: probability that an exposed person re-shares content from a person in a different clique
    */
    private static Tuple7<Integer, Integer, Double, Double, Integer, Double, Double> readArgs() {
        Scanner scanner= ScannerUtils.defaultScanner();
        int size = ScannerUtils.get(Integer.class, scanner, "Enter the size of the population: ",
                "Please enter a positive non-zero integer", (i) -> i > 0);
        int nCliques= ScannerUtils.get(Integer.class, scanner, 
                "Enter the number of cliques in the network: ",
                "Please enter an integer between 1 and size", (i) -> i > 0 && i<= size);
        double cliqueConnectionProb= ScannerUtils.get(Double.class, scanner, 
                "Enter the probability of a connection within a clique: ",
                "Please enter a double in the range [0,1]", (d) -> d >= 0 && d <= 1);
        double noncliqueConnectionProb= ScannerUtils.get(Double.class, scanner, 
                "Enter the probability of a connection between different cliques: ",
                "Please enter a double in the range [0,1]", (d) -> d >= 0 && d <= 1);  
        int il = ScannerUtils.get(Integer.class, scanner, 
                "Enter the interest level of the modeled content: ",
                "Please enter an integer between 0 and 10", (i) -> i >= 0 && i <= 10);
        double cliqueRepostProb= ScannerUtils.get(Double.class, scanner, 
                "Enter the probability that a person reposts content from their clique: ",
                "Please enter a double in the range [0,1]", (d) -> d >= 0 && d <= 1);
        double noncliqueSharingProb= ScannerUtils.get(Double.class, scanner, 
                "Enter the probability that a person reposts content from a different clique: ",
                "Please enter a double in the range [0,1]", (d) -> d >= 0 && d <= 1);
        scanner.close();
        return Tuple.of(size, nCliques, cliqueConnectionProb, noncliqueConnectionProb, 
        		il,cliqueRepostProb,noncliqueSharingProb);
    }


    /** Run Content Sharing simulation on the arguments listed in args.
     * If args does not match the pattern below, read in arguments via the console
     * by using readArgs().
     * 
     * Then, call post.run() and create a PostingFrame showing the created PostingTree.
     * 
     * args should be an array of [size, nCliques, clique connection probability, 
     * 						nonclique connection probability, post interest level,
     * 						clique sharing probability, nonclique sharing probability],
     * 		or unused (any value). If not used, the user is prompted for input in the console.
     */
    public static void main(String[] args) {
        //Get arguments
        int size= 10;
        int nCliques= 3;
        double cliqueConnectionProbability= 0.7;
        double noncliqueConnectionProbability= 0.2;
        int il= 5;
        double cliqueSharingProbability= 0.5;
        double noncliqueSharingProbability= 0.1;


        try {
            //Attempt to read from args array passed in
            size= Integer.parseInt(args[0]);
            nCliques= Integer.parseInt(args[1]);
            cliqueConnectionProbability= Double.parseDouble(args[2]);
            noncliqueConnectionProbability= Double.parseDouble(args[3]);
            il= Integer.parseInt(args[4]);
            cliqueSharingProbability= Double.parseDouble(args[5]);
            noncliqueSharingProbability= Double.parseDouble(args[6]);
        } catch (Exception e) {
            //If too few or wrong type, read from scanner
            Tuple7<Integer, Integer, Double, Double, Integer, Double, Double> args2= readArgs();
            size= args2._1;
            nCliques= args2._2;
            cliqueConnectionProbability= args2._3;
            noncliqueConnectionProbability= args2._4;
            il= args2._5;
            cliqueSharingProbability= args2._6;
            noncliqueSharingProbability= args2._7;
        }

        //Set defaults and create the Network, Statistics, and Disease objects
        System.out.print("\nSetting up ");
        System.out.print(".");
        Network n= new Network(size, nCliques, cliqueConnectionProbability, 
        			noncliqueConnectionProbability);
        System.out.print(".");
        Statistics s= new Statistics(il,cliqueSharingProbability, noncliqueSharingProbability);
        System.out.print(".");
        Post post= new Post(n, Util.randomElement(n.vertexSet()), s);
        System.out.println("Done.");

        post.run();

        System.out.println(post.tree.toStringVerbose() + "\n");

        for (Person p : post.network.getPeopleOfType(Person.State.NEVER_SAW)) {
            System.out.println(p);
        }

        PostingFrame.show(post.tree, post.steps);
    }
}
