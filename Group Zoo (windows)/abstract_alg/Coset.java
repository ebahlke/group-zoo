package abstract_alg;
/* NAME: Coset.java
 * AUTHOR: Emma Bahlke
 * DATE: May 22, 2014
 * COMMENTS: Describes a coset, that is an element in a factor group of some
 * original group (contextGroup) modded out by some normal subgroup of that group
 * (identitySubgroup, since this subgroup becomes the identity coset in the resulting
 * factor group). The constructor, given the context group, the factoring subgroup,
 * and an element "e" belonging to the context group, makes that element the primary
 * representative of a new coset and generates the coset by multiplying every member
 * of the identity subgroup by e and storing the resulting list of group elements in
 * cosetMembers. */


import java.util.*;

public class Coset extends GroupElement {
  private LinkedList<GroupElement> cosetMembers;
  private GroupElement primaryRep;
  private Group contextGroup;
  
  public Coset(Group group, LinkedList<GroupElement> identitySubgroup, GroupElement e) {
    cosetMembers = new LinkedList<GroupElement>();
    contextGroup = group;
    primaryRep = e;
    generateCoset(identitySubgroup);
  }
  
  /* Returns a true copy of cosetMembers - the copying is an attempt to avoid
   * information about the coset being destroyed by outside methods that
   * involve manipulation/removal of the members of the coset.*/
  public LinkedList<GroupElement> getElements() {
    LinkedList<GroupElement> elementsCopy = new LinkedList<GroupElement>();
    
    for (int i = 0; i < cosetMembers.size(); i++)
      elementsCopy.add(cosetMembers.get(i));
    
    return elementsCopy;
  }
  
  public GroupElement getPrimaryRep() {
    return primaryRep;
  }
  
  public int getSize() {
    return cosetMembers.size();
  }
  
  public boolean equals(GroupElement e) {
    Coset another = (Coset)e;
    LinkedList<GroupElement> otherCoset = another.getElements();
    
    for (int i = 0; i < cosetMembers.size(); i++) {
      if (!contextGroup.contains(cosetMembers, otherCoset.get(i)))
        // use the Group contains method in case this is a coset of modular ints or etc., which have no working GroupElement equals method
        return false;
    }
    
    return true;
  }
  
  public String toString() {
    String s = ((primaryRep.toString().equalsIgnoreCase("identity")) ?
                  "The coset generated by the identity: " :
                  "The coset generated by " + primaryRep.toString() + ": ");
    if (cosetMembers.size() <= 20) {
    for (int i = 0; i < cosetMembers.size(); i++)
      s+=cosetMembers.get(i).toString()+", ";
    
    return (s.substring(0, s.length()-2));
    }
    else {
      s+=cosetMembers.get(0).toString()+", "+cosetMembers.get(1).toString()+", ..., "
        +cosetMembers.get(getSize()-2).toString()+", "+cosetMembers.get(getSize()-1).toString();
      return s;
    }
  }
  
  private void generateCoset(LinkedList<GroupElement> identity) {
    for (int i = 0; i < identity.size(); i++)
      cosetMembers = contextGroup.addInOrder(cosetMembers, (contextGroup.operate(primaryRep, identity.get(i))));
  }
  
  public static void main(String[] args) {
    PermutationGroup pg = new PermutationGroup(3);
    LinkedList<Integer> l = new LinkedList<Integer>();
    l.add(3);
    l.add(1);
    l.add(2);
    Permutation p = new Permutation(l);
    LinkedList<GroupElement> sg = pg.generateSubgroup(p);
    Coset c = new Coset(pg, sg, sg.get(0));
    System.out.println(c.toString());
  }
}