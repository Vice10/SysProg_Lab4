import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Dispatcher {
    public static void main(String[] argv){
        String tape = readFile();
        //System.out.println(tape);
        Lexer lex = new Lexer(tape);
        List<Token> tokens = new LinkedList<Token>();
        while(!tokens.contains(Token.EOF)){
            tokens.add(lex.getToken());
        }
        lex.displayMsg();
    }

    public static String readFile(){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\JavaProjects\\SysProg\\Lab4\\src\\input.txt"));
            String line = null;
            String ls = System.getProperty("line.separator");
        try{
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            // delete the last new line separator
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            reader.close();
        }
        catch (IOException ex){
            ex.getMessage();
        }
        }
        catch (Exception ex){
            ex.getMessage();
        }
        String content = stringBuilder.toString();
        return content;
    }
}
