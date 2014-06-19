package abstract_alg;
/* NAME: FactorGroup.java
 * AUTHOR: Emma Bahlke
 * DATE: May 22, 2014
 * COMMENTS: Implements the Group abstract class for a factor group. */

import java.util.*;


public class FactorGroup extends Group {
  // instance variables inherited from Group:
  // LL<GroupElement> members, int dimension, Hashtable<Integer, LL<GroupElement>> membersByOrder, LL<int> allOrders
  // methods inherited from Group:
  // - equals, getIdentity, computeElementOrder, generateSubgroup, StoreElementsByOrder, getElementsByOrder,
  //   orderedElementsToString, isNormal, getMembers, and protected helpers:
  //     - findIndex, subgroupEquals, contains, isPrime, and two versions of addInOrder
  
  private Group context;
  private LinkedList<GroupElement> fullgroup;
  private LinkedList<GroupElement> subgroup;
  
  /* Note that unlike groups such as Z mod N, S_n, D_n etc., the
   * "n" (or dimension) of a factor group is not part of the group name
   * in this way.  So we simply take n to be the number of elements (cosets)
   * in the group, which is the number of elements in the quotient group
   * divided by the number of elements in the factoring subgroup. */
  public FactorGroup(Group group, LinkedList<GroupElement> subgroup) {
    super(group.getGroupOrder()/subgroup.size());
    
    context = group;
    this.subgroup = subgroup;
    fullgroup = group.getMembers();
    identity = new Coset(context, subgroup, subgroup.get(0));
    
    modOut();
  }
  
  private void modOut() {
    members.add(identity);
    
    for (int i = 1; i < fullgroup.size(); i++) {
      Coset nextPotential = new Coset(context, subgroup, fullgroup.get(i));
      if (contains(members, nextPotential))
        continue;
      else
        members.add(nextPotential);
    }
  }
  
  public int getGroupOrder() {
    return dimension;
  }
  
  public GroupElement operate(GroupElement e1, GroupElement e2) {
    Coset coset1 = (Coset)e1;
    Coset coset2 = (Coset)e2;
    
    GroupElement pr1 = coset1.getPrimaryRep();
    GroupElement pr2 = coset2.getPrimaryRep();
    GroupElement product = context.operate(pr1, pr2);
    
    return (new Coset(context, subgroup, product));
  }
  
  public GroupElement getInverse(GroupElement e) {
    Coset c = (Coset)e;
    
    GroupElement pr = c.getPrimaryRep();
    GroupElement prInverse = context.getInverse(pr);
    
    return (new Coset(context, subgroup, prInverse));
  }
  
  public String toString() {
    String s = "The factor group of " + subgroup.toString() + " in "
      + context.groupName() +" contains:";
    
    for (int i = 0; i < members.size(); i++)
      s+="\n"+members.get(i).toString();
    
    return s;
  }
  
  public String groupName() {
    String s = "Factor group of " + subgroup.toString() + " in " + context.groupName();
    return s;
  }
  
  public static void main(String[] args) {
//    PermutationGroup pg = new PermutationGroup(3);
//    Permutation perm = new Permutation(3, 1, 2);
//    Permutation perm2 = new Permutation(3, 2, 1);
//    System.out.println("Identity coset-generating candidate: " + perm.toString());
//    System.out.println("Have we got a normal subgroup?: " + pg.isNormal(pg.generateSubgroup(perm)));
//    System.out.println("Generating factor group of S3...");
//    FactorGroup fg = new FactorGroup(pg, pg.generateSubgroup(perm));
//    System.out.println(fg.toString());
//    Coset c = new Coset(pg, pg.generateSubgroup(perm), pg.generateSubgroup(perm).get(0));
//    Coset d = new Coset(pg, pg.generateSubgroup(perm), perm2);
//    System.out.println("Testing operate.");
//    System.out.println(fg.operate(c, d));
  }
}