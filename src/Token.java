public enum Token{
    EOF, LPAREN, RPAREN, SCOLON, NEQ, NOT, LEQ, LESS, INT, KWORD, ID, NLINE,
    CBLEFT, CBRIGHT, GR, GREQ, EQ, SPACE, FUS, RSYM, COUT, DLB, LIBNAME, FLTP;
    Token(String tk){
        System.out.println("Token recognised: " + tk + "\n");
    }
    Token(){}
}
