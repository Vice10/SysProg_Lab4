import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;

public class Lexer {
    private List<String> keywords = Arrays.asList(new String[]{"int", "double", "char", "float", "cout", "if",
            "else", "true", "false", "include", "pragma", "while"});
    private List<String> messages = new LinkedList<String>();
    private String tape;
    private int curInd = -1;
    char nextch;
    Lexer(String tape){
        this.tape = tape;
        getch();
    }

    public Token getToken() { // return next input token
        Token result;
        skipWhiteSpace();
        if (curInd == tape.length() - 1) {
            result = Token.EOF; return result;
        }
        switch(nextch) {
            case '\n': result = Token.NLINE; getch(); return result;
            case '\r': result = Token.RSYM; getch(); return result;
            case ' ' : result = Token.SPACE; getch(); return result;

            case '(': result = Token.LPAREN; messages.add("<(> R :: "+result.name()); getch(); return result;
            case ')': result = Token.RPAREN; messages.add("<)> R :: "+result.name()); getch(); return result;
            case '{': result = Token.CBLEFT; messages.add("<{> R :: "+result.name()); getch(); return result;
            case '}': result = Token.CBRIGHT; messages.add("<}> R :: "+result.name()); getch(); return result;
            case ';': result = Token.SCOLON; messages.add("<;> R :: "+result.name()); getch(); return result;

            case '=': result = Token.EQ; messages.add("<=> R :: "+result.name()); getch(); return result;
            case '#':
                getch();
                String regex = "[a-z]";
                String dumStr = "", s = null;
                dumStr += nextch;
                while (Pattern.matches(regex, dumStr) && nextch != ' ') {
                    s = s + nextch;
                    getch();
                    dumStr = "";
                    dumStr += nextch;
                }
                s = s.substring(4);
                if(keywords.contains(s)){
                    result = Token.DLB;
                    messages.add("< #"+s+" > R :: "+result.name()); getch();
                    return result;
                }
                else {
                    result = Token.DLB;
                    messages.add("< #"+s+" N/R :: "+result.name()); getch();
                    return result;
                }

            case '!': // ! or !=
                skipWhiteSpace();
                getch();
                if (nextch == '=') {
                    result = Token.NEQ; messages.add("< != > R :: "+result.name()); getch(); return result;
                } else if(nextch == '\0') {
                    result = Token.NOT; messages.add("< ! > R :: "+result.name()); return result;
                }else {
                    String badInp = "";
                    while (nextch != '\0'){
                        badInp += nextch;
                        getch();
                    }
                    result = Token.NOT; messages.add("< !"+badInp+" > N/R :: "+result.name());
                    getch(); return result;
                }
            case '<': // < or <= or <<
                skipWhiteSpace();
                getch();
                if (nextch == '=') {
                    result = Token.LEQ; getch(); messages.add("< <= > R :: "+result.name()); return result;
                } else if(nextch == ' '){
                    result = Token.LESS; messages.add("< < > R :: "+result.name()); return result;
                }else if(nextch == '<'){
                    result = Token.COUT; getch(); messages.add("< << > R :: "+result.name()); return result;
                }else {
                    String libName = "";
                    String dumCh = "";
                    libName += nextch;
                    dumCh += nextch;
                    regex = "[a-zA-Z_0-9;.+]";
                    while (Pattern.matches(regex, dumCh)){
                        getch();
                        libName += nextch;
                        dumCh = ""; dumCh += nextch;
                    }

                    result = Token.LIBNAME; messages.add("< <"+libName+" > R :: "+result.name());
                    getch(); return result;
                }
            case '>': // > or >=
                skipWhiteSpace();
                getch();
                if (nextch == '=') {
                    result = Token.GREQ; getch(); messages.add("< >= > R :: "+result.name()); getch(); return result;
                } else if(nextch == ' '){
                    result = Token.GR; messages.add("< > > R :: "+result.name()); return result;
                }else {
                    String badInp = "";
                    badInp += nextch;
                    regex = "[a-zA-Z_0-9;]";
                    while (!Pattern.matches(regex, badInp)){
                        getch();
                        badInp += nextch;
                    }
                    result = Token.GR; messages.add("< >"+badInp+" > N/R :: "+result.name());
                    getch(); return result;
                }

            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':
                // integer constant
                boolean isFloat = false;
                String num = "";
                num += nextch;
                getch();
                while (nextch >= '0' && nextch <= '9') {
                    num = num + nextch;
                    getch();
                }
                if(nextch == '.'){
                    while (nextch >= '0' && nextch <= '9') {
                        num = num + nextch;
                        getch();
                    }
                    isFloat = true;
                }
                if(nextch == ';' || nextch == '+' || nextch == '-' ||
                        nextch == '*' || nextch == '/' || nextch == ' ' ||
                        nextch == ')' || nextch == '(') {
                    if(isFloat)
                        result = Token.FLTP;
                    else result = Token.INT;
                    messages.add("< "+num+" > R :: "+result.name());
                    return result;
                }
                else {
                    String badInp = "";
                    s = "";
                    badInp += nextch;
                    regex = "^[0-9,;]+$";
                    while (!Pattern.matches(regex, badInp)){
                        getch();
                        badInp = "";
                        badInp += nextch;
                        s += nextch;
                    }
                    result = Token.INT;
                    messages.add("< "+num+s+" > N/R :: "+result.name());
                    return result;
                }


            case 'a': case 'b':case 'c':case 'd':case 'e':case 'f':case 'g':case 'h':case 'i':case 'j':case 'k':
            case 'l':case 'm':case 'n':case 'o':case 'p':case 'q':case 'r':case 's':case 't':case 'u':
            case 'v':case 'w':case 'x':case 'y':case 'z':
            case 'A': case 'B':case 'C':case 'D':case 'E':case 'F':case 'G':case 'H':case 'I':case 'J':case 'K':
            case 'L':case 'M':case 'N':case 'O':case 'P':case 'Q':case 'R':case 'S':case 'T':case 'U':
            case 'V':case 'W':case 'X':case 'Y':case 'Z': // id or keyword
                s = null;
                s += nextch;
                getch();
                regex = "[a-zA-Z_0-9]";
                dumStr = "";
                dumStr += nextch;
                while (Pattern.matches(regex, dumStr)) {
                    s = s + nextch;
                    getch();
                    dumStr = "";
                    dumStr += nextch;
                }
                //s += nextch;
                s = s.substring(4);
                if (keywords.contains(s)) {
                    result = Token.KWORD;
                    messages.add("< "+s+" > R :: "+result.name());
                } else {
                    result = Token.ID;
                    messages.add("< "+s+" > R :: "+result.name());
                }
                return result;
            default: messages.add("< "+nextch+" > N/R :: "+Token.FUS);
        }
        return Token.FUS;
    }

    private void skipWhiteSpace() {
        while(curInd < tape.length() && nextch == ' ')
            getch();
    }


    private void getch(){
        if (tapeNotOver()){
            nextch = tape.charAt(++curInd);
        }
        else return;
    }

    public boolean tapeNotOver(){
        if(curInd < tape.length())
            return true;
        return false;
    }

    public void displayMsg(){
        for (String mes:
             messages) {
            System.out.println(mes + "\n");
        }
    }
}