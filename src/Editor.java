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
    private enum Keywords {READ, SAVE, LIST, RESEQUENCE, LET, PRINT, RUN, ACCEPT, IF, GOTO, FOR, HELP, QUIT, EXIT, UNDEFINED};
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
                    else if(splitString[0].equalsIgnoreCase("help")){
                        if(splitString.length == 1)
                            done = this.doCommand(splitString[0], null);
                        else
                            done = this.doCommand(splitString[0], splitString[1]);
                    }
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
            case HELP: this.help(expr);
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
//      TODO: Change variable parsing to scheme used in for_func and validate with isValidVariable

    }

    // print out the result of an expression
    private void print(String expr)
    {
        int index;
        int lineNum = -1;
        String target = "";
        if(expr.contains("LINE")){
            index = expr.indexOf("LINE");
            index += 4;
            while(index < expr.length() && Character.isWhitespace(expr.charAt(index))){
                index++;
            }
            if(index < expr.length() && Character.isDigit(expr.charAt(index))){
                try{
                    lineNum = Integer.parseInt(expr.substring(index));
                    Line targetline = new Line(lineNum, "");
                    if((targetline = theText.find(targetline)) != null)
                        target = targetline.getValue();
                    System.out.println(target);
                } catch(NumberFormatException e){
                    System.err.println("Error: Illegal argument for PRINT.");
                }
            }
            else
                System.out.println(expr.substring(index));
        }
        else
            System.out.println(evaluate(expr));
    }

    // runs all the executable commands currently in the editor
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
    // If instruction, expression is true if >= 0
    private void if_func(String expr){
        String expression = "";
        int cmdIndex = parseCommand(expr);
        if(cmdIndex < 0){
            System.err.println("Error in IF: No valid command");
            return;
        }

        String exprOnly = expr.substring(0, cmdIndex);
        String command = expr.substring(cmdIndex);
        String splitString[];
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

    // Evaluates the string expr for a line number, and jumps the instruction pointer to that position
    private void goto_func(String expr){
        int jump;
        try {
            jump = Integer.parseInt(expr);
            if(lineToRun.get(jump) >= theText.getLength() || jump < 0){
                System.err.println("Error: invalid argument for GOTO, usage GOTO <Line Number>");
            }
            this.runPtr = lineToRun.get(jump);
        }
        catch(NumberFormatException e){
            System.err.println("Error: invalid argument for GOTO, usage GOTO <Line Number>");
        }
    }

    // implements a counter based control structure instruction
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
        this.symbolTable.insert(var, initialValue); // will overwrite the variable name if exists already.
        double endValue = evaluate(expression2);

        // add the commands of the FOR loop to a command queue
        boolean hasEnd = false;
        for(Line line : theText){
            if(line.getLineNum() > lineNum && !line.getValue().substring(0,4).equalsIgnoreCase("NEXT")){
                cmdList.insertInOrder(line);
            }
            if(line.getValue().substring(0,4).equalsIgnoreCase("NEXT")) {
                String nextcmd = line.getValue();
                index = 4; // look after NEXT command

                while(index < nextcmd.length() && Character.isWhitespace(nextcmd.charAt(index))){ // skip whitespace
                    index++;
                }
                if(nextcmd.substring(index).equalsIgnoreCase(var)) {
                    hasEnd = true;
                    break;
                }
                else{
                    System.err.println("Error: Syntax in NEXT, usage: NEXT <variable>.");
                    return;
                }

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

    private void help(String cmd){
        String command_help[] = new String[Keywords.values().length];
        command_help[Keywords.LET.ordinal()] = "LET:\n\tUsage: LET <variable> = <expression>\n"
                                                + "Variable names may not start with numerals and variables may not contain whitespace.\n"
                                                + "Expression should be in infix notation\n";
        command_help[Keywords.ACCEPT.ordinal()] = "ACCEPT:\n\tUsage: ACCEPT <variable>\n"
                                                + "Prompts the user to enter the value of a variable. The value may be in the form of a valid expression\n";
        command_help[Keywords.LIST.ordinal()] = "LIST:\n\tUsage: LIST\nPrints a list of all lines currently stored in the editor.\n";
        command_help[Keywords.PRINT.ordinal()] = "PRINT:\n\tUsage: PRINT <expression> or PRINT LINE <string> or PRINT LINE <line number>"
                                                + "\nPrints the value of the expression which may contain variables, or a string of text.\n";
        command_help[Keywords.RUN.ordinal()] = "RUN:\n\tUsage: RUN\nExecutes the current commands stored in the editor\n";
        command_help[Keywords.IF.ordinal()] = "IF:\n\tUsage: IF (<expression>) <command>\nExecutes command if the expression evaluates to greater than or equal 0\n";
        command_help[Keywords.GOTO.ordinal()] = "GOTO:\n\tUsage: GOTO <line number>\nNot valid as a runtime command.  Jumps RUN execution to <line number>.\n";
        command_help[Keywords.SAVE.ordinal()] = "SAVE:\n\tUsage: SAVE\nPrompts the user to enter a file name and saves the current lines in the editor\n";
        command_help[Keywords.READ.ordinal()] = "READ:\n\tUsage: READ\nPrompts the user to enter a file name and adds the lines read from the file into the editor\n";
        command_help[Keywords.QUIT.ordinal()] = "QUIT:\n\tUsage: QUIT\nExits the editor\n";
        command_help[Keywords.EXIT.ordinal()] = "EXIT:\n\tUsage: EXIT\nExits the editor\n";
        command_help[Keywords.RESEQUENCE.ordinal()] = "RESEQUENCE:\n\tUsage: RESEQUENCE\nReplaces the line numbers currently in the editor, incrementing by 10.\n";
        command_help[Keywords.FOR.ordinal()] = "FOR...NEXT:\n\tUsage: \n\tFOR <variable> = <expression 1>, <expression 2>"
                                                + "\n\t<command list>\n\tNEXT <variable>"
                                                + "\nSets variable to the value of expression 1."
                                                + "Iterates over command list until expression 1 > expression 2\n";
        try {
            // Print entire manual
            if (cmd == null || cmd.isEmpty()) {
                for (int i = 0; i < command_help.length; i++) {
                    if (command_help[i] != null)
                        System.out.println(command_help[i]);
                }

            }
            // print specific section
            else {
                Keywords command = Keywords.valueOf(cmd);
                System.out.println(command_help[command.ordinal()]);
            }
        } catch (IllegalArgumentException e){
            System.err.println("Error: " + cmd + " is not a valid help entry.");
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
         //Valid commands are LET, ACCEPT, PRINT, GOTO, IF, FOR
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
