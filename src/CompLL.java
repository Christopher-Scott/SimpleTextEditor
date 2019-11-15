/*  CIS 2168 Lab3
    Christopher Scott
    "An ordered linked list used to hold lines in the text editor"
 */
import java.util.Iterator;

public class CompLL<T extends Comparable<T>> implements Iterable<T>
{
    private ListElement<T> head;
    private ListElement<T> tail;
    private int length;

    public CompLL()
    {
        this.head = new ListElement<T>(); // add dummy "first" element
        this.tail = this.head;
        this.length = 0;
    }

    public Iterator<T> iterator() {
        return new LLIterator<T>(this);
    }

    public void insertAtHead(T value)
    {
        this.head.link = new ListElement<T>(value, this.head.link);
    }

//    public T removeFromHead()
//    {
//        T retval = null;
//
//        if (this.isEmpty())
//        {
//            return null;
//        }
//        retval = this.head.link.value;
//        this.head.link = this.head.link.link;
//        return retval;
//    }
//
//    public void insertAtTail(T value)
//    {
//        this.tail.link = new ListElement<T>(value);
//        this.tail = this.tail.link;
//    }

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

    public void insertInOrder(T value)
    {
        head = insertInOrder(value, head);
        this.length++;
    }

    public ListElement<T> insertInOrder(T value, ListElement<T> h)
    {
        if (h.link == null || value.compareTo(h.link.value) < 0)
        {
            h.link = new ListElement<T>(value, h.link);
            return h;
        }
        else if(value.compareTo(h.link.value) == 0) // found two equal elements, should be replaced
        {
            h.link = new ListElement<T>(value, h.link.link);
            this.length--; // duplicate elements should not increase length
            return h;
        }
        else
        {
            h.link =  insertInOrder(value, h.link);
            return h;
        }
    }

    public void removeElement(T target)
    {
        head.link = removeElement(target, head.link);
        this.length--;
    }

    public ListElement<T> removeElement(T target, ListElement<T> h) {

        if (target.compareTo(h.value) == 0) { // current value is the one to remove
            return h.link;
        }
        else if (h.link == null) { // end of list
            return h;
        } else
//            System.out.println("Going deeper: " + h.link);
            h.link = removeElement(target, h.link);
            return h;
    }

    public int getLength(){
        return this.length;
    }

    public boolean isEmpty()
    {
        return this.head.link == null;
    }


    public static void main(String args[])
    {
        CompLL<Integer> cll = new CompLL<Integer>();

        cll.insertInOrder(21);
        cll.insertInOrder(2);
        cll.insertInOrder(1);
        cll.insertInOrder(13);
        cll.insertInOrder(-7);
        cll.insertInOrder(33);
        cll.insertInOrder(4);
        cll.insertInOrder(15);
        cll.insertInOrder(12);

        System.out.println(cll);

        cll.removeElement(2);

        System.out.println(cll);

        cll.removeElement(33);
        System.out.println(cll);

    }

class LLIterator<E extends Comparable<E>> implements Iterator<E>{
        ListElement<E> current;

        public LLIterator(CompLL obj){
            current = obj.head.link;
        }

        public boolean hasNext(){
            return current != null;
        }

        public E next(){
            E val = current.getValue();
            current = current.link;
            return val;
        }
}

class ListElement<R extends Comparable<R>>
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


}
