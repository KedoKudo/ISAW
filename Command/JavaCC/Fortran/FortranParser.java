/* Generated By:JavaCC: Do not edit this line. FortranParser.java */
package Command.JavaCC.Fortran;

import java.io.StringReader;

import java.util.*;

public class FortranParser implements FortranParserConstants {
  //don't want to continually recreate this thing
  private static FortranParser myParser;
  private static boolean appendSemiColon = false;
  private static boolean standalone = false;
  private static final String LOGICAL = "logical";
  private static final String BOOLEAN = "boolean";
  private static final String REAL = "real";
  private static final String FLOAT = "float";
  private static final String F_DOUBLE = "double precision";
  private static final String J_DOUBLE = "double";
  private static final String F_INT = "integer";
  private static final String J_INT = "int";


  /**
   * Method used to run the FortranParser for testing purposes.
   *
   * @param args Unused.
   *
   * @throws ParseException If anything goes wrong during parsing
   */
  public static void main( String args[] ) throws ParseException {
    standalone = true;
    myParser = new FortranParser( System.in );
    myParser.parseCode(  );
  }

  /**
   *  Used to call the parser from an outside class.  Returns a String in Java
   *  for each line of Fortran code.
   *
   * @param text The text to parse.
   *
   * @return The result of the parsing.
   *
   * @throws ParseException If anything goes wrong during parsing
   */
  public static String parseText( String text ) throws ParseException {
    standalone = false;
    //need a semicolon on the end
    if( text.indexOf( ";" ) < 0 ) {
      text = text.trim(  ) + ";";
    }
    if( myParser == null ) {
      myParser = new FortranParser( new StringReader( text )  );
    } else {
      myParser.ReInit( new StringReader( text )  );
    }
    String line = myParser.parseCode(  );

    if( line == null || line.length(  ) <= 0 ) {
      return "";
    }  else  {
      return line;
    }
  }

  /**
   * Method to convert fromFortran style array and variable 
   * declaration to Java style array and variable declaration.
   * If no array declarations exist, this essentially just converts from
   * Fortran type declaration to Java type declaration.  This method 
   * "knows" how to convert from, say, a Fortran double to a Java
   * double
   * 
   * @param s The String to convert Fortran arrays from.
   * 
   * @return The String representing the passed in String converted
   *                 to Java style array or scalar declarations.
   */
  private static String convertToJavaStyle(
    String s ) {
    String type = null;

    s = s.replaceAll( ".true.", "true" ).replaceAll( ".false.", "false" );

    if( s.indexOf( LOGICAL ) >= 0 ) {
      type = BOOLEAN;
      s = s.replaceAll( LOGICAL, "");
    } else if( s.indexOf( F_DOUBLE ) >= 0 ) {
      type = J_DOUBLE;
      s = s.replaceAll( F_DOUBLE, "");
    } else if( ( ( s.indexOf( F_INT ) >= 0 ) ||
                    ( s.startsWith( "i | j | k | l | m | n ") ) ) ) {
      type = J_INT;
      s = s.replaceAll( F_INT, "");
    } else {
      type = FLOAT;
      s = s.replaceAll( REAL, "");
    }

    StringBuffer buffer = new StringBuffer( type );
    buffer.append( " " );
    //make sure these get a semicolon
    appendSemiColon = true;
    s = s.trim(  );
    //the parentheses should NOT be right at the beginning of the declaration
    if( s.indexOf( "(" ) > 0 ) {
      String tokenList[] = s.split( "," );
      //now go through the tokens, determining if an element is an array
      String[] temp;
      for( int i = 0; i < tokenList.length; i++ ) {
        if( tokenList[i].indexOf( "(" ) >= 0 ) {
          //change to Java style array delimiters
          tokenList[i] = tokenList[i].replace( '(', '[' );
          tokenList[i] = tokenList[i].replace( ')', ']' );
          //this will split the String just after the variable name.  We
          //will have to put the '[' back on however
          temp = tokenList[i].split( "\\[" );
          tokenList[i] = temp[0] + " = new " + type + "[" + temp[1];

        }

        //put the token (array element or not) in
        buffer.append( tokenList[i] );

        //basically, put the commas back in
        if( i < tokenList.length - 1 ) {
          buffer.append( "," );
        }
      }
    } else {
      //just return what we had after initial replacements
      buffer.append( s );
    }

    return buffer.toString(  );
  }

  /**
   * Finds the first word in the String and prepends a "Math." onto it.
   * The assumption with this is that the argument to the function is 
   * the correct one (i.e. float).
   * 
   * @param s The String that is to be converted.
   * 
   * @return The converted String.
   */
  private static String convertFPMath( String s ){
    //int letterIndex = s.indexOf( "^")
    return "Math." + s.trim(  );
  }

/**
 * This method goes through a line of Fortran code, converting it to Java.
 * It also has the ability to test the parser by checking the standalone
 * quality and bringing up a command line prompt if it should run 
 * standalone.
 * 
 * @return The converted Fortran-to-Java code.
 */
  static final public String parseCode() throws ParseException {
  String codeLine = "";
    if( standalone ) {
      /*System.out.println( "Please type in a line of Fortran code " +
        "or ^D to quit:\n" );*/
    }
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LINE_END:
      case EMPTY_LINE:
      case STRING:
      case FORTRAN_COMMENT:
      case FORTRAN_MATH_FUN:
      case FORTRAN_ABS:
      case FORTRAN_FLOAT_FUN:
      case FORTRAN_TRUNC_FUN:
      case FORTRAN_MOD_FUN:
      case FORTRAN_FRACTION_FUN:
      case FORTRAN_EXPRESSION:
      case SINGLE_IF:
      case ELSE_COND:
      case START_MULTI_IF:
      case END_IF:
      case FORTRAN_DO_START:
      case FORTRAN_END_DO:
      case VAR_ASSIGN:
      case FORTRAN_VARIABLE:
      case FORTRAN_CHAR_1:
      case FORTRAN_CHAR_2:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      codeLine = convertFortranToJava();
      jj_consume_token(LINE_END);
      //a newline ends the expansion, and the parser expects one, so tack it on
      //print the parsed expression
      if( standalone ) {
        System.out.print( codeLine );
        /*System.out.println(  );
        System.out.println( "Please type in another line of Fortran code or ^D to quit:" );
        System.out.println(  );*/
      }
    }
    {if (true) return codeLine;}
    jj_consume_token(0);
    throw new Error("Missing return statement in function");
  }

/**
 * This method actually converts the line of Fortran to Java.
 * 
 * @return A line of code converted from Fortran to Java. 
 */
  static final public String convertFortranToJava() throws ParseException {
  StringBuffer fCode = new StringBuffer(  );
  String fToken;
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EMPTY_LINE:
      case STRING:
      case FORTRAN_COMMENT:
      case FORTRAN_MATH_FUN:
      case FORTRAN_ABS:
      case FORTRAN_FLOAT_FUN:
      case FORTRAN_TRUNC_FUN:
      case FORTRAN_MOD_FUN:
      case FORTRAN_FRACTION_FUN:
      case FORTRAN_EXPRESSION:
      case SINGLE_IF:
      case ELSE_COND:
      case START_MULTI_IF:
      case END_IF:
      case FORTRAN_DO_START:
      case FORTRAN_END_DO:
      case VAR_ASSIGN:
      case FORTRAN_VARIABLE:
      case FORTRAN_CHAR_1:
      case FORTRAN_CHAR_2:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_2;
      }
      /*( ( <WHITESPACE> )* )*/ fToken = convertFortranTokens();
    fCode.append( fToken );
    fCode.append( " " );
    }
    if( appendSemiColon ) {
      appendSemiColon = false;
      {if (true) return fCode.toString(  ) + ";\n";}
    } else {
      {if (true) return fCode.toString(  ).replaceAll( "\\t", "  " ) + "\n";}
    }
    throw new Error("Missing return statement in function");
  }

/**
 * This method goes through the tokens defined earlier in this file and 
 * changes each to a Java token.
 * 
 * @return The converted token.
 */
  static final public String convertFortranTokens() throws ParseException {
  Token t;
  String s;
  StringBuffer buffer;
  String tokenList[];
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case FORTRAN_COMMENT:
      //matched a comment
        t = jj_consume_token(FORTRAN_COMMENT);
    s = t.image;
    //we know that the comment 'C' is always in the first column
    s = s.substring( 1, s.length(  ) );
    s = "//" + s;
    {if (true) return s;}
      break;
    case FORTRAN_EXPRESSION:
      //some kind of math expression
        t = jj_consume_token(FORTRAN_EXPRESSION);
    {if (true) return t.image;}
      break;
    case VAR_ASSIGN:
      //variable assignment
        t = jj_consume_token(VAR_ASSIGN);
    appendSemiColon = true;
    {if (true) return t.image;}
      break;
    case SINGLE_IF:
      //single line if
        t = jj_consume_token(SINGLE_IF);
    appendSemiColon = true;
    {if (true) return t.image.replaceAll( ".lt.", "<" )
                          .replaceAll( ".gt.", ">" )
                          .replaceAll( ".ge.", ">=")
                          .replaceAll( ".le.", "<=");}
      break;
    case ELSE_COND:
      t = jj_consume_token(ELSE_COND);
    {if (true) return t.image.replaceAll( "else", "}else{");}
      break;
    case START_MULTI_IF:
      //start of multi line if
        t = jj_consume_token(START_MULTI_IF);
    {if (true) return t.image.replaceAll( ".lt.", "<" )
                          .replaceAll( ".gt.", ">" )
                          .replaceAll( ".ge.", ">=")
                          .replaceAll( ".le.", "<=")
                          .replaceAll( "elseif", "}else if" )
                          .replaceAll( "then", "{");}
      break;
    case END_IF:
      //end if
        t = jj_consume_token(END_IF);
    {if (true) return t.image.replaceAll( "endif", "}" );}
      break;
    case FORTRAN_DO_START:
      //start of a do statement..we'll convert this to a for loop
        //I am guessing that Fortran uses a <= by default...
        t = jj_consume_token(FORTRAN_DO_START);
    //consume the 'do' and the label
    s = t.image.trim(  );
    s = s.substring( s.indexOf( " " ), s.length(  ) ).trim(  );
    s = s.substring( s.indexOf( " " ), s.length(  ) ).trim(  );

    //this will give us something like {i = 1, n} or { i = 1, n, 3}
    //if a step was given
    tokenList = s.split( "," );

    buffer = new StringBuffer( "for( " );

    //I want to keep some of these variables local to this case
    {
      int step = 1;
      String tokenList2[] = tokenList[0].split( "=" );
      String intVar = tokenList2[0].trim(  );
      String start = tokenList2[1].trim(  );
      String end = tokenList[1].trim(  );

      if( tokenList.length == 3 ) {
        step = Integer.parseInt( tokenList[2].trim(  ) );
      }

      buffer.append( intVar );
      buffer.append( " = ");
      buffer.append( start );
      buffer.append( "; ");
      buffer.append( intVar );

      if( step >= 0 ) {
        buffer.append( " <= ");
      } else {
        buffer.append( " >= ");
      }

      buffer.append( end );
      buffer.append( "; " );
      buffer.append( intVar );
      buffer.append( " = " );
      buffer.append( intVar );
      buffer.append( " + ( " );
      buffer.append( step );
      buffer.append( " ) ) {");
    }
    {if (true) return buffer.toString(  );}
      break;
    case FORTRAN_END_DO:
      //the end of the do... loop
        t = jj_consume_token(FORTRAN_END_DO);
    {if (true) return "}";}
      break;
    case FORTRAN_ABS:
      //matched the absolute function
        t = jj_consume_token(FORTRAN_ABS);
    s = t.image;
    if( s.indexOf( "iabs" ) >= 0 ) {
      s = s.replaceFirst( "iabs", "Math.abs" );
    } else {
      s = s.replaceFirst( "abs", "Math.abs" );
    }

    {if (true) return s;}
      break;
    case FORTRAN_MATH_FUN:
      //generic simple floating point math functions
        t = jj_consume_token(FORTRAN_MATH_FUN);
    {if (true) return convertFPMath( t.image );}
      break;
    case FORTRAN_FRACTION_FUN:
      //fraction function
        t = jj_consume_token(FORTRAN_FRACTION_FUN);
    //based upon the Javadocs for the Math.IEEERemainder() method,
    //this will compute the remainder of whatever the argument was
    //divided by 1 (e.g. 3.14 / 1, which would give 0.14 as a remainder
    s = t.image;
    s = s.replaceAll( "fraction", "" ).replaceAll( "\\(", "").replaceAll( "\\)", "");
    s = "Math.IEERemainder( " + s.trim(  ) + ", 1)";

    {if (true) return s;}
      break;
    case FORTRAN_FLOAT_FUN:
      //matched the float() function
        t = jj_consume_token(FORTRAN_FLOAT_FUN);
    s = t.image;
    if( s.indexOf( "float" ) >= 0 ) {
      s = s.replaceAll( "float", "" );
    } else {
      s = s.replaceAll( "real", "" );
    }

    s = "Float.parseFloat" + s;

    {if (true) return s;}
      break;
    case FORTRAN_TRUNC_FUN:
      //matched the truncate function
        t = jj_consume_token(FORTRAN_TRUNC_FUN);
    s = t.image.replaceAll( "int", "" );

    s = "new Float" + s + ".intValue()";

    {if (true) return s;}
      break;
    case FORTRAN_MOD_FUN:
      //matched the mod() function
        t = jj_consume_token(FORTRAN_MOD_FUN);
    s = t.image;
    s = s.replaceAll( "mod", "" ).replaceAll( "\\)", "" )
                                               .replaceAll( "\\(", "" )
                                               .replaceAll( ",", " % " );
    {if (true) return s;}
      break;
    case FORTRAN_VARIABLE:
      t = jj_consume_token(FORTRAN_VARIABLE);
    {if (true) return convertToJavaStyle( t.image );}
      break;
    case FORTRAN_CHAR_1:
      //matched a character or character array where each variable is
        //explicitly given a length
        t = jj_consume_token(FORTRAN_CHAR_1);
    appendSemiColon = true;
    s = t.image;
    buffer = new StringBuffer( "char " );

    //dump the "character" stuff
    s = ( s.replaceAll( "character", "" ) ).trim(  );
    //now we will create character arrays for each of the Fortran
    //character declarations.  The assumption is that we will have a 
    //comma delimited list of name, number pairs at this point
    tokenList = s.split( "\\s+|\\s*,\\s*" );

    for( int i = 0; i < tokenList.length; i++ ) {
      if( ( i % 2 )  == 0 ) {
        //we found a variable name, so append it and the array declaration
        buffer.append( tokenList[i] );
        buffer.append( " = new char[" );
      } else {
        //we found the array dimension, so append that
        buffer.append( tokenList[i].replaceAll( "\\*", "" ) );
        buffer.append( "]" );

        //if we haven't reached the end of our token list, append a 
        //comma
        if( i < tokenList.length - 2 ) {
                buffer.append( "," );
        }
      }
    }

    {if (true) return buffer.toString();}
      break;
    case FORTRAN_CHAR_2:
      //matched a character or character array where each variable is
        //given a length specified in the character *n declaration
        t = jj_consume_token(FORTRAN_CHAR_2);
    appendSemiColon = true;
    s = t.image;
    buffer = new StringBuffer( "char " );

    //dump the "character" stuff
    s = ( s.replaceAll( "character", "" ) ).trim(  );
    //break the declarations up by comma and space.  The first token is
    //the number to use for length

    tokenList = s.split( "\\s+|\\s*,\\s*" );
    String length = tokenList[0].replaceAll( "\\*", "" );

    for( int i = 1; i < tokenList.length; i++ ) {
      buffer.append( tokenList[i] );
      buffer.append( " = new char[" );
      //first element is array length
      buffer.append( length );
      buffer.append( "]" );

      if( i < tokenList.length - 1 ) {
        buffer.append( "," );
      }
    }

    {if (true) return buffer.toString();}
      break;
    case STRING:
      //matched a String
        t = jj_consume_token(STRING);
    //replace Fortran's double single quotes with a backslash quote
    {if (true) return t.image.replaceAll( "''", "\\\\\"");}
      break;
    case EMPTY_LINE:
      //found an empty line, so spit it back
        t = jj_consume_token(EMPTY_LINE);
    {if (true) return "";}
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_initialized_once = false;
  static public FortranParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  static public Token token, jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[3];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_0();
      jj_la1_1();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x8fc0900a,0x8fc09008,0x8fc09008,};
   }
   private static void jj_la1_1() {
      jj_la1_1 = new int[] {0x1dfc,0x1dfc,0x1dfc,};
   }

  public FortranParser(java.io.InputStream stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new FortranParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  static public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  public FortranParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new FortranParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  public FortranParser(FortranParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  public void ReInit(FortranParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 3; i++) jj_la1[i] = -1;
  }

  static final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.Vector jj_expentries = new java.util.Vector();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  static public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[45];
    for (int i = 0; i < 45; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 3; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 45; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  static final public void enable_tracing() {
  }

  static final public void disable_tracing() {
  }

}
