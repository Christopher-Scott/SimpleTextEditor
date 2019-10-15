public class Stack<T>
{
  LL<T> theStack;

  public Stack()
  {
    theStack = new LL<T>();
  }

  public boolean isEmpty()
  {
    return this.theStack.isEmpty();
  }

  public boolean isFull()
  {
    return false;
  }

  public void push(T value)
  {
    this.theStack.insertAtHead(value);
  }

  public T pop()
  {
    {
      return this.theStack.removeFromHead();
    }
  }

  public T peek()
  {
    T retval = this.pop();
    this.push(retval);
    return retval;
  }

  public String toString()
  {
    return this.theStack.toString();
  }  

  public static void main(String args[])
  {
    Stack<Double> myStack =  new Stack<Double>();
    
    for (int i = 0; i < 10; i++)
    {
      myStack.push((double)i);
    }

    while (!myStack.isEmpty())
    {
      System.out.println(myStack.pop());
    }
  } 
}
