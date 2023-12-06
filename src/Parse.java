/*
 * Name: Eliana Gaul
 * Class: CSCI 4200, Fall 2023
 * Program: Parser
 * Yes, I did use my own Lexical Analyzer
 */
import java.util.*;
import java.io.*;
public class Parse {
    private static FileWriter myOutput;
    private static ArrayList<String> errors;
    private static String[] lexemeList;
    private static Scanner scan;
    private static String line;
    private static Token nextToken;

    private static int i = 0;

    enum Token {
        ADD_OP,
        DIV_OP,
        END_KEYWORD,
        IDENT,
        INT_LIT,
        LEFT_PAREN,
        MULT_OP,
        PRINT_KEYWORD,
        PROGRAM_KEYWORD,
        RIGHT_PAREN,
        SEMICOLON,
        SUB_OP,
        ASSIGN_OP,
        IF_KEYWORD,
        READ_KEYWORD,
        THEN_KEYWORD,
        ELSE_KEYWORD
    }

    public static void main(String[] args) throws IOException {

        Parse parser = new Parse();

        //File writer for creating the output text file and string for scanner to place lines from input file
        myOutput = new FileWriter("parseOut.txt");

        System.out.println("Eliana Gaul, CSCI4200, Fall 2023, Parser");
        myOutput.write("Eliana Gaul, CSCI4200, Fall 2023, Parser\n");
        System.out.println("********************************************************************************");
        myOutput.write("********************************************************************************\n");

        try {
            scan = new Scanner(new File("sourceProgram.txt"));
            while (scan.hasNextLine()) {
                line = scan.nextLine();// Trims leading and trailing whitespace from each line
                System.out.println(line);//prints line to terminal
                myOutput.write(line + "\n");//writes line to output text file
            }
            System.out.println("********************\n");
            myOutput.write("********************\n\n");

            scan = new Scanner(new File("sourceProgram.txt"));
            line = scan.nextLine();
            parser.getLexemes(line);//gets lexemes and stores in String array
            parser.getToken();

            if (nextToken == Token.PROGRAM_KEYWORD) {
                parser.program();
            }

            // If there are no more lines, it must be the end of file
            System.out.println("Exit <program>");
            myOutput.write("Exit <program>\n");
            System.out.print("Parsing of the program is complete!");
            myOutput.write("Parsing of the program is complete!\n");
            System.out.println("********************************************************************************");
            myOutput.write("********************************************************************************\n");
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        myOutput.close();
    }

    private void getLexemes(String uglyLine) {//modifies string line then splits it into string array of lexemes
        //Multiple String.replace method calls adds spaces to special operators
        String prettyLine = uglyLine.replace("=", " = ")
                .replace("(", " ( ")
                .replace(")", " ) ")
                .replace("+", " + ")
                .replace("-", " - ")
                .replace("/", " / ")
                .replace("*", " * ")
                .replace(";", " ; ");
        //Splits the modified line by one or more spaces
        lexemeList = prettyLine.split("\\s+");
    }
    private void getToken() throws IOException {//getToken is part of the implementation of my Lexical analyzer.
        //The method tokenizes one lexeme at a time and assigns nextToken the result.

        if (i != lexemeList.length) {

            switch (lexemeList[i]) {//Compares lexemes to strings and assigns the appropriate token
                case "(":
                    nextToken = Token.LEFT_PAREN;
                    break;
                case ")":
                    nextToken = Token.RIGHT_PAREN;
                    break;
                case "=":
                    nextToken = Token.ASSIGN_OP;
                    break;
                case "+":
                    nextToken = Token.ADD_OP;
                    break;
                case "-":
                    nextToken = Token.SUB_OP;
                    break;
                case "/":
                    nextToken = Token.DIV_OP;
                    break;
                case "*":
                    nextToken = Token.MULT_OP;
                    break;
                case ";":
                    nextToken = Token.SEMICOLON;
                    break;
                case "if":
                    nextToken = Token.IF_KEYWORD;
                    break;
                case "else":
                    nextToken = Token.ELSE_KEYWORD;
                    break;
                case "print":
                    nextToken = Token.PRINT_KEYWORD;
                    break;
                case "then":
                    nextToken = Token.THEN_KEYWORD;
                    break;
                case "read":
                    nextToken = Token.READ_KEYWORD;
                    break;
                case "PROGRAM":
                    nextToken = Token.PROGRAM_KEYWORD;
                    break;
                case "END":
                    nextToken = Token.END_KEYWORD;
                    break;
                default:
                    if (lexemeList[i].matches("\\d+"))
                        nextToken = Token.INT_LIT;//Checks if lexeme is literal Integer by regex
                    else
                        nextToken = Token.IDENT;//sets token to IDENT if the lexeme is not a special character or Integer literal
            }

            if (nextToken == Token.END_KEYWORD){
                System.out.print("Next token is: " + nextToken);
                myOutput.write("Next token is: " + nextToken);
            }
            else {
                System.out.println("Next token is: " + nextToken);
                myOutput.write("Next token is: " + nextToken + "\n");
            }

            i++;//increments counter for lexemes
        }
    }

    private void program() throws IOException {
        System.out.println("Enter <program>");
        myOutput.write("Enter <program>\n");

        while (scan.hasNextLine()) {//iterates through each line in input text file
            i = 0;//after every loop index is re-initialized to 0

            // Begins iterating through lines and trim to get rid of white spaces
            String line = scan.nextLine().trim();

            getLexemes(line);//sets lexeme array
            getToken();//get prints first token

            //Processes the first statement
            if (nextToken == Token.PRINT_KEYWORD || nextToken == Token.IDENT || nextToken == Token.READ_KEYWORD || nextToken == Token.IF_KEYWORD){
                statement();
                if (nextToken != Token.SEMICOLON) {
                    System.out.println("**Error** - missing semicolon");
                    myOutput.write("**Error** - missing semicolon\n");
                }
            }
            //Splits initial statement for human comprehension
            System.out.println();
            myOutput.write("\n");
        }
    }
    private void statement() throws IOException {//statement method
        System.out.println("Enter <statement>");
        myOutput.write("Enter <statement>\n");

        if (nextToken == Token.PRINT_KEYWORD) output();
        else if (nextToken == Token.IDENT) assign();
        else if (nextToken == Token.READ_KEYWORD) input();
        else if (nextToken == Token.IF_KEYWORD) selection();
        else if(nextToken == Token.THEN_KEYWORD || nextToken == Token.ELSE_KEYWORD){
            getToken();
            statement();
        }
        else if (nextToken != Token.END_KEYWORD){
            System.out.print("**ERROR** - expected identifier or PRINT_KEYWORD");
            myOutput.write("**ERROR** - expected identifier or PRINT_KEYWORD");
        }
        System.out.println("Exit <statement>");
        myOutput.write("Exit <statement>\n");
    }
    /*******output method*******/
    // used for printing
    private void output() throws IOException {
        System.out.println("Enter <output>");
        myOutput.write("Enter <output>\n");
        // Calls lex to grab next token and then checks to see if that token is left parentheses.
        // From there checks to see what the next token is and calls expression
        getToken();
        if(nextToken == Token.LEFT_PAREN) {
            getToken();
            expr();
        }
        else {
            System.out.println("**ERROR** - left-parenthesis\n");
            myOutput.write("**ERROR** - left-parenthesis\n");
        }
        if (nextToken == Token.RIGHT_PAREN){
            getToken();
        }
        System.out.println("Exit <output>");
        myOutput.write("Exit <output>\n");
    }
    // Statement calls assign based on if nextToken is an ident
    private void assign() throws IOException {
        System.out.println("Enter <assign>");
        myOutput.write("Enter <assign>\n");
        
        getToken();
        // checks to make sure equals sign is there and then calls lex and expr method
        if (nextToken == Token.ASSIGN_OP) {
            getToken();
            expr();
        }
        else {
            System.out.println("**ERROR** - expected ASSIGN_OP");
            myOutput.write("**ERROR** - expected ASSIGN_OP\n");
        }
        System.out.println("Exit <assign>");
        myOutput.write("Exit <assign>\n");
    }
    /*******input method*******/
    /*First processes the left parentheses, then the IDENT, and then the right parentheses*/
    public void input() throws IOException{
        System.out.println("Enter <input>");
        myOutput.write("Enter <input>\n");
        getToken();//grabs the left parentheses
        if(nextToken==Token.LEFT_PAREN){
            getToken();//grabs IDENT
        }
        else{
            System.out.println("**Error** - missing left-parentheses");
            myOutput.write("**Error** - missing left-parentheses\n");
        }
        if(nextToken==Token.IDENT)
            getToken();//gets right parentheses
        else{
            System.out.println("**Error** - missing IDENT");
            myOutput.write("**Error** - missing IDENT\n");
        }
        if(nextToken==Token.RIGHT_PAREN)
            getToken();//gets semicolon
        else{
            System.out.println("**Error** - missing right-parentheses");
            myOutput.write("**Error** - missing right-parentheses\n");
        }
        System.out.println("Exit <input>");
        myOutput.write("Exit <input>\n");
    }
    /*******selection method*******/
    /*
     *Handles statements that start with the if keyword. If the if keyword has been processed, the following
     *tokens in the statement are processed.
     */
    private void selection() throws IOException{
        System.out.println("Enter <selection>");
        myOutput.write("Enter <selection>\n");

        getToken();

        if(nextToken == Token.LEFT_PAREN){//checks to see if the next token was a left-parenthses
            expr();
        }
        else{
            System.out.println("**Error** - missing left-parentheses");
            myOutput.write("**Error** - missing left-parentheses\n");
        }
        if(nextToken == Token.THEN_KEYWORD){//checks to see if the then keyword has been found (processed)
            statement();
        }
        else{
            System.out.println("**Error** - missing then keyword");
            myOutput.write("**Error** - missing then keyword\n");
        }
        if (nextToken == Token.ELSE_KEYWORD) {
            statement();
        }
        System.out.println("Exit <selection>");
        myOutput.write("Exit <selection>\n");
    }
    /*******expr method*******/
    // expr is called by assign, output or factor and leads to terms
    private void expr() throws IOException{
        System.out.println("Enter <expr>");
        myOutput.write("Enter <expr>\n");
        // beginning call for term
        term();
        // Checks for addition or subtraction operators and calls term and lex for next token to parse
        while(nextToken == Token.ADD_OP || nextToken == Token.SUB_OP) {
            getToken();
            term();
        }

        System.out.println("Exit <expr>");
        myOutput.write("Exit <expr>\n");
    }
    /*******term method*******/
    //terms produce factors and is called by expr
    private void term() throws IOException{
        System.out.println("Enter <term>");
        myOutput.write("Enter <term>\n");
        // beginning call for factor
        factor();
        // checks for multiplication or division operators and makes a recursive method call for the next factor to multiply or divide by
        while (nextToken == Token.MULT_OP || nextToken == Token.DIV_OP) {
            getToken();
            factor();
        }
        System.out.println("Exit <term>");
        myOutput.write("Exit <term>\n");
    }
    /*******factor method*******/
    /* factor is called by term and is a key part of the parser as this is where the actual values
     * are defined and where expr is looped back through to lengthen equations
     */
    private void factor() throws IOException {
        System.out.println("Enter <factor>");
        myOutput.write("Enter <factor>\n");
        // if statements run through cases to ascertain what methods will be called. ident/int_lit/expr
        if (nextToken == Token.IDENT || nextToken == Token.INT_LIT || nextToken == Token.IF_KEYWORD) {
            getToken();
        }
        else if(nextToken == Token.LEFT_PAREN){// Used to check for calling of expression and lex is called twice for left and right parentheses
            getToken();
            expr();
            if(nextToken == Token.RIGHT_PAREN) {
                getToken();
            }
            else{
                System.out.println("**ERROR** - expected right-parenthesis");
                myOutput.write("**ERROR** - expected right-parenthesis\n");
            }
        }//end if
        else {
            System.out.println("**ERROR** - expected identifier, integer or left-parenthesis");
            myOutput.write("**ERROR** - expected identifier, integer or left-parenthesis\n");
        }
        System.out.println("Exit <factor>");
        myOutput.write("Exit <factor>\n");
    }
}