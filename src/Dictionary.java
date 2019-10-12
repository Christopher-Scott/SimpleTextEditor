public class Dictionary
{
  private LL<Variable> theTable[];
  private int size;

  public Dictionary(int size)
  {
    theTable = new LL[size];
    this.size = size;
    for (int i = 0; i < size; i++)
    {
      theTable[i] = new LL<Variable>();
    }
//this is wrong - fixed
  }

  public Dictionary()
  {
    this(100);
    this.size = 100;
  }

  public void insert(String s, double val) {
    int where = hash(s);
    Variable input = new Variable(s, val);
    Variable old;
    if((old = theTable[where].find(input)) != null){
      old.setValue(val);
    }
    else
      theTable[where].insertAtHead(input);
//lookup first and change if it already exists, otherwise insert as above
  }

  public double find(String s) throws KeyException
  {
    int where = hash(s);
    Variable value  = theTable[where].find(new Variable(s));
    if(value != null)
    {
      return value.getValue();
    }
    else
    {
      throw new KeyException("Key not found:" + s);
    }
  } 

  private int hash(String s)
  {
    int sum = 0;
    for (int i = 0; i < s.length(); i++)
    {
      char c = s.charAt(i);
      sum = sum + (int)c & 0xff;
    }
    return sum % this.size;
  }

  public String toString(){
    String retval = "{";
    for(int i = 0; i < this.size; i++){
      if(!this.theTable[i].isEmpty())
        retval += this.theTable[i].toString() + "\n";

    }
    retval += "}";
    return retval;
  }

  public static void main(String args[]){
    Dictionary table = new Dictionary();

    table.insert("myvariable", 1.0);
    table.insert("anothervariable", 2.0);
    table.insert("AAAAAAAAAAAAAAAAAAAAAA", 3.0);
    table.insert("i", 0.0);

    System.out.println(table);

    try {
      System.out.println("lookup for \"myvariable\": " + table.find("myvariable"));
    } catch(KeyException e){
      System.err.println(e);
    }

  }

  public class KeyException extends Exception{

    public KeyException(String s){
      super(s);
    }
  }
}
 
  
