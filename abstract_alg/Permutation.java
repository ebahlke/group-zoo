package abstract_alg;
/* Name: Permutation.java
 * Author: Emma Bahlke
 * Date: May 22, 2014
 * Comments: Describes an element of a symmetric/permutation group -- essentially,
 * one possible bijective mapping on the set {1, ..., n}. */

import java.util.*;

public class Permutation extends GroupElement {
  private int size;
  private Hashtable<Integer, Integer> functionVals;
  
  public Permutation(int ... yValues) {
    size = yValues.length;
    functionVals = new Hashtable<Integer, Integer>(size);
    int xValue = 1;
    
    for (int i = 0; i < size; i++) {
      functionVals.put(xValue, yValues[i]);
      xValue++;
    }
  }
  
  public Permutation(LinkedList<Integer> yValues) {
    size = yValues.size();
    functionVals = new Hashtable<Integer, Integer>(size);
    int xValue = 1;
    
    for (int i = 0; i < size; i++) {
      functionVals.put(xValue, yValues.get(i));
      xValue++;
    }
  }
  
  public Hashtable<Integer, Integer> getFunctionVals() {
    return functionVals;
  }
  
  public int size() {
    return size;
  }
  
  public boolean equals(GroupElement e) {
    Permutation another = (Permutation)e;
    if (another.size() != this.size)
      return false;
    else{
      for (int i = 1; i <= size; i++) {
        if (functionVals.get(i) != another.getFunctionVals().get(i))
          return false;
      }
      return true;
    }
  }
  
  public String toString() {
    String s = "";
    LinkedList<Integer> nonFixedVals = getNonFixedVals();
    if (nonFixedVals.size() == 0)
      return "identity";
    
    else {
      while (nonFixedVals.size() > 0) {
        // print each cycle in turn by removing the values from nonFixedVals as we print them
        // and adding a ")" at the end of each cycle:
        s+="(";
        LinkedList<Integer> currentCycle = getFullCycle(nonFixedVals.get(0));
        
        for (int i = 0; i < currentCycle.size(); i++) {
          s+=currentCycle.get(i)+"";
          nonFixedVals.removeFirstOccurrence(currentCycle.get(i));
          if (i == currentCycle.size()-1)
            s+=")";
        }
      }
      
      return s;
    }
  }
  
  /* Generates the full cycle that can be traversed from the given start value. */
  public LinkedList<Integer> getFullCycle(int startVal) {
    LinkedList<Integer> cycleSoFar = new LinkedList<Integer>();
    int currentVal = startVal;
    
    while (!cycleSoFar.contains(currentVal)) {
      cycleSoFar.add(currentVal);
      currentVal = functionVals.get(currentVal);
    }
    
    return cycleSoFar;
  }
  
  /* Returns the NUMBER of integers that are not fixed by this mapping,
   * i.e. that are sent to a value other than themselves - note that it
   * does not return a list of these values. (see getNonFixedVals()) */
  public int nonFixedInts() {
    int acc = 0;
    for (int i = 1; i <= size; i++) {
      if (functionVals.get(i) == i)
        continue;
      else acc++;
    }
    return acc;
  }
  
  /* Returns all the values of the mapping that aren't fixed, i.e. are sent to
   * something other than themselves. */
  public LinkedList<Integer> getNonFixedVals() {
    LinkedList<Integer> nonFixedVals = new LinkedList<Integer>();
    
    for (int i = 1; i <= size; i++) {
      if (functionVals.get(i) != i)
        nonFixedVals.add(i);
    }
    
    return nonFixedVals;
  }
  
  public static void main(String[] args) {
//    Permutation p = new Permutation(4, 3, 2, 1);
//    System.out.println("Permutation 1 (2 cycles): " + p.toString());
//    
//    Permutation q = new Permutation(1, 2, 3, 4);
//    System.out.println("Permutation 2 (identity): " + q.toString());
//    
//    Permutation r = new Permutation(3, 2, 4, 1);
//    System.out.println("Permutation 3 (1 cycle, integer 2 fixed): " + r.toString());
//    
//    System.out.println("p equals r? " + p.equals(r));
//    System.out.println("p equals p? " + p.equals(p));
  }
  }