package abstract_alg;
/* NAME: PermutationGroup.java
 * AUTHOR: Emma Bahlke
 * DATE: May 22, 2014
 * COMMENTS: An implementation of the group of permutations on the set {1, ..., n}.
 * Allows users to compose two (or more, using the chainOperate method) permutations,
 * find the inverse of a permutation, find the two-cycle decomposition of a permutation,
 * and all other methods stipulated in the Group class. It works more or less comfortably
 * on my computer up to n = 7, and starts giving out-of-memory errors after that. */

import java.util.*;

public class PermutationGroup extends Group {
  private final String NOT_A_PERM_ERROR = "ERROR: Please input only Permutations to methods in PermutationGroup.";
  // instance variables inherited from Group:
  // LL<GroupElement> members, int dimension, Hashtable<Integer, LL<GroupElement>> membersByOrder, LL<int> allOrders
  // methods inherited from Group:
  // - equals (OVERWRITTEN), getIdentity, computeElementOrder, generateSubgroup, StoreElementsByOrder, getElementsByOrder,
  //   orderedElementsToString, isNormal, getMembers, and protected helpers:
  //     - findIndex, subgroupEquals, contains, isPrime, and two versions of addInOrder
  
  public PermutationGroup(int n) {
    super(n);
    
    if (n >= 7) {
      System.out.println("NOTE: Because you've chosen such a high order for your permutation group, elements will not "
                           + "have their order pre-set in the list of group members, and will not be added to this list in any particular order.");
      LinkedList<LinkedList<Integer>> allMappings = generateOrderings(oneToN(n));
      identity = new Permutation(allMappings.get(0));
      members.add(identity);
      
      for (int i = 1 ; i < allMappings.size(); i++) {
        Permutation toAdd = new Permutation(allMappings.get(i));
        members.add(toAdd);
      }
    }
    else {
      LinkedList<LinkedList<Integer>> allMappings = generateOrderings(oneToN(n));
      identity = new Permutation(allMappings.get(0));
      members.add(identity);
      
      for (int i = 1; i < allMappings.size(); i++) {
        Permutation toAdd = new Permutation(allMappings.get(i));
        toAdd.setOrder(computeElementOrder(toAdd));
        addInOrder(members, toAdd);
      }
    }
  }
  
  /* ToString(): */
  public String toString() {
    String s = "The permutation group S" + dimension + " contains " + fact(dimension) + " elements:";
    
    for (int i = 0; i < members.size(); i++)
      s+="\n"+members.get(i).toString();
    
    return s.substring(0, s.length())+".";
  }
  
  public String groupName() {
    return "S"+dimension;
  }
  
  /* Returns the number of elements in the group. */
  public int getGroupOrder() {
    return fact(dimension);
  }
  
  /* Operates two permutations by way of function composition, following the convention
   * that the second input is the "first" function to act. The basic mechanism is that,
   * for each integer from 1 to n, we find where the "right" function takes i (e2 maps i -> j),
   * then whene the "left" function takes j (e1 maps j -> k), and then use this to
   * construct a new permutation that takes i directly to k. */
  public GroupElement operate(GroupElement e1, GroupElement e2) {
    if (!isPermutation(e1, e2)) {
      System.out.println(NOT_A_PERM_ERROR);
      return null;
    }
    
    Permutation p1 = (Permutation)e1;
    Permutation p2 = (Permutation)e2;
    Hashtable<Integer, Integer> function1 = p1.getFunctionVals();
    Hashtable<Integer, Integer> function2 = p2.getFunctionVals();
    
    LinkedList<Integer> composition = new LinkedList<Integer>();
    
    for (int i = 1; i <= dimension; i++) {
      int firstVal = function2.get(i);
      int secondVal = function1.get(firstVal);
      
      composition.add(secondVal);
    }
    
    Permutation result = new Permutation(composition);
    return result;
  }
  
  /* Composes together a list of more than two permutations in one fell swoop. */
  public GroupElement chainOperate(LinkedList<Permutation> list) {
    if (list.size() == 1)
      return (list.get(0));
    
    else if (list.size() == 2)
      return (operate(list.get(0), list.get(1)));
    
    else {
      Permutation two = list.removeLast();
      Permutation one = list.removeLast();
      return (operate(chainOperate(list), operate(one, two)));
    }
  }
  
  /* Computes the inverse of an element by decomposing it into 2-cycles and
   * reversing & then multiplying out the decomposition. */
  public GroupElement getInverse(GroupElement e) {
    if (!isPermutation(e)) {
      System.out.println(NOT_A_PERM_ERROR);
      return null;
    }
    
    if (equals(e, identity))
      return identity;
    if (equals(operate(e, e), identity))
      return e;
    else {
      LinkedList<Permutation> twoCycleDecomp = decompose((Permutation)e);
      LinkedList<Permutation> inverseDecomp = reverse(twoCycleDecomp);
      
      return (chainOperate(inverseDecomp));
    }
  }
  
  /* Decomposes the given permutation into a "product", or in this case a
   * LinkedList, of (non-disjoint) 2-cycles, such that multiplying these 2-cycles
   * out would yield the original permutation. The heart of the method is to first
   * assemble a list of the starting points of all the distinct cycles in the
   * original permutation, then traverse and decompose each cycle individually. The whole
   * method is motivated by tricky cases as the permutation (143)(26) in S6: the desired
   * decomposition is (14)(13)(26), but if we start from just a single point in the
   * permutation, not all information will be recovered.  So we need to find both 1 and 2
   * as the starting points of disjoint cycles (while leaving 5, which is fixed, alone)
   * and then decompose .*/
  public LinkedList<Permutation> decompose(Permutation p) {
    LinkedList<Permutation> decomposition = new LinkedList<Permutation>();
    
    if (equals(p, identity)) {
      decomposition.add((Permutation)identity);
      return decomposition;
    }
    
    else {
      Hashtable<Integer, Integer> allFunctionVals = p.getFunctionVals();
      LinkedList<Integer> cycleMembers = p.getNonFixedVals();
      LinkedList<Integer> allOrigins = new LinkedList<Integer>();
      
      // find an origin for every cycle:
      while (cycleMembers.size() > 0) {
        int cycleStart = cycleMembers.get(0);
        allOrigins.add(cycleStart);
        
        LinkedList<Integer> correspondingCycle = p.getFullCycle(cycleStart);
        
        for (int i = 0; i < correspondingCycle.size(); i++)
          cycleMembers.removeFirstOccurrence(correspondingCycle.get(i));
      }
      
      // now decompose all the cycles into 2-cycles:
      LinkedList<Integer> base = new LinkedList<Integer>();
      LinkedList<Integer> traversed = new LinkedList<Integer>();
      traversed.addAll(allOrigins); // the point we start from in each cycle is by default "traversed"
      
      int cyclesDoneSoFar = 0; // tracks position in allOrigins
      int cycleOrigin = allOrigins.get(0);
      int currentPos = allFunctionVals.get(cycleOrigin);
      
      while (cyclesDoneSoFar < allOrigins.size()) {
        if (!traversed.contains(currentPos)) {
          traversed.add(currentPos);
          
          base = oneToN(dimension); // <- list(1, ..., n)
          swap(base, cycleOrigin, currentPos);
          Permutation twoCycle = new Permutation(base);
          decomposition.addFirst(twoCycle);
          
          currentPos = allFunctionVals.get(currentPos);
        }
        else {
          cyclesDoneSoFar++;
          if (cyclesDoneSoFar == allOrigins.size()) { /*finish*/ }
          else { /*move to the next cycle*/
            cycleOrigin = allOrigins.get(cyclesDoneSoFar);
            currentPos = allFunctionVals.get(cycleOrigin); 
          }
        }
      }
      
      return decomposition;
    }
  }
  
  /* Returns the group member at the given index. */
  public Permutation getMember(int index) {
    return (Permutation)members.get(index);
  }
  
  // -------------------------------------------------------------------------------------------------- //
  // --------------------------------------------- HELPERS -------------------------------------------- //
  // -------------------------------------------------------------------------------------------------- //
  
  /* Generates all possible ways of arranging a given set of integers. */
  private LinkedList<LinkedList<Integer>> generateOrderings(LinkedList<Integer> digits) {
    LinkedList<LinkedList<Integer>> allOrderings = new LinkedList<LinkedList<Integer>>();
    int numDigits = digits.size();
    
    if (numDigits == 1)
      allOrderings.add(digits);
    
    else {
      int numOrders = fact(numDigits);
      int partitionPoint = numOrders/numDigits; // = (n-1)!
      int firstDigit = 0; // <-placeholder
      LinkedList<Integer>[] paredDigits = new LinkedList[numDigits];
      
      // populate the allOrderings list and, in successive bunches of (n-1)!, add
      // one of the numbers in digits at the head of each new list:
      for (int j = 0; j < numOrders; j++) {
        allOrderings.add(new LinkedList<Integer>());
        
        if (divides(j, partitionPoint)) {
          // if at a multiple of (n-1)!, we change over the number we're entering into
          // the 1st slot & add to paredDigits an abridged copy of the digits list
          // that omits the current firstDigit:
          firstDigit = digits.get(j/partitionPoint);
          LinkedList<Integer> reducedList = (LinkedList<Integer>)digits.clone();
          reducedList.removeFirstOccurrence(firstDigit);
          paredDigits[j/partitionPoint] = reducedList;
        }
        
        allOrderings.get(j).add(firstDigit);
      }
      
      // Why? Because now we have:
      // - n subsections of the "allOrderings" list, each containing
      //   (n-1)! lists with just the first digit assigned;
      // - n corresponding "paredDigits" lists, each of length (n-1), which,
      //   when sent to generateOrderings, will produce (n-1)! possible arrangements
      //   of those digits, NOT containing whatever number k is currently sitting at
      //   the head of the 1-entry list in allOrderings.  How we proceed is clear:
      
      int counter = 0;
      
      for (int currentSubsect = 0; currentSubsect < numDigits; currentSubsect++) {
        LinkedList<LinkedList<Integer>> subOrderings = generateOrderings(paredDigits[currentSubsect]);
        for (int k = 0; k < subOrderings.size(); k++) {
          allOrderings.get(counter).addAll(subOrderings.get(k));
          counter++;
        }
      }
    }
    
    return allOrderings;
  }
  
  /* Generates a list of the integers from one to n. */
  private LinkedList<Integer> oneToN(int n) {
    LinkedList<Integer> toReturn = new LinkedList<Integer>();
    for (int i = 1; i <= n; i++)
      toReturn.add(i);
    
    return toReturn;
  }
  
  /* Swaps the position of two integers a and b in a list of integers. */
  private LinkedList<Integer> swap(LinkedList<Integer> list, int a, int b) {
    int index1 = list.indexOf(a);
    int index2 = list.indexOf(b);
    int temp = list.get(index1);
    list.set(index1, list.get(index2));
    list.set(index2, temp);
    
    return list;
  }
  
  /* Reverses the entries in a linked list of permutations. */
  private LinkedList<Permutation> reverse(LinkedList<Permutation> list) {
    LinkedList<Permutation> reversed = new LinkedList<Permutation>();
    
    for (int i = 0; i < list.size(); i++)
      reversed.addFirst(list.get(i));
    
    return reversed;
  }
  
  private boolean divides(int quotient, int divisor) {
    return (quotient%divisor == 0);
  }
  
  /* Factorial function */
  private int fact(int n) {
    if ((n == 1) || (n == 0))
      return 1;
    else
      return (n * fact(n-1));
  }
  
  /* Returns true if all the given GroupElements are Permutations,
   * false otherwise. */
  private boolean isPermutation(GroupElement ... args) {
    for (int i = 0; i < args.length; i++) {
      if (!(args[i] instanceof Permutation))
        return false;
    }
    
    return true;
  }
  
  public static void main(String[] args) {
//    PermutationGroup pg = new PermutationGroup(4);
//    System.out.println(pg.toString());
//    
//    Permutation r = new Permutation(4, 2, 1, 3); // = (143)
//    
//    Permutation q = new Permutation(2, 1, 3, 4); // = (12)
//    
//    System.out.println("--------OPERATE METHOD TESTS--------");
//    System.out.println("(143)(12) should be (1243). Actually is: " + pg.operate(r, q).toString());
//    System.out.println("The decomposition of (143) should be (13)(14). Actually is: " 
//                         + pg.decompose(r).toString());
//    System.out.println("chainOperate on the decomposition of (143) should return (143). Actually returns: "
//                         + pg.chainOperate(pg.decompose(r)));
//    System.out.println("The decomposition of (12) should be (12). Actually is: " + pg.decompose(q));
//    
//    Permutation shift = new Permutation(2, 3, 4, 1);
//    System.out.println("The decomposition of (1234) should be (14)(13)(12). Actually is: "
//                         + pg.decompose(shift));
//    System.out.println("chainOperate on this decomposition should return (1234). Actually returns: "
//                         + pg.chainOperate(pg.decompose(shift)));
//    System.out.println("The inverse of (12)?: " + pg.getInverse(q));
//    System.out.println("The inverse of (143)?: " + pg.getInverse(r));
//    System.out.println("The inverse of (1234)?: " + pg.getInverse(shift));
//    System.out.println("Does operate((1234), getInverse(1234)) yield the identity? Result is: "
//                         + pg.operate(shift, (Permutation)pg.getInverse(shift)));
//    System.out.println("Order of r? " + pg.computeElementOrder(r));
//    System.out.println("Subgroup generated by (143)? " + pg.generateSubgroup(r));
//    
//    Permutation bug = new Permutation(4, 3, 2, 1);
//    System.out.println("Permutation of disjoint 2-cycles: " + bug.toString());
//    System.out.println("The inverse of this permutation should be the permuatation itself: " 
//                         + pg.getInverse(bug).toString());
//    System.out.println("And just to prove that the decompose method works on disjoint cycles, the decomposition of (14)(23)...: "
//                         + pg.decompose(bug));
    
    // defunct tests:
//    LinkedList<Integer> list = new LinkedList<Integer>();
//    for (int i = 1; i <= 3; i++)
//      list.add(i);
//    System.out.println(pg.generateOrderings(list));
//    list.add(4);
//    System.out.println(pg.generateOrderings(list));
  }
}