/*  CIS 2168 Lab3
    Christopher Scott
    "A Linked List used for chaining in the hash table"
 */
public class LL<T>
{
  private ListElement<T> head;
  private ListElement<T> tail;

  public LL()
  {
    this.head = new ListElement<T>(); // add dummy "first" element
    this.tail = this.head;
  }

  public void insertAtHead(T value)
  {
    this.head.link = new ListElement<T>(value, this.head.link);
  }
    
  public T removeFromHead()
  {
    T retval;

    if (this.isEmpty())
    {
      return null;
    }
    retval = this.head.link.value;
    this.head.link = this.head.link.link;
    return retval;
  }

  public void insertAtTail(T value)
  {
    this.tail.link = new ListElement<T>(value);
    this.tail = this.tail.link;
  }

  // Added - Iteratively lookup target in the list and return the value
  public T find(T target){
      if(this.isEmpty()){
          return null;
      }
      ListElement<T> where = this.head.link; //skip dummy element
      while(where.link != null){
          if(where.value.equals(target))
              return where.value;
          else
              where = where.link;
      }
      // handle case for list of length 1
      if(where.value.equals(target))
          return where.value;
      return null;
  }
  // Added - unused
//  public T findAndRemove(T target){
//      if(this.isEmpty())
//          return null;
//      ListElement<T> where = this.head;
//      while(where.link.link != null){ // iterate through, looking one element ahead
//          if(where.link.getValue().equals(target)) {
//              T val = where.link.getValue();
//              where.link = where.link.link;  // value is found, cut where.link out of chain
//              return val;
//          }
//          else
//              where = where.link;
//      }
//      if(where.link.getValue().equals(target)){ // check if the last element has the target
//          T val = where.link.getValue();
//          where.link = null;
//          return val;
//      }
//      return null;
//  }

  public String toString()
  {
    
    String retval = "";
    retval +=  toString(head.link, retval);
    return retval;
  }

  public String toString(ListElement h, String first)
  {
    if(h == null)
      return first;
    else
    {
      return first + h.value + ", " + toString(h.link, first);
    }
  }
/*
  public String toString()
  {
    String retval = "";
    ListElement<T> where = this.head.link;
    if (where == null)
    {
      retval += "Empty\n";
    }
    else
    {
      while (where != null)
      {
        retval += where.value + "\n";
        where = where.link;
      }
    }
    return retval;
  }
*/
  
  public static void main(String args[])
  {
    LL<Integer> myLL = new LL<Integer>();

    System.out.println(myLL); 
    for (int i = 0; i < 10; i++)
    {
      myLL.insertAtTail(i);
    }

    System.out.println(myLL);
  }
  
  public boolean isEmpty()
  {
    return this.head.link == null;
  }
}

class ListElement<R>
{
  public R value;
  public ListElement<R> link;

  public ListElement(R v, ListElement<R> link)
 {
    this.value = v;
    this.link = link;
  }

  public ListElement(R v)
  {
    this(v, null);
  }
  
  public ListElement()
  {
    this(null, null);
  }

  public void setValue(R v)
  {
    this.value = v;
  }
  
  public void setLink(ListElement<R> link)
  {
    this.link = link;
  }
  
  public R getValue()
  {
    return this.value;
  }

  public ListElement<R> getLink()
  {
    return this.link;
  }

  public String toString()
  {
    return "Value = " + this.getValue();
  }
}

