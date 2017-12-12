import java.util.HashSet;

/**
 * Write a description of class CompositePoint here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class XXCompositePoint //implements Solution, Comparable<XXCompositePoint>
{
//     private XXCompositePointTracker[] solutions;
//     private HashSet<XXCompositePointTracker> uniqueSolutions = new HashSet<>();
//     private XXCompositePoint previous;
//     private XXCompositePoint next;
//     private boolean queryPoint = false;
// 
//     XXCompositePoint(int numberOfObjectives) {
//         solutions = new CompositePointTracker[numberOfObjectives];
//         uniqueSolutions = new HashSet<>(numberOfObjectives+1);
//     }
// 
//     XXCompositePoint(XXCompositePointTracker s) {
//         solutions = new CompositePointTracker[s.getNumberOfObjectives()];
//         for (int i=0; i< solutions.length; i++)
//             solutions[i] = s;
//         uniqueSolutions.add(s);    
//     }
// 
//     XXCompositePoint(XXCompositePointTracker s, boolean queryPoint) {
//         this(s);
//         this.queryPoint = queryPoint;
//     }
// 
//     XXCompositePoint getPrevious() {
//         return previous;
//     }
// 
//     XXCompositePoint getNext() {
//         return next;
//     }
// 
//     boolean isDuplicatingPrevious() {
//         System.out.println("duplicate check :" +  this + ", previous: " + previous);
//         
//         if (previous==null)
//             return false;
//         if (uniqueSolutions.size()!=previous.uniqueSolutions.size())
//             return false;
//         boolean contentsMatch = uniqueSolutions.containsAll(previous.uniqueSolutions);
//         for (CompositePointTracker t : uniqueSolutions) {
//             System.out.println("this member " + t);
//             System.out.println("this member " + t.getWrappedSolution());
//         }
//         for (CompositePointTracker t : previous.uniqueSolutions) {
//             System.out.println("other member " + t);
//             System.out.println("this member " + t.getWrappedSolution());
//         }
//         System.out.println("Contents match: " + contentsMatch);
//         return contentsMatch;
//     }
//     
//     void setPrevious(XXCompositePoint previous) {
//         this.previous = previous;
//     }
// 
//     void setNext(XXCompositePoint next) {
//         this.next = next;
//     }
// 
//     boolean anyElementWeaklyDominates(Solution s) {
//         for (CompositePointTracker c : uniqueSolutions) 
//             if (c.getWrappedSolution().weaklyDominates(s)) 
//                 return true;
//         return false;        
//     }
// 
//     void purgeAll(){
//         for (CompositePointTracker t : uniqueSolutions)
//             t.removeFromToDominatedTreeTracking(this);
//     }
//     
//     boolean purge(XXCompositePointTracker s) {
//         return uniqueSolutions.remove(s);
//     }
//     
//     void setElement(int index, XXCompositePointTracker s) {
//         if (solutions[index]!=null) {
//             uniqueSolutions.remove(solutions[index]);
//         }
//         solutions[index] = s;
//         uniqueSolutions.add(s);
//     }
// 
//     int getNumberOfUniqueElements() {
//         return uniqueSolutions.size();
//     }
// 
//     HashSet<XXCompositePointTracker> getUniqueElements() {
//         return uniqueSolutions;
//     }
// 
//     XXCompositePointTracker getElement(int index) {
//         return solutions[index];
//     }
// 
//     /*
//      * Returns true if dominator used to replace solution at all
//      */
//     boolean cleanAndReplaceDominatedTree(LLDominatedTree tree, XXCompositePointTracker toRemove, XXCompositePointTracker dominator) {
//         System.out.println("REMOVING>>> "+ toRemove.getWrappedSolution());
//         System.out.println("FROM>>> " + tree);
//         System.out.println("FROM CP >>> " + this);
//         uniqueSolutions.remove(toRemove);
//         if (previous!=null) {
//             XXCompositePointTracker[] toAdd = new XXCompositePointTracker[dominator.getNumberOfObjectives()];
//             XXCompositePointTracker[] toClean = new XXCompositePointTracker[dominator.getNumberOfObjectives()];
//             int index=0;
//             for (int i=0; i<solutions.length; i++) {
//                 if (solutions[i] == toRemove) {
//                     solutions[i] = previous.getElement(i);
//                     uniqueSolutions.add(previous.getElement(i));
//                     toAdd[index++] = previous.getElement(i);
//                 } 
//             }
//             if (isDuplicatingPrevious()) { // remove any node if duplicate
//                 System.out.println("~~~~DUPLICATE REMOVAL");
//                 if (this == tree.getWorst()) // if tail
//                     tree.removeWorst();
//                 else
//                     tree.removeComponent(this);
//             } else {
//                 for (int i=0; i<index; i++)
//                     toAdd[i].addToDominatedTreeTracking(this);
//             }
//             return false;
//         } 
//         System.out.println("REMOVING FROM HEAD");
//         // this composite point is the head of the list
//         uniqueSolutions.add(dominator);
//         dominator.addToDominatedTreeTracking(this);
//         System.out.println("CP HEAD "+ this);
//         for (int i=0; i<solutions.length; i++)
//             if (solutions[i] == toRemove) 
//                 solutions[i] = dominator;
//            
//         return true;
//     }
// 
//     public String stripOutContents() {
//         String s = "UniqueSolutions in node -- " + uniqueSolutions.size();
//         for (CompositePointTracker t : uniqueSolutions) {
//             s+=", Solution: " +t.getWrappedSolution() + " is in CPs:";
//             for (CompositePoint c : t.getDominatedTreeMembership())
//                 s+=c + ", ";
//             s+="\n";    
//         }
//         return s;   
//     }
//     
//     @Override 
//     public String toString() {
//         String s = "Objective Values-- ";
//         for (int i=0; i<getNumberOfObjectives(); i++) 
//             s+= " : " + getFitness(i);
//         return s;    
//     }
// 
//     @Override
//     public double getFitness(int index){
//         return solutions[index].getFitness(index); 
//     }
// 
//     @Override
//     public void setFitness(int index, double value){
//         solutions[index].setFitness(index,value);
//     }
// 
//     @Override
//     public void setFitness(double[] fitnesses) {
//         for (int i=0; i<solutions.length; i++)
//             solutions[i].setFitness(i,fitnesses[i]);
//     }
// 
//     @Override
//     public int getNumberOfObjectives() {
//         return solutions.length;
//     }
// 
//     /**
//      * Returns -1 if this dominates the argument (or objectives the same but the internal solutions are 'lower'), 
//      * 0 if they are the same including all the same solutions and 1 if this is 
//      * dominated by the argument (or objectives same but internal solutions are higher). Assumes that the composite points you have constructed in 
//      * your dominated or non-dominated tree are correctly formed, in that none should be
//      * mutually non-dominating
//      */
//     @Override
//     public int compareTo(XXCompositePoint c) {
//         if (this==c)
//             return 0;
//         System.out.println("this comparitor" + this);
//         System.out.println("argument comparitor" +  c);
//         /*if (this.queryPoint){ // could do by type...
//         if (this.strictlyDominates(c))
//         return -1;
//         return 1;
//         }*/
// 
//         if (this.dominates(c))
//             return -1;
//         if (c.dominates(this))
//             return 1;    
//         if (this.isFitnessTheSame(c)) {
//             // same objective values, but still need to order unless identical solutions inside
//             for (int i=0; i<solutions.length; i++) {
//                 int hc1 = solutions[i].hashCode();
//                 int hc2 = c.solutions[i].hashCode();
//                 if (hc1<hc2)
//                     return -1;
//                 if (hc1>hc2)
//                     return 1;
//             }
//             return 0;  
//         }
//         return 0; // mutually non-dominating -- returning 0 as will be needed when dentifying ranges
//     }
}
