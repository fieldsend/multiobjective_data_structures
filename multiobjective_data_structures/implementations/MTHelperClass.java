package multiobjective_data_structures.implementations;
import multiobjective_data_structures.*;


import java.util.ArrayList;
/**
 * MTHelperClass provides a set of helper methods required by the various
 * Quad Tree implementations
 * 
 * @author Jonathan Fieldsend
 * @version 1.0
 */
public class MTHelperClass
{
    static void setUpMatrices(int MIN_INDEX, int MAX_INDEX, int[][] lChildrenSet0IndicesLessThanK, int[][] lChildrenSet1IndicesLessThanL, int[][] lChildrenSet0Indices, int[][] lChildrenSet1Indices) {
        
        // Set up matrices of mappings
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            ArrayList<Integer> tempZeros = new ArrayList<>(); 
            for (int j = MIN_INDEX+1; j < i; j++){
                // use bitwise operators to check if has all zeros in same place;
                if ((~i & ~j) == ~i) {
                    tempZeros.add(j);
                }
            }
            lChildrenSet0IndicesLessThanK[i] = new int[tempZeros.size()];
            for (int j=0; j<tempZeros.size(); j++)
                lChildrenSet0IndicesLessThanK[i][j] = tempZeros.get(j);
            for (int j = i; j < MAX_INDEX; j++){
                // use bitwise operators to check if has all zeros in same place;
                if ((~i & ~j) == ~i) {
                    tempZeros.add(j);
                }
            }
            lChildrenSet0Indices[i] = new int[tempZeros.size()];
            for (int j=0; j<tempZeros.size(); j++)
                lChildrenSet0Indices[i][j] = tempZeros.get(j);    
                
                
            ArrayList<Integer> tempOnes = new ArrayList<>(); 
            for (int j = i + 1; j < MAX_INDEX; j++){
                // use bitwise operators to check if has all ones in same place;
                if ((i & j) == i) {
                    tempOnes.add(j);
                }
            }
            lChildrenSet1IndicesLessThanL[i] = new int[tempOnes.size()];
            for (int j=0; j<tempOnes.size(); j++)
                lChildrenSet1IndicesLessThanL[i][j] = tempOnes.get(j);
            for (int j = MIN_INDEX+1; j < i+1; j++){
                // use bitwise operators to check if has all ones in same place;
                if ((i & j) == i) {
                    tempOnes.add(j);
                }
            }
            lChildrenSet1Indices[i] = new int[tempOnes.size()];
            for (int j=0; j<tempOnes.size(); j++)
                lChildrenSet1Indices[i][j] = tempOnes.get(j);    
                  
        }

        //printMatrices(MIN_INDEX, MAX_INDEX, lChildrenSet0IndicesLessThanK,lChildrenSet1IndicesLessThanL,lChildrenSet0Indices,lChildrenSet1Indices);
    }

    static void printMatrices(int MIN_INDEX, int MAX_INDEX, int[][] lChildrenSet0IndicesLessThanK, int[][] lChildrenSet1IndicesLessThanL, int[][] lChildrenSet0Indices, int[][] lChildrenSet1Indices) {
        System.out.println("LchildrenSet0IndicesLessThanK");
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            System.out.print("Index: " + i + "---");
            for (int j=0; j<lChildrenSet0IndicesLessThanK[i].length; j++)
                System.out.print(", " + lChildrenSet0IndicesLessThanK[i][j]);
            System.out.println();  
        }

        System.out.println("LchildrenSet1IndicesLessThanL");
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            System.out.print("Index: " + i + "---");
            for (int j=0; j<lChildrenSet1IndicesLessThanL[i].length; j++)
                System.out.print(", " + lChildrenSet1IndicesLessThanL[i][j]);
            System.out.println();  
        }
        
        System.out.println("LchildrenSet0Indices");
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            System.out.print("Index: " + i + "---");
            for (int j=0; j<lChildrenSet0Indices[i].length; j++)
                System.out.print(", " + lChildrenSet0Indices[i][j]);
            System.out.println();  
        }

        System.out.println("LchildrenSet1Indices");
        for (int i = MIN_INDEX+1; i < MAX_INDEX; i++) {
            System.out.print("Index: " + i + "---");
            for (int j=0; j<lChildrenSet1Indices[i].length; j++)
                System.out.print(", " + lChildrenSet1Indices[i][j]);
            System.out.println();  
        }
    }
}
