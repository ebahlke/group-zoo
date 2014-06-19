package abstract_alg;
/* NAME: ModularInt.java
 * AUTHORS: Emma Bahlke (Primary), Alexi Block Gorman, and Meredith McCormack-Mager
 * DATE: May 2014
 * COMMENTS: Extends the abstract GroupElement class by implementing a representation
 * of an integer mod N, which, removed from the context of the group (i.e. with no
 * knowledge of what N is), simply means an integer with an order instance variable
 * attached (inherited from GroupElement).
 * */

public class ModularInt extends GroupElement {
  private int intValue;
  
  public ModularInt(int n) {
    intValue = n;
  }
  
  public int getIntValue() {
    return intValue;
  }
  
  public String toString() {
    return "[" + intValue + "]";
  }
  
  /* Stub method; the functional equals method for ModularInts is in ZmodN. */
  public boolean equals(GroupElement e) {
    return false;
  }
}