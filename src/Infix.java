/*
    CIS2168 Lab 2 "Infix Notation"
    Christopher Scott
    "A function to evaluate an expression presented in infix notation and convert it to postfix notation
     using the Shunting-yard algorithm."

 */
import java.util.StringTokenizer;

public class Infix {
    public static String infixToPost(String s, Dictionary symbols){ // Takes argument of symbols to look up variables
        Stack<String> opStack = new Stack<String>();
        StringTokenizer st = new StringTokenizer(s, " +-*/()", true);
        StringBuilder output = new StringBuilder();
//        String output = "";

        while(st.hasMoreTokens())
        {
            String tok = st.nextToken();
            if(tok.matches("\\^*")) // current token contains an illegal operator
            {
                System.err.println("Expression is not valid: illegal characters");
                System.exit(1);
            }
            // ####### new code #######
            else if(tok.matches("[A-z]+[0-9]*")) { // current token matches a variable
                double val;
                try{
                    val = symbols.find(tok);
//                    output += val + " ";
                    output.append(val);
                    output.append(' ');
                }
                catch(Dictionary.KeyException e){
                    System.err.println("Error: In expression evaluation, symbol not found");
                    System.exit(1);
                }
            }
            // ##########################
            else if(tok.matches("[0-9]*\\.*[0-9]+")) // Current token matches a number
            {
//                output += tok + " ";
                output.append(tok);
                output.append(' ');
//                System.out.println("Line 26: " + output);
            }
            else if(tok.matches("[+\\-*/]")) // Token contains an operator
            {
                // while operator on top of stack has higher precedence or is not a left paren, pop to output
                while (!opStack.isEmpty() && comparePrec(tok, opStack.peek()) <= 0 && !opStack.peek().equals("("))
                {
//                    output += opStack.pop() + " ";
                    output.append(opStack.pop());
                    output.append(' ');
//                    System.out.println( "line 35: " + output);

                }
                opStack.push(tok);
            }
            if(tok.equals("("))
                opStack.push(tok);
            if(tok.equals(")")) {
                // process stack until it is empty or a left paren is found
                while (!opStack.isEmpty() && !opStack.peek().equals("(")) {
//                    output += opStack.pop() + " ";
                    output.append(opStack.pop());
                    output.append(' ');
//                        System.out.println("Line 44: " + output);
                }
                if (!opStack.isEmpty() && opStack.peek().equals("("))
                    opStack.pop();
                else { // Whole stack processed and no left paren found
                    System.err.println("Expression is not valid: Mismatched parentheses");
                    System.exit(1);
                }
            }


        }
        while(!opStack.isEmpty())
        {
            if(opStack.peek().equals("(")){
                System.out.println("Expression is not valid: Mismatched parentheses");
                System.exit(1);
            }
//            output += opStack.pop() + " ";
              output.append(opStack.pop());
              output.append(' ');
//            System.out.println("Line 77: " + output);
        }
        return output.toString();
    }

    // Compares the precedence of two operands, returns -1 if second op has greater precedence,
    // 0 if second op has the same precedence, or 1 if second op has lower precedence.
    private static int comparePrec(String op1, String op2){
        //System.out.println("comparePrec: op1 = " + op1 + ", op2 = " + op2);
        if(op1.equals("(") || op1.equals(")")){
            if(op2.equals("(") || op2.equals(")"))
                return 0;
            else
                return 1;
        }
        else if(op1.equals("*") || op1.equals("/")){
            if(op2.equals("(") || op2.equals(")"))
                return -1;
            else if(op2.equals("*") || op2.equals("/"))
                return 0;
            else
                return 1;
        }
        else {
            if(op2.equals("(") || op2.equals(")"))
                return -1;
            else if(op2.equals("*") || op2.equals("/"))
                return -1;
            else
                return 0;

        }
    }

    public static void main(String[] args){
        String test1 = "100 * myVar8 + 12 ";
        Dictionary symbols = new Dictionary();
        symbols.insert("myVar8", 2.0);
//        String test2 = "(10.0 + 3) * 45.0";
//        String test3 = "3 + 4 * 2 / (1 - 5)";

        String post1 = infixToPost(test1, symbols);
        System.out.print(post1 + ": ");
        System.out.println(Postfix.postfix(post1));
        System.out.println();

//        String post2 = infixToPost(test2);
//        System.out.print(post2 + ": ");
//        System.out.println(Postfix.postfix(post2));
//        System.out.println();
//
//        String post3 = infixToPost(test3);
//        System.out.print(post3 + ": ");
//        System.out.println(Postfix.postfix(post3));
//        System.out.println();
    }
}
