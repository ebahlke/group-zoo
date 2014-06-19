package abstract_alg;
/* NAME: ZmodN.java
 * AUTHORS: Emma Bahlke (Primary), Alexi Block Gorman, and Meredith McCormack-Mager
 * DATE: May 2014
 * COMMENTS: Extends the abstract Group class by implementing a representation
 * of the integers modulo N.  Users can add two elements and find the inverse or
 * order of an element mod N and generate subgroups.
 * */

import java.util.*;

public class ZmodN extends Group {
  // instance variables inherited from Group:
  // LL<GroupElement> members, int dimension, Hashtable<Integer, LL<GroupElement>> membersByOrder, LL<int> allOrders
  // methods inherited from Group:
  // - equals (OVERWRITTEN), getIdentity, computeElementOrder, generateSubgroup, StoreElementsByOrder, getElementsByOrder,
  //   orderedElementsToString, isNormal, getMembers, and protected helpers:
  //     - findIndex, subgroupEquals, contains, isPrime, and two versions of addInOrder (overwritten here)
  
  private final String NOT_AN_INT_ERROR = "ERROR: Please enter only modular integer inputs.";
  
  public ZmodN(int n) {
    super(n);
    identity = new ModularInt(0);
    
    // populate Z mod N with all the integers from 0 to n-1,
    // with their orders pre-assigned:
    for (int i = 0; i < n; i++) {
      ModularInt next = new ModularInt(i);
      next.setOrder(computeElementOrder(next));
      members.add(next);
    }
  }
  
  /* In the case of Z mod N, the "group order" (number of elements in the
   * group) is simply the "dimension" of the group.  To contrast, in
   * the permutation group, the number of elements is the factorial function
   * of the dimension, i.e. the permutation group S_4 (with dimension 4)
   * has 24 (= 4!) elements. */
  public int getGroupOrder() {
    return dimension;
  }
  
  /* Performs addition mod N on two integers n and m, returning the resulting
   * equivalence class. */
  public GroupElement operate(GroupElement n, GroupElement m) {
    if (!isModularInt(n, m)) {
      System.out.println(NOT_AN_INT_ERROR);
      return identity;
    }
    
    ModularInt nprime = (ModularInt)n;
    ModularInt mprime = (ModularInt)m;
    return (new ModularInt((nprime.getIntValue()+mprime.getIntValue())%dimension));
  }
  
  /* Determines whether or not two integers n and m are congruent mod N,
   * that is, whether or not they belong to the same equivalence class. */
  public boolean equals(GroupElement n, GroupElement m) {
    if (!isModularInt(n, m)) {
      System.out.println(NOT_AN_INT_ERROR);
      return false;
    }
    ModularInt nprime = (ModularInt)n;
    ModularInt mprime = (ModularInt)m;
    return (nprime.getIntValue()%dimension == mprime.getIntValue()%dimension);
  }
  
  /* Finds the inverse of a given ModularInt, i.e. the equivalence class [a]
   * such that [a] + [n] = [0]. */
  public GroupElement getInverse(GroupElement n) {
    if (!isModularInt(n)) {
      System.out.println(NOT_AN_INT_ERROR);
      return identity;
    }
    
    ModularInt residueValueOfN = getResidueValue(n);
    if (residueValueOfN.getIntValue() == 0)
      return (ModularInt)identity;
    else return new ModularInt(dimension-residueValueOfN.getIntValue());
  }
  
  /* ToString: */
  public String toString() {
    String s = "The integers mod " + dimension + ":\n";
    
    if (dimension <= 20) {
      for (int i = 0; i < dimension; i++) {
        s+=members.get(i).toString()+", ";
      }
      return s.substring(0,s.length()-2); // remove trailing ", "
    }
    
    else {
      s+="\n"+members.get(0).toString()+", "+members.get(1).toString()+
        ", ... , "+members.get(dimension-2).toString()+", "+members.get(dimension-1).toString();
      return s;
    }
  }
  
  public String groupName() {
    return "Z" + dimension;
  }
  
  /* Returns true if all the given GroupElements are ModularInts,
   * false otherwise. */
  private boolean isModularInt(GroupElement ... args) {
    for (int i = 0; i < args.length; i++) {
      if (!(args[i] instanceof ModularInt))
        return false;
    }
    
    return true;
  }
  
  /* Adds a new modular int to a linked list of modular ints by placing
   * it in the appropriate position relative to the existing members of
   * the list.  Here, the "appropriate position" is determined by the residue
   * value of the integer mod N, so the integer with the least residue value comes
   * first and the integer with the highest residue value comes last.  If two
   * integers have the same residue value, they are placed side by side. */
  protected LinkedList<GroupElement> addInOrder(LinkedList<GroupElement> listSoFar, GroupElement toAdd) {
    for (int i = 0; i < listSoFar.size(); i++) {
      if (getResidueValue(toAdd).getIntValue() <=
    		  getResidueValue(listSoFar.get(i)).getIntValue()) {
        listSoFar.add(i, toAdd);
        return listSoFar;
      }
    }
    // otherwise, the toAdd element has the highest order so far and we just add it at the end:
    listSoFar.add(toAdd);
    return listSoFar;
  }
  
  /* Returns the least positive residue value of n, i.e. the
   * "default"/primary representative of its equivalence class. */
  private ModularInt getResidueValue(GroupElement n) {
	  if (!isModularInt(n)) {
		  System.out.println(NOT_AN_INT_ERROR);
		  return new ModularInt(-1);
	  }
	  ModularInt nprime = (ModularInt)n;
	  if (nprime.getIntValue() >= 0)
		  return new ModularInt(nprime.getIntValue()%dimension);
	  else
		  return new ModularInt(dimension + (nprime.getIntValue()%dimension));
  }
  
  public static void main(String[] args) {
//    ZmodN fours = new ZmodN(4);
//    ModularInt three = new ModularInt(3);
//    ModularInt two = new ModularInt(2);
//    
//    System.out.println("Zmod4.getGroupOrder(): " + fours.getGroupOrder());
//    System.out.println("Zmod4.operate(three, two) should return one. Returns: " +
//                       fours.operate(three, two));
//    System.out.println("Zmod4.equals(two, three) should return false. Returns: " +
//                       fours.equals(two, three));
//    System.out.println("Zmod4.equals(three, new ModularInt(7)) should return true. Returns: " +
//                       fours.equals(three, new ModularInt(7)));
//    System.out.println("Zmod4.computeElementOrder(three) should return four. Returns: " +
//                       fours.computeElementOrder(three));
//    System.out.println("Zmod4.generateSubgroup(three) should return Zmod4. Returns: " +
//                       fours.generateSubgroup(three));
//    System.out.println("Zmod4.generateSubgroup(two) should return [0], [2]. Returns: " +
//                       fours.generateSubgroup(two));
//    System.out.println("Zmod4.getInverse(new ModularInt(6)) should return [2]. Returns: " +
//                       fours.getInverse(new ModularInt(6)));
//    System.out.println("Does <[3]> = <[3]>? (yes): " +
//                       fours.subgroupEquals(fours.generateSubgroup(three), fours.generateSubgroup(three)));
//    System.out.println("Does <[3]> = <[2]>? (no): " +
//                       fours.subgroupEquals(fours.generateSubgroup(three), fours.generateSubgroup(two)));
//    System.out.println("Is <[3]> a normal subgroup? (yes): " +
//                       fours.isNormal(fours.generateSubgroup(three)));
//    System.out.println("Zmod4.findIndex(fours.getMembers(), 1) should give 1: " +
//                       fours.findIndex(fours.getMembers(), new ModularInt(1)));
//    System.out.println("Zmod4.findIndex(<[2]>, 1) should give -1: " +
//                       fours.findIndex(fours.generateSubgroup(new ModularInt(2)), new ModularInt(5)));
  }
}