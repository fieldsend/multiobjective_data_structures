import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Iterator;

/**
 * Write a description of class BSPTreeArchive here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BSPTreeArchive implements ParetoSetManager
{
    private BSPTreeNode root;
    private int maxLeafSize;
    private final int NUMBER_OF_OBJECTIVES;

    BSPTreeArchive(int numberOfObjectives, int maxLeafSize) {
        root = new BSPTreeNode(new ArrayList<Solution>(maxLeafSize+1), numberOfObjectives, null);
        this.maxLeafSize = maxLeafSize;
        NUMBER_OF_OBJECTIVES = numberOfObjectives;
    }

    public boolean add(Solution s) throws IllegalNumberOfObjectivesException
    {
        if (s.getNumberOfObjectives() != NUMBER_OF_OBJECTIVES)
            throw new IllegalNumberOfObjectivesException("ARchive maintains solutions with " 
                + NUMBER_OF_OBJECTIVES + " number of objectives, not " + s.getNumberOfObjectives());
        if (checkDominance(root,s,new TreeSet<Integer>(),new TreeSet<Integer>()) < 0 )
            return false;
        BSPTreeNode node =  root;
        while (node.isInteriorNode()) {
            node.incrementNumberCovered();
            node = node.getChild(s);
        }
        node.addToSet(s,maxLeafSize);
        return true;
    }

    private int processLeafForDominanceCheck(BSPTreeNode node, Solution s)
    {
        int k=0;
        ArrayList<Solution> setCovered = node.getCoveredSet();
        Iterator<Solution> iterator = setCovered.iterator();
        while (iterator.hasNext()) {
            Solution member  = iterator.next();
            //System.out.println("Comparing " + member +" to "+ s);
            if (member.weaklyDominates(s))
                return -1;
            /* Next line is effectively a dominates check, as would have returned 
             * if equal already, weakDom at this point is quicker
             */    
            if (s.weaklyDominates(member)) { 
                iterator.remove();
                node.decrementNumberCovered();
                k++;
            }
        }
        return k;
    }

    
    private int checkDominance(BSPTreeNode node, Solution s,  TreeSet<Integer> b, TreeSet<Integer> w) {
        int k = 0;
        if (!node.isInteriorNode()) {
            k = processLeafForDominanceCheck(node,s);
        }
        if (node.isInteriorNode()) {
            TreeSet<Integer> wPrime;
            TreeSet<Integer> bPrime; 
            // update B to B' or W to W'
            if (s.getFitness(node.getObjectiveIndex()) < node.getTheta()) {
                bPrime  = new TreeSet<>(b); 
                bPrime.add(node.getObjectiveIndex());
                wPrime  = w;
            } else { 
                wPrime  = new TreeSet<>(w);
                wPrime.add(node.getObjectiveIndex());
                bPrime = b;
            }
            if (w.size() == NUMBER_OF_OBJECTIVES)   
                return -1;
            else if (b.size() == NUMBER_OF_OBJECTIVES) {
                k += node.getRight().getNumberCovered();
                node.getRight().removeAllCovered();
            } else {
                //System.out.println(wPrime.size() + " "+ w.size() +  " " + bPrime.size() + " "+ b.size());
                if ((wPrime.size()==0) || (b.size()==0)) {
                    //System.out.println("Check left index " + node.getObjectiveIndex() + " < " + node.getTheta() + " " + wPrime.size() + " "+ b.size());
                    int v = checkDominance(node.getLeft(),s,b,wPrime);
                    if (v<0) {
                        //System.out.println("DOMINATED");
                        return -1;
                    }
                    k+=v;    
                }
                if ((bPrime.size()==0) || (w.size()==0)) {
                    //System.out.println("Check right index " + node.getObjectiveIndex() + " >= " + node.getTheta() + " "+ w.size() + " " +bPrime.size());
                    int v = checkDominance(node.getRight(),s,bPrime,w);
                    if (v<0) {
                        //System.out.println("DOMINATED");
                        return -1;
                    }
                    k+=v; 
                }
            }
            node.decrementNumberCovered(k);
            if ((node.getLeft().getNumberCovered() > 0) && (node.getRight().getNumberCovered() == 0)) {
                if  (node.getParent().getLeft()==node) // find if node is the left or right child of parent
                    node.getParent().setLeft(node.getLeft()); // swap in left to replace node
                else
                    node.getParent().setRight(node.getLeft()); // swap in left to replace node
                node.setParent(null); // detatch node
            }
            if ((node.getRight().getNumberCovered() > 0) && (node.getLeft().getNumberCovered() == 0)) {
                if  (node.getParent().getLeft()==node) // find if node is the left or right child of parent
                    node.getParent().setLeft(node.getRight()); // swap in right to replace node
                else
                    node.getParent().setRight(node.getRight()); // swap in right to replace node
                node.setParent(null); // detatch node
            }
        }
        return k;
    }

    private int checkForDominanceWithoutChangingState(BSPTreeNode node, Solution s,  TreeSet<Integer> b, TreeSet<Integer> w) {
        int k = 0;
        if (!node.isInteriorNode()) {
            k = processLeafForDominanceCheck(node,s);
        }
        if (node.isInteriorNode()) {
            TreeSet<Integer> wPrime;
            TreeSet<Integer> bPrime; 
            // update B to B' and W to W'
            if (s.getFitness(node.getObjectiveIndex()) < node.getTheta()) {
                bPrime  = new TreeSet<>(b); 
                b.add(node.getObjectiveIndex());
                wPrime  = w;
            } else { 
                wPrime  = new TreeSet<>(w);
                w.add(node.getObjectiveIndex());
                bPrime = b;
            }
            if (w.size() == NUMBER_OF_OBJECTIVES)   
                return -1;
            else if (b.size() == NUMBER_OF_OBJECTIVES) {
                k += node.getRight().getNumberCovered();
                node.getRight().removeAllCovered();
            } else {
                if ((wPrime.size()==0) || (b.size()==0)) {
                    int v = checkForDominanceWithoutChangingState(node.getLeft(),s,b,wPrime);
                    if (v<0)
                        return -1;
                    k+=v;
                }
                if ((bPrime.size()==0) || (w.size()==0)) {
                    int v = checkForDominanceWithoutChangingState(node.getRight(),s,bPrime,w);
                    if (v<0)
                        return -1;
                    k+=v;
                }
            }
        }
        return k;
    }

    public boolean weaklyDominates(Solution s) throws IllegalNumberOfObjectivesException
    {
        return (checkForDominanceWithoutChangingState(root,s,new TreeSet<Integer>(),new TreeSet<Integer>())<0);
    }

    public Collection<? extends Solution> getContents() {
         ArrayList<Solution> contents = new ArrayList<Solution>(this.size());
         recursivelyFillWithContents(root, contents);
         return contents;
    }
    
    private void recursivelyFillWithContents(BSPTreeNode node, ArrayList<Solution> contents) {
        if (node.isInteriorNode()) {
            recursivelyFillWithContents(node.getLeft(), contents);
            recursivelyFillWithContents(node.getRight(), contents);
        } else {
            contents.addAll(node.getCoveredSet());
        }
    }

    public Solution getRandomMember(){
        return null;
    }

    public int size() {
        return root.getNumberCovered();
    }

    public void clean() {
        root = null;
    }

    @Override
    public String toString() {
        return rescursivelyAddToString(root," ");
    }
    
    private String rescursivelyAddToString(BSPTreeNode node, String s) {
        if (node.isInteriorNode()) {
            s += " index " + node.getObjectiveIndex() + " < " + node.getTheta() + " " + rescursivelyAddToString(node.getLeft(), s);
            s += " index " + node.getObjectiveIndex() + " >= " + node.getTheta() + " " + rescursivelyAddToString(node.getRight(), s);
        } else {
            s += "\n contents: ";
            Collection<? extends Solution> temp = node.getCoveredSet();
            for (Solution member : temp)
                s += " " + member + ", ";
        }
        return s;
    }
    
    
    public static ParetoSetManager managerFactory(int numberOfObjectives, int maxLeafSize) {
        return new BSPTreeArchive(numberOfObjectives,maxLeafSize);
    }

    public static ParetoSetManager managerFactory(int numberOfObjectives) {
        return new BSPTreeArchive(numberOfObjectives,1);
    }
}
