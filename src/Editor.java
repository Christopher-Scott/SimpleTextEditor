/*  CIS 2168 Lab3
    Christopher Scott
    "A simple text editor"
 */

import java.util.Hashtable;
import java.util.Scanner;
import java.io.*;
import java.util.StringTokenizer;


public class Editor
{
    private CompLL<Line> theText;
    private String prompt;
    private enum Keywords {READ, SAVE, LIST, RESEQUENCE, LET, PRINT, RUN, ACCEPT, IF, GOTO, QUIT, EXIT, UNDEFINED};
    private Scanner console;
    private Dictionary symbolTable;
    private int runPtr;
    private Hashtable<Integer, Integer> lineToRun = null;
    private Line cmdQueue[] = null;

    public Editor()
    {
        this.theText = new CompLL<Line>();
        this.prompt = ">";
        this.console = new Scanner(System.in);
        this.symbolTable = new Dictionary();
        this.runPtr = 0;
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
                try {
                    this.theText.removeElement(target);
                } catch (NullPointerException e){
                    System.err.println("Error: Line " + line + " could not be deleted.");
                }
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

                }
                else //otherwise, it is a command, so call doCommand to perform it.
                {
                    // provides command arg and expression arg to let(), print(), and accept()
                    if (splitString[0].equalsIgnoreCase("let")
                            || splitString[0].equalsIgnoreCase("print")
                            || splitString[0].equalsIgnoreCase("accept")
                            || splitString[0].equalsIgnoreCase("if"))
                        done = this.doCommand(splitString[0], splitString[1]);
                    else if(splitString[0].equalsIgnoreCase("goto"))
                        System.err.println("Error: Invalid use of GOTO");
                    else if(splitString[0].equalsIgnoreCase("for"))
                        System.err.println("Error: Invalid use of FOR");
                    else
                        done = this.doCommand(splitString[0]);
                }
            }
        }
    }

    // Overloaded for commands that do not require additional parameters
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
            case ACCEPT: this.accept(expr);
                break;
            case IF: this.if_func(expr);
                break;
            case GOTO: this.goto_func(expr);
                break;
            case QUIT:
            case EXIT: retval = true;
                break;
            case UNDEFINED: System.err.println("Undefined command:" + com);
//                System.out.println();
                break;
        }
        return retval;
    }

// You need to implement the following routines.

    // Read lines from a user specified file and insert them into the editor
    private void read() throws FileNotFoundException
    {
        System.out.print("Enter the file you would like to read:\n>");
        Scanner input = new Scanner(new File(console.nextLine()));
        while(input.hasNext()){
            String inputLine = input.nextLine();
            String splitline[] = inputLine.split(" ", 2);
            if(isInt(splitline[0])) {
                Line lineObj = new Line(Integer.parseInt(splitline[0]), splitline[1]);
                theText.insertInOrder(lineObj);
            }
            else
            {
              fatal("could not parse file");

            }
        }

    }

    // Write the current lines to an output file
    private void save() throws FileNotFoundException
    {
        System.out.print("What would you like to save the file as?\n>");
        File output = new File(console.nextLine());
        PrintStream out = new PrintStream(output);
        out.print(theText.toString());
    }

    // Prints out the current lines
    private void list()
    {
        System.out.print(theText.toString());
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
        boolean valid = true;
        String variable;
        String exprArr[] = expr.split("=");
        if(exprArr.length != 2)// should only be 2 tokens, one on either side of =
        {
            System.err.println("Error: Invalid expression\n\tUsage: LET <variable> = <expression>");
            valid = false;
        }
        if(Character.isDigit(exprArr[0].charAt(0)))
        {
            System.err.println("Error: Illegal variable name, variables may not start with numerals");
            valid = false;
        }
        // Tokenizer is used because the exprArr[0] likely has a space before the equals sign
        // so exprArr[0].contains(" ") would not cover typical use
        StringTokenizer splitter = new StringTokenizer(exprArr[0], " \t\n", false);
        if(splitter.countTokens() > 1) // If there are more than 1 tokens then there was a space in the variable name
        {
            System.err.println("Error: Illegal variable name, variables may not contain whitespace");
            valid = false;
        }
        if(valid)
        {
            variable = splitter.nextToken();
            this.symbolTable.insert(variable, evaluate(exprArr[1]));
        }
        //        if(isValidVariable(exprArr[0]))
//      TODO: Change variable parsing to scheme used in for_func and validate with isValidVariable

    }

    // print out the result of an expression
    private void print(String expr)
    {
//        System.out.println(expr);
        System.out.println(evaluate(expr));
    }

//    private void run()
//    {
//        for(Line line : theText) // iterate through theText
//        {
////            System.out.println(line);
//            String splitString[] = line.value.split(" ", 2);
//            if(splitString[0].equalsIgnoreCase("let")
//                || splitString[0].equalsIgnoreCase("print")
//                || splitString[0].equalsIgnoreCase("accept")
//                || splitString[0].equalsIgnoreCase("if"))
//                this.doCommand(splitString[0], splitString[1]);
//            else
//                this.doCommand(splitString[0]);
//        }
//
//    }

    private void run(){
        this.lineToRun = new Hashtable<Integer, Integer>();
        this.runPtr = 0;
        this.cmdQueue = new Line[theText.getLength()];
        int i = 0;

        for(Line line : theText){ // set up data structures
            cmdQueue[i] = line;
            lineToRun.put(line.getLineNum(), i);
            i++;
        }
        Line line;
        while(this.runPtr < cmdQueue.length){
            line = cmdQueue[this.runPtr];
            this.runPtr++;
            String splitString[] = line.getValue().split(" ", 2);
            if(splitString[0].equalsIgnoreCase("let")
                || splitString[0].equalsIgnoreCase("print")
                || splitString[0].equalsIgnoreCase("accept")
                || splitString[0].equalsIgnoreCase("if")
                || splitString[0].equalsIgnoreCase("goto"))
                doCommand(splitString[0], splitString[1]);
            else if(splitString[0].equalsIgnoreCase("for")) {
                // pass control to for
                for_func(line.getLineNum(), splitString[1]);
                // skip commands until next, for checks for syntax
                while (this.runPtr < cmdQueue.length && !splitString[0].equalsIgnoreCase("next")) {
                    line = cmdQueue[++this.runPtr];
                    splitString = line.getValue().split(" ", 2);
                }
                this.runPtr++; // skip line holding next
            }
            else
                doCommand(splitString[0]);
        }

    }

    // evaluate a mathematical expression
    private double evaluate(String expr)
    {
        return Postfix.postfix(Infix.infixToPost(expr, this.symbolTable));
    }

    // Ask the user to specify a value to assign to a variable
    private void accept(String var)
    {
        // Handle variable name errors
//        if(Character.isDigit(var.charAt(0)))
//            System.err.println("Error: Illegal variable name, variables may not start with numerals");
//        else if(var.contains(" ") || var.contains("\n") || var.contains("\t"))
//            System.err.println("Error: Illegal variable name, variables may not contain whitespace");
        if(isValidVariable(var))
        {
            // create a separate scanner to prevent collisions with console
            // and command input system
            Scanner input = new Scanner(System.in);
            System.out.print(var + " = \n>");
            this.symbolTable.insert(var, evaluate(input.next()));
            // Do not explicitly close Scanner input
        }

    }

    private void if_func(String expr){
        String expression = "";
        int cmdIndex = parseCommand(expr);
        if(cmdIndex < 0){
            System.err.println("Error in IF: No valid command");
            return;
        }

        String exprOnly = expr.substring(0, cmdIndex);
        String command = expr.substring(cmdIndex);
        String splitString[] = null;
        int parens = 0;
        boolean valid = false;
        char c;
        int i = 0;

        // parse expression for valid parentheses
        while(i < exprOnly.length()){
            c = exprOnly.charAt(i);
            if( parens == 0 && Character.isWhitespace(c)) { // skip leading whitespace before expression
                i++;
                continue;
            }
            if(c == '(')
                parens++;

            else if(c == ')')
                parens--;

            if(parens == 0)
                valid = true;

            //System.out.println("Parens = " + parens);
            expression += c;
            i++;
        }

        if(parens < 0) {
            System.err.println("Error in IF: malformed expression");
            return;
        }

        if(parens == 0 && valid){ // parse command
            if(evaluate(expression) >= 0) {
//                System.out.println("DEBUG: " + command);
                splitString = command.split(" ", 2);
                doCommand(splitString[0], splitString[1]);
            }
        }
        else
            fatal(expr + " is not a valid expression.");

        return;

    }

    private void goto_func(String expr){
        int jump;
        try {
            jump = Integer.parseInt(expr);
            if(lineToRun.get(jump) >= theText.getLength() || jump < 0){
                System.err.println("Error: invalid argument for GOTO, usage GOTO <Line Number>");
            }
            this.runPtr = lineToRun.get(jump);
        }
        catch(NullPointerException e){
        System.err.println("Error: invalid argument for GOTO, usage GOTO <Line Number>");
        }
    }

    private void for_func(int lineNum, String expr){
        CompLL<Line> cmdList = new CompLL<Line>();
        char c;
        String var = "";
        String expression1 = "";
        String expression2 = "";
        int len = expr.length();
        int index = 0;

        // Parse variable name
        while( index < len && (c = expr.charAt(index++)) != '='){
            if(!Character.isWhitespace(c))
                var += c;
        }
        if(!isValidVariable(var))
            fatal("In FOR, illegal variable name");
        else if(index >= len)
            fatal("Syntax error in FOR, reached end of field while parsing");
        index++; // skip over '='

        // Parse for expression 1 until ',' or EOF, expression evaluation is agnostic towards whitespace
        while( index < len && (c = expr.charAt(index++)) != ','){
            expression1 += c;
        }
        if(!isValidExpression(expression1))
            fatal("\"" + expression1 + "\"" + "is not valid");
        else if(index >= len)
            fatal("Syntax error in FOR, reached end of field while parsing");
        index++; // skip over ','

        // Parse for expression 2 until EOF
        while( index < len && (c = expr.charAt(index++)) != '\n') {
            expression2 += c;
        }

        // evaluate expressions and add to symbol table
        double initialValue = evaluate(expression1);
        double endValue = evaluate(expression2);
        this.symbolTable.insert(var, initialValue);

        // add the commands of the FOR loop to a command queue
        boolean hasEnd = false;
        for(Line line : theText){
            if(line.getLineNum() > lineNum && !line.getValue().equalsIgnoreCase("NEXT")){
                cmdList.insertInOrder(line);
            }
            if(line.getValue().equalsIgnoreCase("NEXT")) {
                hasEnd = true;
                break;
            }
        }
        if(!hasEnd)
            fatal("Syntax in FOR, no NEXT command");
        try {
            while (this.symbolTable.find(var) <= endValue){
                for(Line line : cmdList){
                    parseAndExecute(line);
                }
                initialValue++;
                this.symbolTable.insert(var, initialValue);
            }

        }catch(Dictionary.KeyException e){
            fatal("in FOR\n" + e.getMessage());
        }


    }

    private int parseCommand(String expr){
        expr.toUpperCase();
        if(expr.contains("LET"))
            return expr.indexOf("LET");
        else if(expr.contains("ACCEPT"))
            return expr.indexOf("ACCEPT");
        else if(expr.contains("PRINT"))
            return expr.indexOf("PRINT");
        else if(expr.contains("GOTO"))
            return expr.indexOf("GOTO");
        else
            return -1;
    }

    private boolean parseAndExecute(Line line){
         //Valid commands are LET, ACCEPT, PRINT, LIST, SAVE, READ, RUN, GOTO, IF
        boolean done = false;
        String splitString[] = line.getValue().split(" ", 2);
        if (splitString[0].equalsIgnoreCase("let")
                || splitString[0].equalsIgnoreCase("print")
                || splitString[0].equalsIgnoreCase("accept")
                || splitString[0].equalsIgnoreCase("if")
                || splitString[0].equalsIgnoreCase("goto")
                || splitString[0].equalsIgnoreCase("for"))
            done = this.doCommand(splitString[0], splitString[1]);
        else
            fatal("Runtime command error");
        return done;
    }

    private boolean isValidVariable(String var){
        if(Character.isDigit(var.charAt(0))) {
            System.err.println("Error: Illegal variable name, variables may not start with numerals");
            return false;
        }
        else if(var.contains(" ") || var.contains("\n") || var.contains("\t")) {
            System.err.println("Error: Illegal variable name, variables may not contain whitespace");
            return false;
        }
        else
            return true;
    }

    private boolean isValidExpression(String expr){
        return true;
    }

    private void fatal(String msg){
        System.err.println("Error: " + msg);
        System.exit(1);
    }

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
