/*
    CIS 2168 Lab 1 "Postfix"
    Christopher Scott 9/15/19
    "A class to evaluate expressions given in Reverse Polish (postfix) notation."
 */

import java.util.StringTokenizer;

public class Postfix{

    public static double postfix(String s) {
        Stack<String> stack = new Stack<>();
        StringTokenizer st = new StringTokenizer(s, " +-/*", true);

        while (st.hasMoreTokens()) {
            String tok = st.nextToken();

            if (tok.equals(" ")) {
                continue; // skip over spaces
            }
            else if (tok.matches("[A-z]+")){ // Token contains alphabet characters
                System.err.println("The expression is not valid: contains illegal characters");
                System.exit(1);
            }

            else if (tok.matches("[+\\-/*]")) { // Token contains an operator

                // catch error where stack is empty when an operator is presented
                if(stack.isEmpty()){
                    System.err.println("The expression was not valid: stack is empty");
                    System.exit(1);
                }
                //System.out.print(tok + " ");
                double num1, num2, result;
                try {
                    num1 = Double.parseDouble(stack.pop());
                    num2 = Double.parseDouble(stack.pop());

                    result = 0.0;

                    if (tok.equals("+"))
                        result = num1 + num2;

                    if (tok.equals("-"))
                        result = num2 - num1;

                    if (tok.equals("/"))
                        result = num2 / num1;

                    if (tok.equals("*"))
                        result = num1 * num2;

                    stack.push(String.valueOf(result));
                }
                catch(NullPointerException e){
                    System.err.println("The expression was not valid: only 1 operand on stack");
                    System.exit(1);
                }

            } 
            else {
                stack.push(tok);
                //System.out.print(tok + " ");
            }
        }
        
        // Catch error where there is more than 1 item left on the stack
        try {
            double answer = Double.parseDouble(stack.pop());
            if (stack.isEmpty()) {
                return answer;
            } else {
                System.err.println("The expression was not valid: 1 item left over");
                System.exit(1);
                return 0.0;
            }
        }
        catch(NullPointerException e) {
            System.err.println("The expression was not valid");
            System.exit(1);
            return 0.0;

        }
    }    
        

    public static void main(String args[]){
        String test1 = "2 3 1 * + 9 -";
        String test2 = "100 200 + 2 / 5 * 7 +";
        String test3 = "4 3 + -";
        //String test4 = "2 4/";
        //String test5 = "3 a +";

        System.out.println(postfix(test1));
        System.out.println(postfix(test2));
        System.out.println(postfix(test3));
    //    System.out.println(postfix(test4));
    //    System.out.println(postfix(test5));
    }

}