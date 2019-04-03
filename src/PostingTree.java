/* NetId(s): xxxnn, xxxnn. Time spent: h hours, mm minutes.

 * Name(s):
 * What I thought about this assignment:
 * 
 *
 */

import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/** An instance of PostingTree represents the spreading of a Post through
 * a (social) Network of people.
 * The root of the PostingTree is the original poster. From there, each
 * person in the PostingTree is the child of the person from whom they saw the post.
 * For example, for the tree:
 * <p>
 *      A
 *     / \
 *    B   C
 *       / \
 *      D   E
 * <p>
 * Person A originally created the post, B and C saw A's post, 
 * C reshared the post, and D and E saw the post from C.
 * <p>
 * Important note: The name of each person in the sharing tree is unique.
 *
 * @author Mshnik and ebirrell
 */
public class PostingTree {

    /** The String to be used as a separator in toString() */
    public static final String SEPARATOR = " - ";

    /** The String that marks the start of children in toString() */
    public static final String START_CHILDREN_DELIMITER = "[";

    /**  The String that divides children in toString() */
    public static final String DELIMITER = ", ";

    /** The String that marks the end of children in toString() */
    public static final String END_CHILDREN_DELIMITER = "]";

    /** The String that is the space increment in toStringVerbose() */
    public static final String VERBOSE_SPACE_INCREMENT = "\t";

    /** The person at this node of this PostingTree.
     * All Person's in a PostingTree have different names.
     * There are no duplicates.
     */
    private Person root;

    /** The immediate children of this PostingTree node.
     * Each element of children saw the post from the person at this node.
     * Set children is non-null. It the empty set if this is a leaf. */
    private Set<PostingTree> children;

    /** Constructor: a new PostingTree with root p and no children.
     * Throw an IllegalArgumentException if p is null. */
    public PostingTree(Person p) throws IllegalArgumentException {
        if (p == null)
            throw new IllegalArgumentException("Can't construct PostingTree with null root");
        root= p;
        children= new HashSet<>();
    }

    /** Constructor: a new PostingTree that is a copy of tree p.
     * Tree p and its copy have no node in common (but nodes can share a Person).
     * Throw an IllegalArgumentException if p is null. */
    public PostingTree(PostingTree p) throws IllegalArgumentException {
        if (p == null)
            throw new IllegalArgumentException("Can't construct PostingTree as copy of null");
        root= p.root;
        children= new HashSet<>();

        for (PostingTree st : p.children) {
            children.add(new PostingTree(st));
        }
    }

    /** Return the person that is at the root of this PostingTree */
    public Person getRoot() {
        return root;
    }

    /** Return the number of direct children of this PostingTree */
    public int getChildrenCount() {
        return children.size();
    }

    /** Return a COPY of the set of children of this PostingTree. */
    public Set<PostingTree> getChildren() {
        return new HashSet<>(children);
    }

    /** Insert c in this PostingTree as a child of p and
     * return the PostingTree whose root is the new child.
     * Throw an IllegalArgumentException if:<br>
     * -- p or c is null,<br>
     * -- c is already in this PostingTree, or<br>
     * -- p is not in this PostingTree */
    public PostingTree insert(Person c, Person p) throws IllegalArgumentException {
        //TODO 1. This function has a simple, non-recursive implementation
        if( c == null || p == null )
        	throw new IllegalArgumentException();
        if(this.contains(c))
        	throw new IllegalArgumentException();
        PostingTree pTree = this.getTree(p);
        if(pTree == null)
        	throw new IllegalArgumentException();
        //PostingTree tree = new PostingTree(getTree(c));
        PostingTree cTree = new PostingTree(c);
       // PostingTree cTree = new PostingTree(getTree(p));
        pTree.children.add(cTree);
        return cTree;
    }

    /** Return the number of people in this PostingTree.
     * Note: If this is a leaf, the size is 1 (just the root) */
    public int size() {
        //TODO 2//go through each of the children, each leaf would add one
    	if(this == null)
    		return 0;
    	int sum = 1;
    	for(PostingTree ch : getChildren())
    	{
    		sum = sum + ch.size();
    	}
        return sum;
    }

    /**Return the depth at which p occurs in this PostingTree, or -1
     * if p is not in the PostingTree.
     * Note: depth(root) is 0.
     * If p is a child of this PostingTree, then depth(p) is 1. etc. */
    public int depth(Person p) 
    {
        //TODO 3
    	if (p == root)
    		return 0;
    	for (PostingTree ch : getChildren())
    	{
    		int d = ch.depth(p);
    		if (d == -1)
    			continue;
    		return d + 1;
    	}
    	return -1; 
    }

    /** If p is in this tree, return the PostingTree in this tree 
     * that contains p. If p is not in this tree, return null.
     * <p>
     * Example: Calling getTree(root) should return this.
     */
    public PostingTree getTree(Person p) {
        if (root == p) return this; //Base case - look here

        // Recursive case - ask children to look
        for (PostingTree st : children) {
            PostingTree search= st.getTree(p);
            if (search != null) return search;
        }

        return null; // Not found
    }

    /** Return true iff this PostingTree contains p. */
    public boolean contains(Person p) {
        /* Note: This PostingTree contains p iff the root of this
         * SharingTree is p or if one of p's children contains p. */
        if (root == p) return true;
        for (PostingTree rt : children) {
            if (rt.contains(p)) return true;
        }
        return false;
    }


    /** Return the maximum depth of this PostingTree, i.e. the longest path from
     * the root to a leaf. Example. If this PostingTree is a leaf, return 0.
     */
    public int maxDepth() {
        int maxDepth= 0;
        for (PostingTree rt : children) {
            maxDepth= Math.max(maxDepth, rt.maxDepth() + 1);
        }
        return maxDepth;
    }

    /** Return the width of this tree at depth d (i.e. the number of sharing 
     * trees that occur at depth d, where the depth of the root is 0.
     * Throw an IllegalArgumentException if depth < 0.
     * Thus, for the following tree :
     * Depth level:
     *         0       A
     *        / \
     * 1     B   C
     *      /   / \
     * 2   D   E   F
     *              \
     * 3             G
     * <p>
     * A.widthAtDepth(0) = 1,  A.widthAtDepth(1) = 2,
     * A.widthAtDepth(2) = 3,  A.widthAtDepth(3) = 1,
     * A.widthAtDepth(4) = 0.
     * C.widthAtDepth(0) = 1,  C.widthAtDepth(1) = 2
     */
    public int widthAtDepth(int d) throws IllegalArgumentException {
        //TODO 4
        // For d > 0, the width at depth d is the sum of the widths
        //            of the children at depth d-1.
    	int sum = 0;	
    	if (d < 0) 
    		throw new IllegalArgumentException();
    	if (d == 0)
    		return 1;
    	//for all children of root, recursively call function with reduced depth 
    	for (PostingTree st : children)
    	{
    		sum = sum + st.widthAtDepth(d-1);
    	}
    	return sum;
    }

    /** Return the maximum width of all the widths in this tree, i.e. the
     * maximum value that could be returned from widthAtDepth for this tree.
     */
    public int maxWidth() {
        return maxWidthImplementationOne(this);
    }

    // Simple implementation of maxWith. Relies on widthAtDepth.
    // Takes time proportional the the square of the size of the t.
    static int maxWidthImplementationOne(PostingTree t) {
        int width = 0;
        int depth = t.maxDepth();
        for (int i = 0; i <= depth; i++) {
            width = Math.max(width, t.widthAtDepth(i));
        }
        return width;
    }

    /* Better implementation of maxWidth. Caches results in an array.
     * Takes time proportional to the size of t. */
    static int maxWidthImplementationTwo(PostingTree t) {
        // For each integer d, 0 <= d <= maximum depth of t, store in
        // widths[d] the number of nodes at depth d in t.
        // The calculation is done by calling recursive procedure addToWidths.
        int[] widths = new int[t.maxDepth() + 1];   // initially, contains 0's
        t.addToWidths(0, widths);

        int max = 0;
        for (int width : widths) {
            max = Math.max(max, width);
        }
        return max;
    }

    /* For each node of this PostingTree that is at some depth d in this
     * SharingTree add 1 to widths[depth + d]. */
    private void addToWidths(int depth, int[] widths) {
        widths[depth]++;        //the root of this SharintTree is at depth d = 0
        for (PostingTree dt : children) {
            dt.addToWidths(depth + 1, widths);
        }
    }

    /* Better implementation of maxWidth. Caches results in a HashMap.
     * Takes time proportional to the size of t. */
    static int maxWidthImplementationThree(PostingTree t) {
        // For each possible depth d >= 0 in tree t, widthMap will contain the
        // entry (d, number of nodes at depth d in t). The calculation is
        // done using recursive procedure addToWidthMap.

        // For each integer d, 0 <= d <= maximum depth of t, add to
        // widthMap an entry <d, 0>.
        HashMap<Integer, Integer> widthMap = new HashMap<>();
        for (int d= 0; d <= t.maxDepth() + 1; d= d+1) {
            widthMap.put(d, 0);
        }

        t.addToWidthMap(0, widthMap);

        int max= 0;
        for (Integer w : widthMap.values()) {
            max= Math.max(max, w);
        }
        return max;
    }

    /* For each node of this PostingTree that is at some depth d in this PostingTree,
     * add 1 to the value part of entry <depth + d, ...> of widthMap. */
    private void addToWidthMap(int depth, HashMap<Integer, Integer> widthMap) {
        widthMap.put(depth, widthMap.get(depth) + 1);  //the root is at depth d = 0
        for (PostingTree dt : children) {
            dt.addToWidthMap(depth + 1, widthMap);
        }
    }

    /** Return the route the Post took to get from "here" (the root of
     * this PostingTree) to child c.
     * Return null if no such route.
     * For example, for this tree:
     * <p>
     * Depth level:
     * 0      A
     *       / \
     * 1    B   C
     *     /   / \
     * 2  D   E   F
     *             \
     * 3            G
     * <p>
     * A.getPostingRoute(E) should return a list of [A, C, E].
     * A.getPostingRoute(A) should return [A].
     * A.getPostingRoute(X) should return null.
     * <p>
     * B.getPostingRoute(C) should return null.
     * B.getPostingRoute(D) should return [B, D]
     */
    public List<Person> getPostingRoute(Person c)
    {
        //TODO 5
        // Points will be deducted if you continually call getParent or
        //    something similar. This method itself is best written recursively.
        // If you get a non-null posting route for a child, then you just have
        // to prepend the route.
        // Hint: You have to return a List<Person>. But List is an
        // interface, so use something that implements it.
        // LinkedList<Person> is preferred to ArrayList<Person> here.
        // The reason is a hint on how to use it.
        // Base Case - The root of this PostingTree is c. Route is just [child]
    	if (root.equals(c))
    	{	//List<Person> list = new LinkedList<Person>()
    		LinkedList<Person> path = new LinkedList<Person>();
    		path.add(c);
    		return path;
    	}
    	for(PostingTree ch: children)
    	{
    		List<Person> path = ch.getPostingRoute(c);
    		if (path != null)
    		{
    			path.add(0,root);
        		return path;
    		}
    	}
        return null;
    }

    /** Return the immediate parent of c (null if c is not in this
     * PostingTree).
     * <p>
     * Thus, for the following tree:
     * Depth level:
     * 0        A
     *         / \
     * 1      B   C
     *       /   / \
     * 2    D   E   F
     *                \
     * 3               G
     * <p>
     * A.getParent(E) returns C.
     * C.getParent(E) returns C.
     * A.getParent(B) returns A.
     * E.getParent(F) returns null.
     */
    public Person getParent(Person c) {
        // Base case
        for (PostingTree dt : children) {
            if (dt.root == c) return root;
        }

        // Recursive case - ask children to look
        for (PostingTree dt : children) {
            Person parent = dt.getParent(c);
            if (parent != null) return parent;
        }

        return null; //Not found
    }

    /** If either child1 or child2 is null or is not in this SharingTree, return null.
     * Otherwise, return the person at the root of the smallest subtree of this
     * SharingTree that contains child1 and child2.
     * <p>
     * Examples. For the following tree (which does not contain H):
     * <p>
     * Depth level:
     * 0      A
     *       / \
     * 1    B   C
     *     /   / \
     * 2  D   E   F
     *     \
     * 3    G
     * <p>
     * A.getSharedAncestor(B, A) is A
     * A.getSharedAncestor(B, B) is B
     * A.getSharedAncestor(B, C) is A
     * A.getSharedAncestor(A, C) is A
     * A.getSharedAncestor(E, F) is C
     * A.getSharedAncestor(G, F) is A
     * B.getSharedAncestor(B, E) is null
     * B.getSharedAncestor(B, A) is null
     * B.getSharedAncestor(D, F) is null
     * B.getSharedAncestor(D, H) is null
     * A.getSharedAncestor(null, C) is null
     */
    public Person getSharedAncestor(Person child1, Person child2) {
        //TODO 6
        // It's possible to get a lot of getParent calls to do this,
        // but that can be inefficient. Don't do it; points will be lost.
        // Instead, build the two sharing routes to child1 and child2 and 
        // then compute the largest k such that l1[0..k] and l2[0..k] are equal.
    	List<Person> l1 = getPostingRoute(child1);
    	List<Person> l2 = getPostingRoute(child2);
    	Person[] array1 = l1.toArray(new Person[0]);
    	Person[] array2 = l2.toArray(new Person[0]);
    	int i = 0;
    	for (; (i < array1.length) && (i < array2.length); i++)
    	{
    		if (!(array1[i].equals(array2[i])))
    		{
    			if (i == 0)
    				return null;
    			return array1[i-1];
    		}
    	}
    	if (i > 0)
    		return array1[i-1];
        return null;

    }

    /** Return a (single line) String representation of this PostingTree.
     * If this PostingTree has no children (it is a leaf), return the root's substring.
     * Otherwise, return
     * root's substring + SEPARATOR + START_CHILDREN_DELIMITER + each child's
     * toString, separated by DELIMITER, followed by END_CHILD_DELIMITER.
     * Make sure there is not an extra DELIMITER following the last child.
     * <p>
     * Finally, make sure to use the static final fields declared at the top of
     * SharingTree.java.
     * <p>
     * Thus, for the following tree:
     * Depth level:
     * 0      A
     *       / \
     * 1    B   C
     *     /   / \
     * 2  D   E   F
     *         \
     * 3        G
     * A.toString() should print:
     * (A) - HEALTHY - [(C) - HEALTHY - [(F) - HEALTHY, (E) - HEALTHY - [(G) - HEALTHY]], (B) - HEALTHY - [(D) - HEALTHY]]
     * <p>
     * C.toString() should print:
     * (C) - HEALTHY - [(F) - HEALTHY, (E) - HEALTHY - [(G) - HEALTHY]]
     */
    public String toString() {
        if (children.isEmpty()) return root.toString();
        String s = root.toString() + SEPARATOR + START_CHILDREN_DELIMITER;
        for (PostingTree dt : children) {
            s = s + dt.toString() + DELIMITER;
        }
        return s.substring(0, s.length() - 2) + END_CHILDREN_DELIMITER;
    }


    /** Return a verbose (multi-line) string representing this PostingTree. */
    public String toStringVerbose() {
        return toStringVerbose(0);
    }

    /** Return a verbose (multi-line) string representing this PostingTree.
     * Each person in the tree is on its own line, with indentation representing
     * what each person is a child of.
     * indent is the the amount of indentation to put before this line.
     * Should increase on recursive calls to children to create the above pattern.
     * Thus, for the following tree:
     * Depth level:
     * 0      A
     *       / \
     * 1    B   C
     *     /   / \
     * 2  D   E   F
     *         \
     * 3        G
     * <p>
     * A.toStringVerbose(0) should return:
     * (A) - HEALTHY
     *    (C) - HEALTHY
     *       (F) - HEALTHY
     *       (E) - HEALTHY
     *         (G) - HEALTHY
     *    (B) - HEALTHY
     *        (D) - HEALTHY
     * <p>
     * Make sure to use VERBOSE_SPACE_INCREMENT for indentation.
     */
    private String toStringVerbose(int indent) {
        String s = "";
        for (int i = 0; i < indent; i++) {
            s = s + VERBOSE_SPACE_INCREMENT;
        }
        s = s + root.toString();

        if (children.isEmpty()) return s;

        for (PostingTree dt : children) {
            s = s + "\n" + dt.toStringVerbose(indent + 1);
        }
        return s;
    }

    /** Return true iff this is equal to ob.
     * 1. If ob is not a PostingTree, it cannot equal this PostingTree, return false.
     * 2. Two PostingTrees are equal if
     * <br> - (1) they have the same root Person object (==)    and
     * <br> - (2) their children sets are the same size     and
     * <br> --(3) their children sets are equal.
     * <br>       Since their sizes are equal, this requires:
     * <br> ---       for every PostingTree rt in one set there is a PostingTree 
     *                rt2 in the other set for which rt.equals(rt2) is true.
     * <p>
     * Otherwise the two RepostTrees are not equal.
     * Do not use any of the toString functions to write equals(). */
    public boolean equals(Object ob) {
        //TODO 7
        // Hint about checking whether each child of one tree equals SOME
        // other tree of the other tree's children.
        // First, you have to check them all until you find an equal one (or
        //     return false if you don't.)
        // Second, A child of one tree cannot equal more than one child of
        //    tree because the names of Person's are all unique;
        //    there are no duplicates.
        if (this.getClass() != ob.getClass())
        	return false;
        PostingTree obj = (PostingTree) ob;
        if (root != obj.getRoot())
        	return false;
        if (getChildrenCount() != obj.getChildrenCount())
        	return false;
        for (PostingTree ch : children)
        {
        	boolean found = false;
        	for (PostingTree ch1 : obj.getChildren())
        	{
        		if(ch.equals(ch1))
        		{
        			found = true;
        			break;
        		}
        	}
        	if (!found)
        		return false;
        }
    	
        return true;

    }

}
