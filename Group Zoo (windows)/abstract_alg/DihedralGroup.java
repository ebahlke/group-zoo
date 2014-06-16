package abstract_alg;
/** 
 * FILE NAME: DihedralGroup.java
 * WHO: Meredith McCormack-Mager (Primary), Emma Bahlke, Alexi Block Gorman
 * WHAT: This class extends the Group class. It creates a DihedralGroup of dimension n,
 * which contains 2n Dihedral objects. Of those dihedral objects, half are rotations
 * and half are reflections.
 * WHEN: May 2014
 * MODIFIED: May 27 by Emma Bahlke
 * MODIFICATIONS: Removed some methods (equals, generateSubgroup) that have been
 * generalized to the Group class and, most significantly, patched a bug in the cases
 * where n does not evenly divide 360 (i.e. D7) which was causing infinite loops in
 * computeElementOrder, generateSubgroup, and storeElementsByOrder.  The reason for this
 * was roundoff error in operate, leading to the equals method never returning true in
 * loops such as (while(!equals(soFar, identity)).  I fixed this actually by moving the
 * equals method to Dihedral, adding an ACCEPTABLE_ERROR constant there to deal with
 * roundoff, and doing some math manipulation in the equals method, allowing me to default
 * to the Group equals method, which just calls GroupElement's.
 */
import java.util.*;

public class DihedralGroup extends Group {
  // instance variables inherited from Group:
  // LL<GroupElement> members, int dimension, Hashtable<Integer, LL<GroupElement>> membersByOrder, LL<int> allOrders
  // methods inherited from Group:
  // - equals, getIdentity, computeElementOrder, generateSubgroup, StoreElementsByOrder, getElementsByOrder,
  //   orderedElementsToString, isNormal, getMembers, and protected helpers:
  //     - findIndex, subgroupEquals, contains, isPrime, and two versions of addInOrder (overwritten here)
  
  private int order;
  private Dihedral[] reflections, rotations;
  private final double FULL_ROTATION = 360.0;
  private final double HALF_ROTATION = 180.0;
  
  /**
   * Constructor for DihedralGroup. This method constructs a dihedral group of
   * dimension n and stores it in the members linked list of type group element
   * and two dihedral arrays: rotations and reflections, which contain the rotations
   * and reflections of the group respectively. It also computes the order of each
   * element and stores it as an instance variable, as well as sets the identity
   * to be the first rotation element.
   */
  public DihedralGroup(int n){
    super(n);
    
    order = 2*n;
    identity = new Dihedral("rotation", 0.0);
    rotations = new Dihedral[n];
    for (int i = 0; i < n; i++) {
      double degree = i*(FULL_ROTATION/(double)dimension);
      rotations[i] = new Dihedral("rotation", degree);
      rotations[i].setOrder(computeElementOrder(rotations[i]));
      members.add(rotations[i]);
    }
    reflections = new Dihedral[n];
    for (int i = 0; i < n; i++) {
      double degree = i*(HALF_ROTATION/(double)dimension);
      reflections[i] = new Dihedral("reflection", degree);
      reflections[i].setOrder(computeElementOrder(reflections[i]));
      members.add(reflections[i]);
    }
  }
  
  /** 
   * Getter for order.
   */
  public int getGroupOrder(){
    return order;
  }
    
  /** 
   * toString for DihedralGroup
   */
  public String toString() {
    String s = "Dihedral Group ";
    s += "(D_" + dimension + "): ";
    for (int i = 0; i < 2*dimension; i++){
      s +=members.get(i) + ", ";
    }
    return s.substring(0, s.length()-2);
  }
  
  /* Returns the name of the group. */
  public String groupName() {
    return "D"+dimension;
  }
  
  /** 
   * Getter for identity.
   */
  public Dihedral getIdentity(){
    return (Dihedral) identity;
  }
   
  /** 
   * The operate method separates elements into four different cases based on the
   * input, then returns the dihedral element that is the result of function
   * composition between the two elements.
   */
  public Dihedral operate(GroupElement element1, GroupElement element2) {
    //checks to make sure both elements are dihedral
    //if either is not, returns a dummy dihedral element
    if (!(element1 instanceof Dihedral) || !(element1 instanceof Dihedral)) {
      System.out.println("Error: Element not dihedral. " +
                         "Please enter two dihedral elements in order to operate.");
      return new Dihedral ("rotation", -1);
    }
    Dihedral elt1 = (Dihedral)element1;
    Dihedral elt2 = (Dihedral)element2;
    
    Dihedral result;
    if (elt2.isRotation()){
      if (elt1.isRotation()) {//two rotations
        result = new Dihedral("rotation", mod((elt1.getDegree() + elt2.getDegree()),
                                              FULL_ROTATION));
      } else {//rotation then reflection
        result = new Dihedral("reflection", mod((2*elt1.getDegree() - elt2.getDegree())/2,
                                                HALF_ROTATION));
      }
    } else {
      if (elt1.isRotation()) { //reflection the rotation
        result = new Dihedral("reflection", mod((2*elt2.getDegree() + elt1.getDegree())/2,
                                                HALF_ROTATION));
      } else {//two reflections
        result = new Dihedral("rotation", mod((2*elt1.getDegree() - 2*elt2.getDegree()),
                                              FULL_ROTATION));
      }
    } 
    return result;
  }
  
  /** 
   * Getter for inverse. Checks to make sure element is dihedral, then computes
   * inverse. If element is a reflection, returns element. If element is a rotation, 
   * returns complementary rotation.
   */
  public Dihedral getInverse(GroupElement element) {
    //checks to make sure element is dihedral
    //if not, returns dummy element
    if (!(element instanceof Dihedral)) {
      System.out.println("Error: Element not dihedral. " +
                         "Please enter a dihedral element to obtain an inverse.");
      return new Dihedral ("rotation", -1);
    }
    Dihedral elt = (Dihedral)element;
    
    if (elt.isReflection()) return elt; //reflection case
    else { //rotation case
      double rotateToIdentity = mod((FULL_ROTATION - elt.getDegree()), FULL_ROTATION);
      return new Dihedral("rotation", rotateToIdentity);
    }
  }
  
  /** 
   * Private helper method to compute amodb.
   */
  private static double mod(double num, double modAmount) {
    return ((num%modAmount) + modAmount)%modAmount;
  }
  
  /* Dihedral-specific addInOrder method that adds Dihedrals in neat rotation-reflection segments
   * and by magnitude of degree within each segment. */
  protected LinkedList<GroupElement> addInOrder(LinkedList<GroupElement> ll, GroupElement e) {
    Dihedral d = (Dihedral)e;
    int counter = 0;
    
     while ((counter < ll.size() && !((Dihedral)ll.get(counter)).getType().equals(d.getType())))
       counter++;
     
     for (int i = counter; i < ll.size(); i++) {
       if (((Dihedral)ll.get(i)).getDegree() <= d.getDegree())
         counter++;
       else
         i = ll.size();
     }
     
     ll.add(counter, d);
     return ll;
  }
  
  //main method for testing only
  //note: R_angle refers to a rotation of angle and F_angle refers to a
  // reflection (flip) over angle
  public static void main(String[] args) {
    DihedralGroup test1 = new DihedralGroup(4);
    System.out.println("Testing of DihedralGroup class using D_4");
    System.out.println(test1 + "\n");
    
    //tests group order
    System.out.println("Group order (8): " + test1.getGroupOrder() + "\n");
    
    //tests identity
    System.out.println("Identity of group (R_0): " + test1.getIdentity() + "\n");
    
    //tests operate for two rotations
    System.out.println("Testing of operate method for two rotations: ");
    System.out.println("R_0 + R_0 (R_0): " +
                       test1.operate(test1.rotations[0], test1.rotations[0]));
    System.out.println("R_90 + R_180 (R_270): " +
                       test1.operate(test1.rotations[1], test1.rotations[2]));
    System.out.println("R_180 + R_270 (R_90): " +
                       test1.operate(test1.rotations[2], test1.rotations[3]) + "\n");
    
    //tests operate for two flips (reflections)
    System.out.println("Testing of operate method for two reflections: ");
    System.out.println("F_0 + F_0 (R_0): " +
                       test1.operate(test1.reflections[0], test1.reflections[0]));
    System.out.println("F_45 + F_90 (R_270): " +
                       test1.operate(test1.reflections[1], test1.reflections[2]));
    System.out.println("F_135 + F_90 (R_90): " +
                       test1.operate(test1.reflections[3], test1.reflections[2])
                         + "\n");
    
    //tests operate for a flip and a rotation
    System.out.println("Testing of operate method for one rotation"
                         + " and one reflection: ");
    System.out.println("F_90 + R_0 (F_90): " +
                       test1.operate(test1.reflections[2], test1.rotations[0]));
    System.out.println("R_0 + F_90 (F_90): " +
                       test1.operate(test1.rotations[0], test1.reflections[2]));
    System.out.println("F_45 + R_270 (F_90): " +
                       test1.operate(test1.reflections[1], test1.rotations[3]));
    System.out.println("R_270 + F_45 (F_0): " +
                       test1.operate(test1.rotations[3], test1.reflections[1]));
    System.out.println("F_0 + R_90 (F_135): " +
                       test1.operate(test1.reflections[0], test1.rotations[1]));
    System.out.println("R_90 + F_0 (F_45): " +
                       test1.operate(test1.rotations[1], test1.reflections[0])
                         + "\n");
    
    //tests getInverse
    System.out.println("Inverse of R_270 (R_90): "
                         + test1.getInverse(test1.rotations[3]));
    System.out.println("Inverse of R_0 (R_0): "
                         + test1.getInverse(test1.rotations[0]));
    System.out.println("Inverse of F_45 (F_45): "
                         + test1.getInverse(test1.reflections[1]) + "\n");
    
    
    //tests element order
    System.out.println("Order of identity (1): "
                         + test1.computeElementOrder(test1.rotations[0]));
    System.out.println("Order of identity (1): "
                         + test1.rotations[0].getOrder());
    System.out.println("Order of R_270 (4): "
                         + test1.rotations[3].getOrder());
    System.out.println("Order of R_270 (4): "
                         + test1.computeElementOrder(test1.rotations[3]));
    System.out.println("Order of F_90 (2): "
                         + test1.reflections[2].getOrder());
    System.out.println("Order of R_90 (4): "
                         + test1.rotations[1].getOrder() + "\n");
    
    //tests equals method
    System.out.println("Testing of equals method: ");
    System.out.println(test1.members.get(4) +" equals "
                         + test1.reflections[0] + "? (true): "
                         + test1.equals(test1.members.get(4), test1.reflections[0]));
    System.out.println(test1.rotations[1] +" equals "
                         + test1.reflections[2] + "? (false): "
                         + test1.equals(test1.rotations[1], test1.reflections[2]));
    System.out.println(test1.reflections[1] +" equals "
                         + test1.reflections[2] + "? (false): "
                         + test1.equals(test1.reflections[1], test1.reflections[2]));
    System.out.println(test1.identity +" equals "
                         + test1.rotations[0] + "? (true): "
                         + test1.equals(test1.identity, test1.rotations[0]));
    Dihedral overSize = new Dihedral("rotation", 450);
    Dihedral underSize = new Dihedral("rotation", -270);
    System.out.println(overSize +" equals " + underSize + "? (true): "
                         + test1.equals(overSize, underSize) + "\n");
    
    
    //tests mod helper method
    System.out.println("9mod5 (4.0): " + mod(9,5));
    System.out.println("-6mod5 (4.0): " + mod(-6,5));
    System.out.println("7mod(-4) (-1.0): " + mod(7,-4) + "\n");
        
    //tests subgroups and checks if they are normal (inherited from Group)
    System.out.println("Tests subgroup generator and normality:");
    LinkedList<GroupElement> subgp1 = test1.generateSubgroup(test1.rotations[0]);
    System.out.println("Subgroup generated by R_0 ([R_0]): " + subgp1);
    System.out.println("isNormal? (true): " + test1.isNormal(subgp1));
    LinkedList<GroupElement> subgp2 = test1.generateSubgroup(test1.rotations[1]);
    System.out.println("Subgroup generated by R_90 ([R_0, R_90, R_180, R_270]): "
                         + subgp2);
    System.out.println("isNormal? (true): " + test1.isNormal(subgp2));
    LinkedList<GroupElement> subgp3 = test1.generateSubgroup(test1.rotations[2]);
    System.out.println("Subgroup generated by R_180 ([R_0, R_180]): " + subgp3);
    System.out.println("isNormal? (true): " + test1.isNormal(subgp3));
    LinkedList<GroupElement> subgp4 = test1.generateSubgroup(test1.rotations[3]);
    System.out.println("Subgroup generated by R_270 ([R_0, R_90, R_180, R_270]): "
                         + subgp4);
    LinkedList<GroupElement> subgp5 = test1.generateSubgroup(test1.reflections[3]);
    System.out.println("Subgroup generated by F_135 ([R_0, F_135]): " + subgp5);
    System.out.println("isNormal? (false): " + test1.isNormal(subgp5));
    System.out.println("Is D_4 normal? (true): " + test1.isNormal(test1.members)
                       + "\n");
    
    //tests add in order helper method
    LinkedList<GroupElement> addTest = new LinkedList<GroupElement>();
    test1.addInOrder(addTest, test1.reflections[3]);
    System.out.println("Added F_135 ([F_135]): " + addTest);
    test1.addInOrder(addTest, test1.reflections[1]);
    System.out.println("Added F_45 ([F_45, F_135]): " + addTest);
    test1.addInOrder(addTest, (Dihedral)test1.identity);
    System.out.println("Added identity ([R_0, F_45, F_135]): " + addTest);
    test1.addInOrder(addTest, new Dihedral("rotation", 120));
    System.out.println("Attempted to add R_120 ([R_0, F_45, F_135]): " + addTest
                         + "\n");
    
   System.out.println("************************************************************");
   DihedralGroup test2 = new DihedralGroup(3);
   System.out.println("Testing of DihedralGroup class using D_3");
   System.out.println(test2 + "\n");
    
    //tests group order
   System.out.println("Group order (6): " + test2.getGroupOrder() + "\n");
    
    //tests identity
   System.out.println("Identity of group (R_0): " + test2.getIdentity() + "\n");
    
    //tests operate for two rotations
   System.out.println("Testing of operate method for two rotations: ");
   System.out.println("R_0 + R_0 (R_0): " +
                      test2.operate(test2.rotations[0], test2.rotations[0]));
   System.out.println("R_120 + R_240 (R_0): " +
                      test2.operate(test2.rotations[1], test2.rotations[2]));
   System.out.println("R_240 + R_240 (R_120): " +
                      test2.operate(test2.rotations[2], test2.rotations[2]) + "\n");
    
    //tests operate for two flips (reflections)
   System.out.println("Testing of operate method for two reflections: ");
   System.out.println("F_0 + F_0 (R_0): " +
                      test2.operate(test2.reflections[0], test2.reflections[0]));
   System.out.println("F_120 + F_120 (R_0): " +
                      test2.operate(test2.reflections[2], test2.reflections[2])
                        + "\n");
   
    //tests operate for a flip and a rotation
   System.out.println("Testing of operate method for one rotation"
                        + " and one reflection: ");
   System.out.println("F_120 + R_0 (F_120): " +
                      test2.operate(test2.reflections[2], test2.rotations[0]));
   System.out.println("R_0 + F_120 (F_120): " +
                      test2.operate(test2.rotations[0], test2.reflections[2]));
    
    //tests getInverse
   System.out.println("Inverse of R_240 (R_120): "
                        + test2.getInverse(test2.rotations[2]));
   System.out.println("Inverse of F_60 (F_60): "
                         + test2.getInverse(test2.reflections[1]) + "\n");    
    
    //tests element order
   System.out.println("Order of identity (1): "
                        + test2.computeElementOrder(test2.rotations[0]));
   System.out.println("Order of R_240 (3): "
                        + test2.computeElementOrder(test2.rotations[2]));
   System.out.println("Order of F_120 (2): "
                        + test2.reflections[2].getOrder() + "\n");
    
    //tests equals method
   System.out.println("Testing of equals method: ");
   System.out.println(test2.members.get(4) +" equals "
                        + test2.reflections[1] + "? (true): "
                        + test2.equals(test2.members.get(4), test2.reflections[1]));
   System.out.println(test2.identity +" equals "
                        + test2.rotations[0] + "? (true): "
                        + test2.equals(test2.identity, test2.rotations[0]));
      
    //tests subgroups
   System.out.println("Subgroup generated by R_0 ([R_0]): "
                        + test2.generateSubgroup(test2.rotations[0]));
   System.out.println("Subgroup generated by R_240 ([R_0, R_120, R_240]): "
                        + test2.generateSubgroup(test2.rotations[2]));
   System.out.println("Subgroup generated by F_120 ([R_0, F_120]): "
                        + test2.generateSubgroup(test2.reflections[2]) + "\n");
   
   System.out.println("************************************************************");
   DihedralGroup test3 = new DihedralGroup(7);
   System.out.println("Testing of DihedralGroup class using D_7");
   System.out.println(test3 + "\n");
   System.out.println("Generating a subgroup in D_7:");
   System.out.println(test3.generateSubgroup(test3.members.get(2)));
   System.out.println("Storing elements by order in D_7.");
   test3.storeElementsByOrder();
  }
}