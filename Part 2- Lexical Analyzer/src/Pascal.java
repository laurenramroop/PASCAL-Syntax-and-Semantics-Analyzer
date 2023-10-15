import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Pascal {
    //lists to compare to token to determine identifier 
    //lists to compare tokens for lexical analysis 
    public final static List<String> RESERVED_WORDS = Arrays.asList("and", "array", "begin", "case", "const", 
         "div", "do" , "downto", "else", "end ", "file", "for", "function", "goto", "if", "in", "label", "mod", "nil", 
         "not", "of", "or", "packed", "procedure", "program", "record", "repeat", "set", "then", 
         "to", "type", "until", "var", "while", "with");
    public final static List<String> ASSIGN_OP = Arrays.asList("+", "-", "*", "/", "%",",", ".", ";",
         ";." , "`", "=", "==", "<>", "<", ">", 
        "<=", ">=", ":=", ".", "..");
    public final static String VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";  

    /**
     * Read through line by line
     * @param file the file to be read
     * @return null
     */
    public static void tokenizer(String file) throws IOException {
        String line, str;
        String wholeFile = "";
        int lineNumber = 0;
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            //read through each line 
            while ((line = br.readLine()) != null) {
                //tokens for lexical analyzer
                String[] tokens = line.split(" ");
                //line with no whitespace for syntax analysis 
                str = line.replaceAll("\\s", "");
                
                //concatenate entire file into a string 
                wholeFile += line;

                for (String token: tokens){
                    if (!token.trim().isEmpty()) {
                        //ignore ";" or ":" at end of token 
                        if (token.endsWith(";") || token.endsWith(".")) 
                            token = token.substring(0, token.length() - 1); 
                        String type1 = validIdentifier(token);
                        //System.out.printf("%-10s %s%n", type1, token);
            
                    }
                }

                System.out.print("Line " + lineNumber++ + ": ");
                String type2 = validSyntax(str);
                System.out.printf("%-10s %s%n", type2, str);
                
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        br.close();
    }
    
    /**
     * analyzes what type of identifier each token corresponds to 
     * @param token the word in text file to be analyzed
     * @return a string coresponding to the proper identifier  
     */
    public static String validIdentifier(String token) {
        if (RESERVED_WORDS.contains(token.toLowerCase())) {
            return "RESERVED: ";
        } else if (ASSIGN_OP.contains(token)) {
            return "OPERATOR: ";
        } else if (isInteger(token)) {
            return "INT_CONST: ";
        } else if (isRealNum(token)) {
            return "REAL_CONST: ";
        } else if (isComment(token)){
            return "COMMENT: ";
        } else if (isVariable(token.toLowerCase())) {
            return "VARIABLE: ";
        } else if (isString(token)) {
            return "STRING: ";
        } else {
            return "UNKNOWN: ";
        }
    }
    public static boolean isAssignment(String str){
        if (str.contains(":=")) return true;
        else return false;
    }
    
    /**
     * A boolean function to determine if a token is a variable 
     * @param token the word in text file to be analyzed
     * @return true if variable, false if otherwise
     */
    public static boolean isVariable(String token) {
        if (token.length() == 0) return false;
        //if first character isnt a letter return false
        if (!Character.isLetter(token.charAt(0))) return false;
        if (!(token.endsWith(":"))) return false;

        for (int i = 1; i < token.length()-1; i++) {
            //if the token doesn't contain any valid chars
            if (!VALID_CHARS.contains(String.valueOf(token.charAt(i)))) 
                return false;
        }
        return true;
    }

    /**
     * A boolean function that evaluates if token is a real number
     * @param token to be analyzed
     * @return true/false
     */
    public static boolean isRealNum(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * A boolean function that evaluates if token is an integer
     * @param token to be analyzed
     * @return true/false
     */
    public static boolean isInteger(String token) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        return pattern.matcher(token).matches();
    }

    /**
     * A boolean function that evaluates if token is a string
     * @param token to be analyzed
     * @return true/false
     */
    public static boolean isString(String token) {
        if (token.length() == 0) return false;
        //if first character isnt a letter return false
        if (!Character.isLetter(token.charAt(0))) return false;
        //if string has at least one character and either starts/ends with a " or ' or and has valid chars
        for (int i = 1; i < token.length(); i++) {
            if (!VALID_CHARS.contains(String.valueOf(token.charAt(i))) || token.endsWith(":")) 
                return false;
        }

       return true;
    }

    /**
     * A boolean function that evaluates if token is a comment
     * @param token to be analyzed
     * @return true/false
     */
    public static boolean isComment(String token) {
        //if token contians '(*'' or '*)' or '{' or '}'
        if ((token.startsWith("(*") || token.startsWith("*)")) || (token.startsWith("{") || token.startsWith("}")))
            return true;
        else return false;
    }

    /**
     * Code to analyze the correctness of syntax
     * @param str the line with no whitespace to be analyzed
     * @return a statement corresponding to the state of syntax (correct/not correct)
     */
    private static String validSyntax(String str){
        if (isComment(str)){
            if (commentSyntax(str)== true){
                return "Correct Comment: ";
            }else{
                return "Wrong Comment Syntax.";
            }
        }else if (str.contains("PROGRAM")){
            if (programSyntax(str) == true){
                return "Correct Program Title: ";
            }else{
                return "Wrong Program Title Syntax: ";
            }
        }else if (str == ""){
            return " ";
        }else if (str.contains("CONST")){
            if (constSyntax(str) == true){
                return "Correct Const declaration: ";
            }else{
                return "Must only contain 'CONST' keyword";
            }
        }else if (isAssignment(str)){
            if (assignmentSyntax(str) == true){
                return "Correct assignment: ";
            }else{
                return "Wrong assignment: ";
            }
            
        }else if (str.contains("IF")){
            if (isIfElseSyntax(str) == true){
                return "Correct if else: ";
            }else{
                return "Incorrect if else: ";
            }
        }else if(str.contains("=")){
            if (assignmentSyntax(str) == true){
                return "Correct assignment: ";
            }else{
                return "Incorrect assignment: ";
            }
        }else if (beginEndSyntax(str)){
            return "Correct placement of BEGIN END";
        }else if (isVariable(str)==true){
            if (variableSyntax(str) == true)
                return "Correct variable assignment: ";
            else
                return "Incorrect variable assignment: ";
        }else if (str.contains("IF") || str.contains("THEN")){
            if (isIfElseSyntax(str) == true){
                return "Correct if-then format: ";
            }else{
                return "Incorrect if-then format; ";
            }
        }else if (str.contains("(") || str.contains(")")){
            if (isFunctionSyntax(str) == true){
                return "correct function format: ";
            }else{
                return "incorrect function format: ";
            }
        }
        
        return "Invalid syntax. ";
    }

     //NEW SYNTAX METHODS 
    private static boolean constSyntax(String str){
        if (str.equalsIgnoreCase("CONST")){
            return true;
        }else{
            return false;
        }
    }
    private static boolean commentSyntax(String str){
        if (str.startsWith("(*") && str.endsWith("*)")){
            return true; 
        }else if(str.startsWith("{") && str.endsWith("}")){
            return true;
        }else 
            return false;
    }

    private static boolean programSyntax(String str){
        if (str.matches("PROGRAM(\\S+)?")) return true;
        else return false; 
    }
    private static boolean assignmentSyntax(String str){
        //regex: var = const
        if (str.matches("[\\S+][=][-?\\d+(\\.\\d+)?][;]")) return true;
        else return false;
    }

    private static boolean variableSyntax(String str){
        //regex: name: data_type
        if (str.matches("[\\S+][:](INTEGER|CHARACTER|BOOLEAN|ENUMERATED|STRING)[;]"))
            return true;
        else 
            return false;
    }

    private static boolean arithmeticOpSyntax(String str){
        //regex: name = value operation value 
        if (str.matches("^[a-zA-Z]+[=][S+][+|-|*|div|mod][S+][;]"))
            return true;
        else 
            return false;
    }
    private static boolean isFunctionSyntax(String str){
        //if word(statement) matches return true 
        if (str.matches("^[a-zA-z]+[(][\\S+][)][;]")) 
            return true;
        else 
            return false;
    }

    private static boolean booleanSyntax(String str){
        // statement operator statemnt
        if (str.matches("\\S+(>|<|>=|<=|==|NOT|AND|OR|XOR)\\S+"))
            return true;
        else
            return false; 
    }
    
    private static boolean isIfElseSyntax(String str){
        //IF statement THEN statement
        if (str.matches("(IF)(\\S+)(THEN)(\\S+)"))
            return true;
        else 
            return false;
    }

    private static boolean beginEndSyntax(String wholeFile){
        //format: beginning BEGIN middle_stuff END (optional comments)
        if (wholeFile.matches("^(.+)(BEGIN)(.+)(END)$(\\(*)(.+)*\\))?"))
            return true;
        else 
            return false;
    }
}


