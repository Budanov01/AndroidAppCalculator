package com.example.calculator;

import java.util.ArrayList;
import java.util.List;

public class Calculator {
    /*------------------------------------------------------------------
     * PARSER RULES
     *------------------------------------------------------------------*/

//    expr : plusminus* EOF ;
//
//    plusminus: multdiv ( ( '+' | '-' ) multdiv )* ;
//
//    multdiv : pow ( ( '^' ) pow )* ;
//
//    pow : unary_minus ( trig_fun );
//
//    trig_fun : ( 'sin' | 'cos' | 'tan'| 'ctn' ) (' factor ')'
//
//    factor : NUMBER | '(' expr ')' ;


    public static double result(String expressionText) throws Exception{
        List<Lexeme> lexemes = lexAnalyze(expressionText);
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        return expr(lexemeBuffer);
    }

    public enum LexemeType {
        LEFT_BRACKET, RIGHT_BRACKET,
        OP_PLUS, OP_MINUS, OP_MUL, OP_DIV,
        OP_TRIG, OP_POWER,
        NUMBER, PI, EXP,
        EOF
    }

    public static class Lexeme {
        LexemeType type;
        String value;

        public Lexeme(LexemeType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class LexemeBuffer {
        private int pos;

        public List<Lexeme> lexemes;

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        public Lexeme next() {
            return lexemes.get(pos++);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }
    }

    public static List<Lexeme> lexAnalyze(String expText) throws Exception{
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos< expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                    pos++;
                    continue;
                case '^':
                    lexemes.add(new Lexeme(LexemeType.OP_POWER, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0' || c == '.') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= '9' && c >= '0' || c == '.');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                    } else if (c <= 'z' && c >= 'a') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= 'z' && c >= 'a');
                        if (sb.toString().equals("pi")){
                            lexemes.add(new Lexeme(LexemeType.PI, sb.toString()));
                        }else if(sb.toString().equals("e")){
                            lexemes.add(new Lexeme(LexemeType.EXP, sb.toString()));
                        }else{
                            lexemes.add(new Lexeme(LexemeType.OP_TRIG, sb.toString()));
                        }
                    }  else {
                        if (c != ' ') {
                            throw new Exception("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }

    public static double expr(LexemeBuffer lexemes) throws Exception{
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    public static double plusminus(LexemeBuffer lexemes) throws Exception{
        double value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_PLUS:
                    value += multdiv(lexemes);
                    break;
                case OP_MINUS:
                    value -= multdiv(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                    lexemes.back();
                    return value;
                default:
                    throw new Exception("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    public static double multdiv(LexemeBuffer lexemes) throws Exception{
        double value = power(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL:
                    value *= power(lexemes);
                    break;
                case OP_DIV:
                    value /= power(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case OP_PLUS:
                case OP_MINUS:
                    lexemes.back();
                    return value;
                default:
                    throw new Exception("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    public static double power(LexemeBuffer lexemes) throws Exception{
        double value = unary_minus(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_POWER:
                    value = Math.pow(value, unary_minus(lexemes));
                    break;
                case OP_MUL:
                case OP_DIV:
                case EOF:
                case RIGHT_BRACKET:
                case OP_PLUS:
                case OP_MINUS:
                    lexemes.back();
                    return value;
                default:
                    throw new Exception("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    public static double unary_minus(LexemeBuffer lexemes) throws Exception{
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.OP_MINUS){
            return -trig_fun(lexemes);
        }else{
            lexemes.back();
            return trig_fun(lexemes);
        }
    }

    public static double trig_fun(LexemeBuffer lexemes) throws Exception{
        Lexeme lexeme = lexemes.next();
        if (lexeme.type.equals(LexemeType.OP_TRIG)){
            switch (lexeme.value){
                case "sin":
                    return Math.sin(factor(lexemes));
                case "cos":
                    return Math.cos(factor(lexemes));
                case "tan":
                    return Math.tan(factor(lexemes));
                case "ctn":
                    return 1 / Math.tan(factor(lexemes));
                default:
                    throw new Exception("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        } else {
            lexemes.back();
            return factor(lexemes);
        }
    }

    public static double factor(LexemeBuffer lexemes) throws Exception{
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case NUMBER:
                return Double.parseDouble(lexeme.value);
            case PI:
                return Math.PI;
            case EXP:
                return Math.E;
            case LEFT_BRACKET:
                double value = plusminus(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new Exception("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
                }
                return value;
            case EOF:
                lexemes.back();
                return 0;
            default:
                throw new Exception("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
        }
    }
}