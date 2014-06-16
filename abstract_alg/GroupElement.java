package abstract_alg;
/* Name: GroupElement.java
 * Authors: Emma Bahlke, Alexi Block Gorman, and Meredith McCormack-Mager
 * Date: May 2014
 * Comments: Describes an element in an algebraic Group (see Group.java). The main
 * function of this class is to ensure comparability of elements, give elements an
 * "element order" (see computeElementOrder in Group), and also to provide a generalized
 * input/output in the signatures for various elements in the Group class.
 * */

public abstract class GroupElement implements Comparable<GroupElement> {
  
  protected int order;
  
  /* The "compareTo" method of a group element compares only the order of the elements. 
   * If we want to determine if two elements are actually the same thing,
   *    we use the "equals" method.
   * If "this" has greater order than "another" the method returns 1.
   * If "this" has the same order as "another" the method returns 0.
   * If "this" has the same order as "another" the method returns -1.
   * */
  public int compareTo(GroupElement another) {
    if (this.order==another.getOrder()) {
      return 0;
    } else if (this.order>another.getOrder()) {
      return 1;
    }
    return -1;
  }
  
  public int getOrder(){
    return order;   
  }
  
  public void setOrder(int n) {
    order = n;
  }
  
  public abstract String toString();
  
  /* This method may not be feasible for some types of GroupElement
   * which require information about the dimension of the group etc.
   * to determine equality; in that case, the Group-extending class should
   * overwrite the Group equals method, OR information about the dimension
   * of the context group should be fed to the GroupElement's constructor. */
  public abstract boolean equals(GroupElement another);
}