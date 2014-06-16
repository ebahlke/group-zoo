package abstract_alg;
/** 
 * FILE NAME: Dihedral.java
 * WHO: Meredith McCormack-Mager (Primary), Emma Bahlke, Alexi Block Gorman
 * WHAT: This class extends the GroupElement class. It sets up a class of elements
 * that are employed in the DihedralGroup class. These objects have a type (rotation
 * or reflection), a degree of rotation (for the rotations) or a degree of axis (for
 * the reflections), and an order that is inherited from GroupElement.
 * WHEN: May 2014
 * MODIFIED: May 27, 2014 by Emma Bahlke
 */

import java.text.DecimalFormat;

public class Dihedral extends GroupElement {
  
  //Instance Variables
  private double degree;
  private String type;
  private final double ACCEPTABLE_ERROR = 1.0;
  
  /** 
   * Constructor for Dihedral class. Checks to make sure appropriate type is entered,
   * and prints an error message if incorrect type is entered.
 */
  public Dihedral (String type, double degree) {
    this.degree = degree;
    this.type = type;
    if (!isRotation() && !isReflection()) {
      System.out.println("Error: " + type + " is not an acceptable type." +
                         " Please enter 'reflection' or 'rotation' as dihedral type.");
    }
  }
  
  
  /** 
   * toString for Dihedral class. Checks to make sure dihedral element is an
   * acceptable type, and returns an error message if it is not.
   */
  public String toString() {
    String s = "";
    if (isRotation() || isReflection()) {
      s += type + " " + ((isRotation()) ? "of " + roundTwoDecimals(degree) + " degrees":
                           "over " + roundTwoDecimals(degree) + " axis");
    } else s = "Not a Dihedral element. Please correct input.";
    return s;
  }
  
  /** 
   * Private helper method that limits the number of decimals returned in toString.
   * This is important in D_7 (and for other dihedral groups where the dimension does
   * not divide 360).
   */
  private static double roundTwoDecimals(double d) {
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    Double tempDouble = Double.valueOf(twoDForm.format(d));
    return tempDouble;
  }
  
  /** 
   * Getter that returns the degree of the dihedral element.
   */
  public double getDegree() {
    return degree;
  }
  
  /** 
   * Getter that returns the type of the dihedral element.
   */
  public String getType(){
    return type;
  }
  
  /** 
   * Boolean that determines whether a dihedral element is a rotation.
   */
  public boolean isRotation() {
    if (type.equalsIgnoreCase("rotation")) return true;
    return false;
  }
  
  /** 
   * Boolean that determines whether a dihedral element is a reflection.
   */
  public boolean isReflection() {
    if (type.equalsIgnoreCase("reflection")) return true;
    return false;
  }
  
  /* Checks for equality of two Dihedrals. */
  public boolean equals(GroupElement e) {
    if (!(e instanceof Dihedral))
      return false;
    else {
      Dihedral another = (Dihedral)e;
      // the Math.min statement covers both cases where this.degree > another.degree
      // and vice versa:
      double toCheck = Math.min(Math.abs(degree - another.getDegree())%360,
    		  (360 - Math.abs(degree - another.getDegree()))%360);
      return ((toCheck <= ACCEPTABLE_ERROR)
                && (type.equals(another.getType())));
    }
  }
  
  //main method for testing use only
  public static void main(String[] args) {
    double testSigFigs1 = 1;
    double testSigFigs2 = 3.14159265358979;
    double testSigFigs3 = 0.009;
    double testSigFigs4 = 30000.100;
    
    System.out.println(testSigFigs1 + " with two decimal places (1.0): "
                         + roundTwoDecimals(testSigFigs1));
    System.out.println(testSigFigs2 + " with two decimal places (3.14): "
                         + roundTwoDecimals(testSigFigs2));
    System.out.println(testSigFigs3 + " with two decimal places (0.01): "
                         + roundTwoDecimals(testSigFigs3));
    System.out.println(testSigFigs4 + " with two decimal places (30000.1): "
                         + roundTwoDecimals(testSigFigs4) + "\n");
    
    Dihedral rotationtest = new Dihedral("rotation", 90.0);
    System.out.println("toString (rotation of 90.0 degrees): " + rotationtest);
    System.out.println("Get type (rotation): " + rotationtest.getType());
    System.out.println("Is rotation? (true): " + rotationtest.isRotation());
    System.out.println("Is reflection? (false): " + rotationtest.isReflection() + "\n");
    
    Dihedral reflectiontest = new Dihedral("reflection", 90);
    System.out.println("toString (reflection of 120.0 degrees): " + reflectiontest);
    System.out.println("Get type (reflection): " + reflectiontest.getType());
    System.out.println("Is rotation? (false): " + reflectiontest.isRotation());
    System.out.println("Is reflection? (true): " + reflectiontest.isReflection() + "\n");
    
    Dihedral catfail = new Dihedral("cat", 180);
    System.out.println("toString (Not a Dihedral element. Please correct input.): "
                         + catfail);
    
    Dihedral rotationtest2 = new Dihedral("rotation", 540.5);
    System.out.println("toString (rotation of 540.5 degrees): " + rotationtest2);
    System.out.println("Get type (rotation): " + rotationtest2.getType());
    System.out.println("Is rotation? (true): " + rotationtest2.isRotation());
    System.out.println("Is reflection? (false): " + rotationtest2.isReflection() + "\n");
  }
                        
}