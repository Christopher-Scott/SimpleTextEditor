/*  CIS 2168 Lab3
    Christopher Scott
    "Variable data type to hold a name and associated value"
 */
public class Variable
{
  private String name;
  private double value;

  public Variable(String s, double val)
  {
    this.name = s;
    this.value = val;
  }

  public Variable(String s){
    this.name = s;
  }

  public boolean equals(Object other){
    if(!(other instanceof  Variable))
      return false;
    final Variable othervar = (Variable) other;
    return this.name.equals(othervar.name);
  }

  public String getName()
  {
    return this.name;
  }
 
  public double getValue()
  {
    return this.value;
  }

  public void setValue(double v)
  {
    this.value = v;
  }

  public String toString()
  {
    return "\"" + this.name + "\"" + ": " + this.value;
  }
}
