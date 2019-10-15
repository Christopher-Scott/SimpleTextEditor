/*  CIS 2168 Lab3
    Christopher Scott
    "A simple text editor"
 */

import java.util.Scanner;
import java.io.*;
import java.util.StringTokenizer;

public class Editor
{
    private CompLL<Line> theText;
    private String prompt;
    private enum Keywords {READ, SAVE, LIST, RESEQUENCE, LET, PRINT, RUN, QUIT, EXIT, UNDEFINED};
    private Scanner console;
    private Dictionary symbolTable;

    public Editor()
    {
        this.theText = new CompLL<Line>();
        this.prompt = ">";
        this.console = new Scanner(System.in);
        this.symbolTable = new Dictionary();
    }

    public String getPrompt()
    {
        return this.prompt;
    }


    public void setPrompt(String p)
    {
        this.prompt = p;
    }

    private static boolean isInt(String s) // see if a string represents
    { // an integer.
        boolean retval = false;
        try
        {
            Integer.parseInt(s);
            retval = true;
        }
        catch (NumberFormatException e)
        {
            retval = false;
        }
        return retval;
    }

    public void process()
    {
        boolean done = false;
        String line;
        while (!done)
        {
            System.out.print(this.prompt);
            line = console.nextLine().toUpperCase(); // Work only with upper case

            // Only a number was entered, delete the corresponding line
            if(isInt(line))
            {
                Line target = new Line(Integer.parseInt(line), null);
                this.theText.removeElement(target);
            }
            else
            {
                String splitString[] = line.split(" ", 2);

            //At this point, you need to decide whether this is a command or
            //a line of text to be entered.
                if (this.isInt(splitString[0])) {

                    // input is a line of text, add it to list
                    Line lineObj = new Line(Integer.parseInt(splitString[0]), splitString[1]);
                    theText.insertInOrder(lineObj);

                } else //otherwise, it is a command, so call doCommand to perform it.
                    if(splitString[0].equalsIgnoreCase("let") || splitString[0].equalsIgnoreCase("print"))
                        done = this.doCommand(splitString[0], splitString[1]);
                    else
                        done = this.doCommand(splitString[0]);
            }
        }
    }

    private boolean doCommand(String com){
        return doCommand(com, null);
    }

    private boolean doCommand(String com, String expr)
    {
        boolean retval = false;
        Keywords command;
//This first bit takes the string in the first word of the line
//and turns it into one of the manifest constants of the
//enumerated data type. This makes it fairly easy to add new
//commands later.
        try
        {
            command = Keywords.valueOf(com);// command is a Keywords and can
        } // can be used as the target of a switch.
        catch (IllegalArgumentException e)
        {
            command = Keywords.UNDEFINED; //An undefined Keywords will cause
        } //an exception.
        switch (command)
        {
            case READ:
                try{
                    this.read();
                } catch (FileNotFoundException e){
                    System.err.println("Error: File not found");
                }
                break;
            case SAVE:
                try {
                    this.save();
                } catch(FileNotFoundException e){
                    System.err.println("Error: File not found");
                }
                break;
            case LIST: this.list();
                break;
            case RESEQUENCE: this.resequence();
                break;
            case LET: this.let(expr);
                break;
            case PRINT: this.print(expr);
                break;
            case RUN: this.run();
                break;
            case QUIT:
            case EXIT: retval = true;
                break;
            case UNDEFINED: System.err.println("Undefined command:" + com);
        }
        return retval;
    }

// You need to implement the following routines.

    // Read lines from a user specified file and insert them into the editor
    private void read() throws FileNotFoundException
    {
        System.out.print(">Enter the file you would like to read:\n>");
        Scanner input = new Scanner(new File(console.nextLine()));
        while(input.hasNext()){
            String inputLine = input.nextLine();
            String splitline[] = inputLine.split(" ", 2);
            if(isInt(splitline[0])) {
                Line lineObj = new Line(Integer.parseInt(splitline[0]), splitline[1]);
                theText.insertInOrder(lineObj);
            }
        }

    }

    // Write the current lines to an output file
    private void save() throws FileNotFoundException
    {
        System.out.print(">What would you like to save the file as?\n>");
        File output = new File(console.nextLine());
        PrintStream out = new PrintStream(output);
        out.print(theText.toString());
    }

    // Prints out the current lines
    private void list()
    {
        System.out.println(theText.toString());
    }

    // Re-number the current lines starting from 10
    private void resequence()
    {
        CompLL<Line> newText = new CompLL(); // create a new LL to hold the resequenced lines
        Scanner input = new Scanner(theText.toString());
        String line;
        String newLine;
        Line newLineObj;
        int count = 10;
        while (input.hasNextLine())
        {
            line = input.nextLine().toUpperCase(); // Should be all uppercase anyways
            String splitline[] = line.split(" ", 2);
            if (isInt(splitline[0])) {
                newLineObj = new Line(count, splitline[1]);
                count += 10;
//            }
//            else // everything should begin with an int and be resequenced, but this catches anything else
//                newLine = line;
                newText.insertInOrder(newLineObj);
            }
        }
        this.theText = newText;
    }

    // declare a variable and insert it into the symbol table
    private void let(String expr)
    {
        String variable;
        String exprArr[] = expr.split("=");
//        System.out.println(java.util.Arrays.toString(exprArr));
        if(exprArr.length > 2)  // should only be 2 tokens, one on either side of =
            System.err.println("Error: Invalid expression\n\tUsage: LET <variable> = <expression>");
        if(Character.isDigit(exprArr[0].charAt(0)))
            System.err.println("Error: Illegal variable name, variables may not start with numerals");
        StringTokenizer splitter = new StringTokenizer(exprArr[0], " ", false);
        if(splitter.countTokens() > 1) // If there are more than 1 tokens then there was a space in the variable name
            System.err.println("Error: Illegal variable name");
        else
        {
            variable = splitter.nextToken();
            symbolTable.insert(variable, evaluate(exprArr[1]));
        }

    }

    // print out the result of an expression
    private void print(String expr)
    {
        // need to validate expr
//        System.out.println(expr);
        System.out.println(evaluate(expr));
    }

    private void run()
    {
        for(Line line : theText){
//            System.out.println(line);
            String splitString[] = line.value.split(" ", 2);
            if(splitString[0].equalsIgnoreCase("let") || splitString[0].equalsIgnoreCase("print"))
                this.doCommand(splitString[0], splitString[1]);
            else
                this.doCommand(splitString[0]);
        }

    }

    // evaluate a mathematical expression
    private double evaluate(String expr)
    {
        return Postfix.postfix(Infix.infixToPost(expr, this.symbolTable));
    }

//    private boolean validateVariable(String var){
//        if(Character.isDigit(var.charAt(0))) // var begins with a digit
//            return false;
//        if(var.matches("\\s+")) // var contains whitespace
//            return false;
//        if(var.matches("[!#@%&\\^|=+;\\-]+")) // var contains illegal characters
//            return false;
//        return true;
//    }

    public static void main(String args[])
    {
        Editor e = new Editor();
        e.process();
    }

    // A data class to hold the line number and the line of text
    private class Line implements Comparable<Line>{
        private int lineNum;
        private String value;

        public Line(int num, String value){
            this.lineNum = num;
            this.value = value;
        }

        public int compareTo(Line other){
            return this.lineNum - other.lineNum;
        }

        public int getLineNum() {
            return lineNum;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return this.lineNum + " " + this.value;
        }
    }
}
