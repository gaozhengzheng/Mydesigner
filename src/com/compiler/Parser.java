package com.compiler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

/**
 * 语法分析器
 * 
 * @author KB
 *
 */
public class Parser
{  
    /**
     * @param args
     */
    private LexAnalyse lexAnalyse; // 词法分析器
                                                                                
    ArrayList<Word>wordList= new ArrayList<Word>();// 单词表                                                                            
    Stack<AnalyseNode>analyseStack= new Stack<AnalyseNode>();// 分析栈                                                                              
    Stack<String>semanticStack= new Stack<String>();// 语义栈                                                                           
    public ArrayList<FourElement>fourElemList= new ArrayList<FourElement>(); // 四元式列表                                                                           
    public ArrayList<Error>errorList= new ArrayList<Error>(); // 错误信息列表                                                                           
    StringBuffer bf; // 分析栈缓冲流                                                                          
    int errorCount= 0; // 统计错误个数                                                                          
    public boolean graErrorFlag = false;// 语法分析出错标志                                                                            
    int tempCount= 0; // 用于生成临时变量                                                                            
    int fourElemCount = 0; // 统计四元式个数                                                                            
    AnalyseNode  S, A, B, C, D, E, F, G, H, L, M, O, P, Q, R,
                 T, U, X, Y, Z, Z1, U1, E1, H1, L1, T1; // 非终结符  
    AnalyseNode ADD_SUB, DIV_MUL, ADD, SUB, DIV, MUL, ASS_F,
            ASS_R, ASS_Q, ASS_U, TRAN_LF;    // 算术表达式和赋值语句 
    AnalyseNode SINGLE, SINGLE_OP, EQ, EQ_U1, COMPARE,
            COMPARE_OP, SCANF, PRINTF, IF_FJ, IF_RJ, IF_BACKPATCH_FJ,
            IF_BACKPATCH_RJ;
    AnalyseNode  WHILE_FJ, WHILE_RJ, WHILE_BACKPATCH_FJ,
            FOR_FJ, FOR_RJ, FOR_BACKPATCH_FJ;
    AnalyseNode  top; // 当前栈顶元素                                                                
    Word firstWord,SecondWord; // 待分析单词                                                                            
    String OP= null; // 四元式操作符                                                                          
    String ARG1, ARG2, RES;// 操作数和结果
    int OUTPORT;
    Error error; // 错误                                                                         
    // int if_fj,if_rj,while_fj,while_rj,for_fj,for_rj;
    Stack<Integer> if_fj, if_rj, while_fj, while_rj, for_fj,for_rj;                                                            // if
                                                                                // while
                                                                                // for
                                                                                // 跳转地址栈类型为整数      
    Stack<String>for_op= new Stack<String>();
    public ArrayList<String>fourElemT= new ArrayList<String>();
    public Parser()// 默认方法
    {
        
    }
    public Parser(LexAnalyse lexAnalyse)// 初始化词法分析器
    {
        this.lexAnalyse = lexAnalyse;
        this.wordList = lexAnalyse.wordList;
        init();//语法分析器初始化函数创建非终结符和动作节点对象
    }
    
    private String newTemp()// 生成临时变量
    {
        tempCount++;
        fourElemT.add("T" + tempCount);// 临时变量名
        return "T" + tempCount;
    }
    
    public void init()
    {
        // 非终结符
        S = new AnalyseNode(AnalyseNode.NONTERMINAL, "S", null);
        A = new AnalyseNode(AnalyseNode.NONTERMINAL, "A", null);
        B = new AnalyseNode(AnalyseNode.NONTERMINAL, "B", null);
        C = new AnalyseNode(AnalyseNode.NONTERMINAL, "C", null);
        X = new AnalyseNode(AnalyseNode.NONTERMINAL, "X", null);
        Y = new AnalyseNode(AnalyseNode.NONTERMINAL, "Y", null);
        Z = new AnalyseNode(AnalyseNode.NONTERMINAL, "Z", null);
        Z1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "Z'", null);
        U = new AnalyseNode(AnalyseNode.NONTERMINAL, "U", null);
        U1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "U'", null);
        E = new AnalyseNode(AnalyseNode.NONTERMINAL, "E", null);
        E1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "E'", null);
        H = new AnalyseNode(AnalyseNode.NONTERMINAL, "H", null);
        H1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "H'", null);
        G = new AnalyseNode(AnalyseNode.NONTERMINAL, "G", null);
        F = new AnalyseNode(AnalyseNode.NONTERMINAL, "F", null);
        D = new AnalyseNode(AnalyseNode.NONTERMINAL, "D", null);
        L = new AnalyseNode(AnalyseNode.NONTERMINAL, "L", null);
        L1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "L'", null);
        T = new AnalyseNode(AnalyseNode.NONTERMINAL, "T", null);
        T1 = new AnalyseNode(AnalyseNode.NONTERMINAL, "T'", null);
        O = new AnalyseNode(AnalyseNode.NONTERMINAL, "O", null);
        P = new AnalyseNode(AnalyseNode.NONTERMINAL, "P", null);
        Q = new AnalyseNode(AnalyseNode.NONTERMINAL, "Q", null);
        R = new AnalyseNode(AnalyseNode.NONTERMINAL, "R", null);
        
        // 动作符号
        ADD_SUB = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ADD_SUB", null);
        ADD = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ADD", null);
        SUB = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SUB", null);
        DIV_MUL = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@DIV_MUL", null);
        DIV = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@DIV", null);
        MUL = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@MUL", null);
        ASS_F = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS_F", null);
        ASS_R = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS_R", null);
        ASS_Q = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS_Q", null);
        ASS_U = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@ASS_U", null);
        TRAN_LF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@TRAN_LF", null);
        SINGLE = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SINGLE", null);
        SINGLE_OP = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SINGLE_OP", null);
        EQ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@EQ", null);
        EQ_U1 = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@EQ_U'", null);
        COMPARE = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@COMPARE", null);
        COMPARE_OP = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@COMPARE_OP",null);
        IF_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_FJ", null);
        SCANF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@SCANF", null);
        PRINTF = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@PRINTF", null);
        IF_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@IF_RJ", null);
        IF_BACKPATCH_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN,"@IF_BACKPATCH_FJ", null);
        IF_BACKPATCH_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN,"@IF_BACKPATCH_RJ", null);
        WHILE_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@WHILE_FJ", null);
        WHILE_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@WHILE_RJ", null);
        WHILE_BACKPATCH_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN,"@WHILE_BACKPATCH_FJ", null);
        FOR_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@FOR_FJ", null);
        FOR_RJ = new AnalyseNode(AnalyseNode.ACTIONSIGN, "@FOR_RJ", null);
        FOR_BACKPATCH_FJ = new AnalyseNode(AnalyseNode.ACTIONSIGN,"@FOR_BACKPATCH_FJ", null);
        
        // 跳转的序号
        if_fj = new Stack<Integer>();
        if_rj = new Stack<Integer>();
        while_fj = new Stack<Integer>();
        while_rj = new Stack<Integer>();
        for_fj = new Stack<Integer>();
        for_rj = new Stack<Integer>();
 
    }
    
    public void grammerAnalyse()// LL1分析方法进行语法分析         最左推导
    {
        if (lexAnalyse.isFail())// 判断词法分析是否通过，通过才能进入语法分析
            javax.swing.JOptionPane.showMessageDialog(null, "词法分析未通过，不能进行语法分析");
        bf = new StringBuffer();// 申请缓冲区
        int gcount = 0;// 分析步骤的序号
        error = null;// 错误数
        analyseStack.add(0, S);// 分析桟开始是文法开始符号
        analyseStack.add(1, new AnalyseNode(AnalyseNode.END, "#", null));// 结束符#
        semanticStack.add("#");// 语义桟初始为#
        while (!analyseStack.empty() && !wordList.isEmpty())// 符号表不为空语法分析桟不为空
        {
            bf.append('\n');// 在字符串结尾添加
            bf.append("步骤" + gcount + "\t");// 步骤序号
            if (gcount++ > 10000)
            {
                graErrorFlag = true;
                break;
            }
            
            top = analyseStack.get(0);// 当前栈顶元素S
            firstWord = wordList.get(0);// 第一个待分析单词
            if (firstWord.value.equals("#") && top.name.equals("#"))// 单词的值是#同时语法栈顶的值也为#则正常结束
            {
                bf.append("\n");//换行
                analyseStack.remove(0);// 语法分析桟出栈
                wordList.remove(0);// wordlist表移除当前单词
                
            } else if (top.name.equals("#"))//语法分析桟栈顶的值是#
            {
                analyseStack.remove(0);//分析桟出栈
                graErrorFlag = true;//出现错误
                break;
                
            } else if (AnalyseNode.isTerm(top))// 判断是终结符时的处理
            {
                termOP(top.name);//调用终结符分析函数
            } else if (AnalyseNode.isNonterm(top))// 判断非终结符
            {
                nonTermOP(top.name);//调用非终结符分析函数
            } else if (top.type.equals(AnalyseNode.ACTIONSIGN))// 栈顶是动作符号时的处理
            {
                actionSignOP();//调用动作符号分析函数
            }
            
            bf.append("当前分析栈：");
            for (int i =analyseStack.size()-1; i>=0; i--)//获取桟内的单词
            {
                bf.append(analyseStack.get(i).name);
            }
            bf.append("\t").append("\t").append("余留符号串：");
            for (int j = 0; j < wordList.size(); j++)//获取单词表剩余的单词
            {
                bf.append(wordList.get(j).value);
            }
            bf.append("\t").append("\t").append("语义栈：");
            for (int k = semanticStack.size() - 1; k >= 0; k--)//获取语义桟的内容
            {
                bf.append(semanticStack.get(k));
            }
        }
    }
    
    private void termOP(String term)// 当前栈顶符号是终结符时的处理
    {
        if ((firstWord.type.equals(Word.INT_CONST) || firstWord.type
                .equals(Word.CHAR_CONST))
                || (firstWord.type.equals(Word.OPERATOR) && term
                        .equals(firstWord.value))
                || (firstWord.type.equals(Word.BOUNDARYSIGN) && term
                        .equals(firstWord.value))
                || (firstWord.type.equals(Word.KEY) && term
                        .equals(firstWord.value))
                || (term.equals("id") && firstWord.type.equals(Word.IDENTIFIER))
                || (term.equals("\"%d\"") && firstWord.type
                        .equals(Word.IDENTIFIER)))//判断终结符的类型
        {
            System.out.println("name:" + term + "   " + "succeful");
            analyseStack.remove(0);
            wordList.remove(0);
        } else
        {
            errorCount++;
            analyseStack.remove(0);
            wordList.remove(0);
            error = new Error(errorCount, "语法错误", firstWord.line, firstWord);//新建错误对象
            System.out.println("name:" + term + "   " + "defeat" + "  "
                    + errorCount);
            errorList.add(error);
            graErrorFlag = true;
        }
    }
    
    private void nonTermOP(String nonTerm)
    {
        if (nonTerm.equals("Z'"))//将Z1改为1
            nonTerm = "1";
        if (nonTerm.equals("U'"))
            nonTerm = "2";
        if (nonTerm.equals("E'"))
            nonTerm = "3";
        if (nonTerm.equals("H'"))
            nonTerm = "4";
        if (nonTerm.equals("L'"))
            nonTerm = "5";
        if (nonTerm.equals("T'"))
            nonTerm = "6";
        
        switch (nonTerm.charAt(0))
        // 栈顶为非终结符处理        
        {
        // N:S,B,A,C,,X,R,Z,Z1,U,U1,E,E1,H,H1,G,M,D,L,L1,T,T1,F,O,P,Q
            case 'S':// 文法开始 S->void main(){A}
                if (firstWord.value.equals("void"))// void为开始,将S出栈然后将void main (){A}进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"void", null));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"main", null));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,"(", null));
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL,")", null));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL,"{", null));
                    analyseStack.add(5, A);
                    analyseStack.add(6, new AnalyseNode(AnalyseNode.TERMINAL, "}", null));
                } 
                else if(firstWord.value.equals("int"))//S->int main(){A return 0;}
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"int", null));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"main", null));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,"(", null));
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL,")", null));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL,"{", null));
                    analyseStack.add(5, A);
                    analyseStack.add(6, new AnalyseNode(AnalyseNode.TERMINAL, "return", null));
                    analyseStack.add(7, new AnalyseNode(AnalyseNode.TERMINAL,"0", null));
                    analyseStack.add(8, new AnalyseNode(AnalyseNode.TERMINAL,";", null));
                    analyseStack.add(9, new AnalyseNode(AnalyseNode.TERMINAL,"}", null));
                }
                else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "主函数没有返回值", firstWord.line,firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
                
            case 'A':// 复合语句 A->CA
                if (firstWord.value.equals("int")
                        || firstWord.value.equals("char")
                        || firstWord.value.equals("bool"))// A->CA A出栈，CA进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    
                } else if (firstWord.value.equals("printf"))// A->CA A出栈，CA进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    
                } else if (firstWord.value.equals("scanf"))// A->CA A出栈，CA进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    
                } else if (firstWord.value.equals("if"))// A->CA A出栈，CA进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    
                } else if (firstWord.value.equals("while"))// A->CA A出栈，CA进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    
                } else if (firstWord.value.equals("for"))// A->CA A出栈，CA进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    
                } else if (firstWord.type.equals(Word.IDENTIFIER))// A->CA
                                                                  // A出栈，CA进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, C);
                    analyseStack.add(1, A);
                    
                } else
                {
                    analyseStack.remove(0);
                }
                break;
            
            case 'B':// 功能函数、控制语句
                if (firstWord.value.equals("printf"))// 输出语句 B->printf(P);将B出栈,printf("%d",F)@PRINTF A;进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"printf", null));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"(", null));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,"\"%d\"", null));// "%d"
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL,",", null));
                    analyseStack.add(4, F);
                    analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL,")", null));
                    analyseStack.add(6, PRINTF);
                    analyseStack.add(7, A);
                    analyseStack.add(8, new AnalyseNode(AnalyseNode.TERMINAL,";", null));
                }
                
                else if (firstWord.value.equals("scanf"))// 输入语句 B->scanf(id);将scanf("%d",&F)@SCANF A;进栈
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"scanf", null));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"(", null));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,"\"%d\"", null));
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL,",", null));
                    analyseStack.add(4, new AnalyseNode(AnalyseNode.TERMINAL,"&", null));
                    analyseStack.add(5, F);
                    analyseStack.add(6, new AnalyseNode(AnalyseNode.TERMINAL,")", null));
                    analyseStack.add(7, SCANF);//sacnf需要进行的动作
                    analyseStack.add(8, A);
                    analyseStack.add(9, new AnalyseNode(AnalyseNode.TERMINAL,";", null));
                    
                } else if (firstWord.value.equals("if"))// B->if (G){A}else{A}将B出栈，if(G)@IF_FJ{A}@IF_BACKPATCH_FJ@IF_RJelse{A}@IF_BACKPATCH_RJ
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"if", null));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"(", null));
                    analyseStack.add(2, G);//
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL,")", null));
                    analyseStack.add(4, IF_FJ);
                    analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL,"{", null));
                    analyseStack.add(6, A);
                    analyseStack.add(7, new AnalyseNode(AnalyseNode.TERMINAL,"}", null));
                    analyseStack.add(8, IF_BACKPATCH_FJ);
                    analyseStack.add(9, IF_RJ);
                    analyseStack.add(10, new AnalyseNode(AnalyseNode.TERMINAL,"else", null));
                    analyseStack.add(11, new AnalyseNode(AnalyseNode.TERMINAL,"{", null));
                    analyseStack.add(12, A);
                    analyseStack.add(13, new AnalyseNode(AnalyseNode.TERMINAL,"}", null));
                    analyseStack.add(14, IF_BACKPATCH_RJ);
                    
                } else if (firstWord.value.equals("while"))// B->while(G){A}
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,
                            "while", null));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,
                            "(", null));
                    analyseStack.add(2, G);
                    analyseStack.add(3, new AnalyseNode(AnalyseNode.TERMINAL,
                            ")", null));
                    analyseStack.add(4, WHILE_FJ);
                    analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL,
                            "{", null));
                    analyseStack.add(6, A);
                    analyseStack.add(7, new AnalyseNode(AnalyseNode.TERMINAL,
                            "}", null));
                    analyseStack.add(8, WHILE_RJ);
                    analyseStack.add(9, WHILE_BACKPATCH_FJ);
                    
                } else if (firstWord.value.equals("for"))// B->for(YZ;G;Q){A}
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"for", null));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"(", null));
                    analyseStack.add(2, R);
                    analyseStack.add(3, G);
                    analyseStack.add(4, FOR_FJ);
                    analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL,";", null));
                    analyseStack.add(6, Q);
                    analyseStack.add(7, new AnalyseNode(AnalyseNode.TERMINAL,")", null));
                    analyseStack.add(8, new AnalyseNode(AnalyseNode.TERMINAL,"{", null));
                    analyseStack.add(9, A);
                    analyseStack.add(10, SINGLE);
                    analyseStack.add(11, new AnalyseNode(AnalyseNode.TERMINAL,"}", null));
                    analyseStack.add(12, FOR_RJ);
                    analyseStack.add(13, FOR_BACKPATCH_FJ);
                } else
                {
                    analyseStack.remove(0);
                }
                break;
            case 'C':// C->X|B|R
                //修改处-zym
                if(firstWord.value.equals("int")
                        || firstWord.value.equals("char")
                        || firstWord.value.equals("bool"))//C->X
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, X);
                }
                else if(firstWord.type.equals(Word.IDENTIFIER))//C->B
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, R);
                }
                else if(firstWord.value.equals("if")
                        || firstWord.value.equals("scanf")
                        || firstWord.value.equals("printf")
                        ||firstWord.value.equals("while")
                        || firstWord.value.equals("for"))//C->R
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, B);
                }               
                break;
                
            case 'X':// 声明语句
                if (firstWord.value.equals("int")
                        || firstWord.value.equals("char")
                        || firstWord.value.equals("bool"))// X->YZ;
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, Y);
                    analyseStack.add(1, Z);
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,
                            ";", null));
                } else
                {
                    analyseStack.remove(0);
                }
                break;
                
            case 'Y':// Y->int|char|bool
                if (firstWord.value.equals("int"))//Y->int
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"int", null));
                } else if (firstWord.value.equals("char"))//Y->char
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"char", null));
                } else if (firstWord.value.equals("bool"))//Y->bool
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"bool", null));
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "非法数据类型", firstWord.line,firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case 'Z':// Z->UZ'
                if (firstWord.type.equals(Word.IDENTIFIER))//下一个字符（当前单词表中的第一个字符）是标识符
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, U);
                    analyseStack.add(1, Z1);
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "非法标识符", firstWord.line,firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '1':// Z' Z'->,Z|$
                if (firstWord.value.equals(","))//下一个字符是,
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,",", null));
                    analyseStack.add(1, Z);
                } else
                {
                    analyseStack.remove(0);
                }
                break;
            case 'U':// U->idU'
                if (firstWord.type.equals(Word.IDENTIFIER))//标识符
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, ASS_U);//将标识符放进语义桟
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"id", null));
                    analyseStack.add(2, U1);
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "非法标识符", firstWord.line,firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '2':// U' U'->=L|$
                if (firstWord.value.equals("="))// U'->=L
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"=", null));
                    analyseStack.add(1, L);
                    analyseStack.add(2, EQ_U1);
                } else
                // $
                {
                    analyseStack.remove(0);
                }
                break;
            case 'R':// R->id=L;|R->Q;
                if (firstWord.type.equals(Word.IDENTIFIER))
                {
                    SecondWord=wordList.get(1);
                    if(SecondWord.value.equals("++"))
                    {
                        analyseStack.remove(0);
                        analyseStack.add(0,Q);
                        analyseStack.add(1, SINGLE);
                        analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,";", null));
                        
                    }
                    else
                    {
                        analyseStack.remove(0);
                        analyseStack.add(0, new AnalyseNode(AnalyseNode.ACTIONSIGN,"@ASS_R", null));
                        analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"id", null));
                        analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,"=", null));
                        analyseStack.add(3, L);
                        analyseStack.add(4, EQ);
                        analyseStack.add(5, new AnalyseNode(AnalyseNode.TERMINAL,";", null)); 
                    }
                    
                } else
                {
                    analyseStack.remove(0);
                }
                break;
            case 'P':// P->id|ch|num
                if (firstWord.type.equals(Word.IDENTIFIER))// p->id
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"id", null));
                } else if (firstWord.type.equals(Word.INT_CONST))// p->int
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"num", null));
                } else if (firstWord.type.equals(Word.CHAR_CONST))// p->char
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"ch", null));
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "不能输出的数据类型", firstWord.line,firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case 'E':// E->HE'
                if (firstWord.type.equals(Word.IDENTIFIER))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, H);
                    analyseStack.add(1, E1);
                } else if (firstWord.type.equals(Word.INT_CONST))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, H);
                    analyseStack.add(1, E1);
                } else if (firstWord.value.equals("("))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, H);
                    analyseStack.add(1, E1);
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "不能进行算术运算的数据类型",firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '3':// E' E'->&&E|$
                if (firstWord.value.equals("&&"))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"&&", null));
                    analyseStack.add(1, E);
                } else
                {
                    analyseStack.remove(0);
                }
                break;
            case 'H':// H->GH'
                if (firstWord.type.equals(Word.IDENTIFIER))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, G);
                    analyseStack.add(1, H1);
                } else if (firstWord.type.equals(Word.INT_CONST))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, G);
                    analyseStack.add(1, H1);
                } else if (firstWord.value.equals("("))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, G);
                    analyseStack.add(1, H1);
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "不能进行算术运算的数据类型",firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '4':// H'
                if (firstWord.value.equals("||"))// H'->||H
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"||", null));
                    analyseStack.add(1, E);
                } else
                // H'->$
                {
                    analyseStack.remove(0);
                }
                break;
            case 'D':// D-><|>|==|!=|>=|<=
                if (firstWord.value.equals("=="))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"==", null));
                } else if (firstWord.value.equals("!="))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"!=", null));
                    
                } else if (firstWord.value.equals(">"))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,">", null));
                } else if (firstWord.value.equals("<"))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"<", null));
                } else if (firstWord.value.equals(">="))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,">=", null));
                } else if (firstWord.value.equals("<="))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, COMPARE_OP);
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"<=", null));
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "非法运算符", firstWord.line,firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case 'G'://
                if (firstWord.type.equals(Word.IDENTIFIER))// G->FDF
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, D);
                    analyseStack.add(2, F);
                    analyseStack.add(3, COMPARE);
                } else if (firstWord.type.equals(Word.INT_CONST))// G->FDF
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, D);
                    analyseStack.add(2, F);
                    analyseStack.add(3, COMPARE);
                } else if (firstWord.value.equals("("))// G->(E)
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"(", null));
                    analyseStack.add(1, E);
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,")", null));
                } else if (firstWord.value.equals("!"))// G->!E
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"!", null));
                    analyseStack.add(1, E);
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "不能进行算术运算的数据类型或括号不匹配",firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case 'L':// 算术运算语句L->TL'
                if (firstWord.type.equals(Word.IDENTIFIER))// L->TL'
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, T);
                    analyseStack.add(1, L1);
                    analyseStack.add(2, ADD_SUB);
                } else if (firstWord.type.equals(Word.INT_CONST))// L->TL'
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, T);
                    analyseStack.add(1, L1);
                    analyseStack.add(2, ADD_SUB);
                } else if (firstWord.value.equals("("))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, T);
                    analyseStack.add(1, L1);
                    analyseStack.add(2, ADD_SUB);
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "不能进行算术运算的数据类型或括号不匹配",firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '5':// L' L'->+L|-L|$
                if (firstWord.value.equals("+"))//L'->+L
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"+", null));
                    analyseStack.add(1, L);
                    analyseStack.add(2, ADD);//动作符号
                } else if (firstWord.value.equals("-"))//L'->-L
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"-", null));
                    analyseStack.add(1, L);
                    analyseStack.add(2, SUB);
                } else
                {
                    analyseStack.remove(0);
                }
                break;
            
            case 'T':// T->FT'
                if (firstWord.type.equals(Word.IDENTIFIER)) //由文法中F推出的符号F->id
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, T1);
                    analyseStack.add(2, DIV_MUL);
                } else if (firstWord.type.equals(Word.INT_CONST))//F->num
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, T1);
                    analyseStack.add(2, DIV_MUL);
                } else if (firstWord.value.equals("("))//F->(L)
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, F);
                    analyseStack.add(1, T1);
                    analyseStack.add(2, DIV_MUL);
                } else
                {
                    errorCount++;
                    analyseStack.remove(0);
                    wordList.remove(0);
                    error = new Error(errorCount, "不能进行算术运算的数据类型",firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
                break;
            case '6':// T' T'->*T|/T|$
                if (firstWord.value.equals("*"))//T'->*T
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"*", null));
                    analyseStack.add(1, T);
                    analyseStack.add(2, MUL);
                } else if (firstWord.value.equals("/"))//T'->/T
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"/", null));
                    analyseStack.add(1, T);
                    analyseStack.add(2, DIV);
                } else//T->$
                {
                    analyseStack.remove(0);
                }
                break;
            case 'F'://F->(L)|id|num
                if (firstWord.type.equals(Word.IDENTIFIER))// 标识符
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, ASS_F);// 赋值语句 F->id
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"id", null));
                } else if (firstWord.type.equals(Word.INT_CONST))
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, ASS_F);// 赋值语句 F->num
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"num", null));
                } else if (firstWord.value.equals("("))// F->(L)
                {
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.TERMINAL,"(", null));
                    analyseStack.add(1, L);
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,")", null));
                    analyseStack.add(3, TRAN_LF);
                } else
                {
                    analyseStack.remove(0);
                }
                break;
            case 'O':// O->++|--|$
                if (firstWord.value.equals("++"))//O->++
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.ACTIONSIGN,"@SINGLE_OP", null));//单目运算
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"++", null));
                } else if (firstWord.value.equals("--"))//O->--
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.ACTIONSIGN,"@SINGLE_OP", null));
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"--", null));
                } else//O->$
                {
                    analyseStack.remove(0);
                }
                break;
            case 'Q':// Q Q->idO|$
                if (firstWord.type.equals(Word.IDENTIFIER))//Q->idO
                {
                    analyseStack.remove(0);
                    analyseStack.add(0, new AnalyseNode(AnalyseNode.ACTIONSIGN,"@ASS_Q", null));//赋值动作
                    analyseStack.add(1, new AnalyseNode(AnalyseNode.TERMINAL,"id", null));
                    analyseStack.add(2, new AnalyseNode(AnalyseNode.TERMINAL,"O", null));
                } else//Q->$
                {
                    analyseStack.remove(0);
                }
                break;
        
        }
    }
    
    
    //语义子程序
    private void actionSignOP()// 当前栈顶符号是动作符号的处理情况
    {
        //算术运算的动作符号
        if (top.name.equals("@ADD_SUB"))// 加减法 T->TL'
        {
            if (OP != null && (OP.equals("+") || OP.equals("-")))//判断其运算符
            {
                ARG2 = semanticStack.pop();//操作数进语义桟
                ARG1 = semanticStack.pop();
                RES = newTemp();//生成临时地址
                FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP,ARG1, ARG2, RES);//创建四元式
                fourElemList.add(fourElem);//加到四元式集合
                L.value = RES;//运算结果赋给L
                semanticStack.push(L.value);//将L的值放进语义桟
                OP = null;
            }
            analyseStack.remove(0);//语法桟出栈
            
        } 
        else if (top.name.equals("@ADD"))// 加法L'->+L
        {
            OP = "+";//令运算符为加
            analyseStack.remove(0);//语法桟出栈
        } 
        else if (top.name.equals("@SUB"))// 减法L'->-L
        {
            OP = "-";
            analyseStack.remove(0);
        } 
        else if (top.name.equals("@DIV_MUL"))// 乘除法 T->FT'
        {
            if (OP != null && (OP.equals("*") || OP.equals("/")))
            {
                ARG2 = semanticStack.pop();
                ARG1 = semanticStack.pop();
                RES = newTemp();
                FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP,ARG1, ARG2, RES);
                fourElemList.add(fourElem);
                T.value = RES;
                semanticStack.push(T.value);
                OP = null;
            }
            analyseStack.remove(0);
        } 
        else if (top.name.equals("@DIV"))// 除法T'->/T
        {
            OP = "/";
            analyseStack.remove(0);
        } 
        else if (top.name.equals("@MUL"))// 乘法T'->*T
        {
            OP = "*";
            analyseStack.remove(0);
        } 
        else if (top.name.equals("@TRAN_LF"))// F->(L);
        {
            F.value = L.value;
            analyseStack.remove(0);
            
        }         
        else if (top.name.equals("@ASS_F"))// F->num|id  F=wordlist表中的变量标识符或常量 
        {
            F.value = firstWord.value;
            if (!LexAnalyse.getTypelist().contains(F.value)
                    && (F.value.charAt(0) > 64))//判断其标识符是否已经定义或者是否是常量
            {
                error = new Error(errorCount, "没有定义      ", firstWord.line,firstWord);
                errorList.add(error);
                graErrorFlag = true;
            };
            semanticStack.push(F.value);
            analyseStack.remove(0);
            
        }
        
        //赋值动作符
        else if (top.name.equals("@ASS_R"))// R->id=L  R=wordlist表当前字符中的值
        {
            R.value = firstWord.value;
            if (!LexAnalyse.getTypelist().contains(R.value)
                    && (R.value.charAt(0) > 64))
            {
                error = new Error(errorCount, "没有定义      ", firstWord.line,firstWord);
                errorList.add(error);
                graErrorFlag = true;
            }
            ;
            semanticStack.push(R.value);
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@ASS_Q"))// Q->idO   Q=wordlist表中的值
        {
            Q.value = firstWord.value;
            semanticStack.push(Q.value);
            analyseStack.remove(0);
            
        } 
        
        //声明动作符
        else if (top.name.equals("@ASS_U"))// U->idU'  U=wordlist表中的值
        {
            U.value = firstWord.value;
            for (String x : semanticStack)//循环语义桟
            {
                if (U.value.equals(x))
                {
                    error = new Error(errorCount, "重复定义      " + x,firstWord.line, firstWord);
                    errorList.add(error);
                    graErrorFlag = true;
                }
            }
            semanticStack.push(U.value);//放进语义桟
            analyseStack.remove(0);
            
        } 
        
        else if (top.name.equals("@SINGLE"))// 在for循环中的单目运算符++ --
        {
            if (for_op.peek() != null)
            {
                ARG1 = semanticStack.pop();
                RES = ARG1;
                FourElement fourElem = new FourElement(++fourElemCount,OUTPORT,for_op.pop(), ARG1, "/", RES);
                fourElemList.add(fourElem);
            }
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@SINGLE_OP"))//for括号中的++/--的标识符
        {
            
            for_op.push(firstWord.value);//将字符的值放进for_op桟
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@EQ"))// R->id=L '='符号即赋值语句
        {
            OP = "=";
            ARG1 = semanticStack.pop();
            RES = semanticStack.pop();
            ;
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, ARG1,"/", RES);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@EQ_U'"))//声明文法中的赋值语句U'->=L
        {
            OP = "=";
            ARG1 = semanticStack.pop();
            RES = semanticStack.pop();
            ;
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, ARG1,"/", RES);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@COMPARE"))// 比较语句G->FDF 两个数比较
        {
            ARG2 = semanticStack.pop();
            OP = semanticStack.pop();
            ARG1 = semanticStack.pop();
            RES = newTemp();
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, ARG1,ARG2, RES);
            fourElemList.add(fourElem);
            G.value = RES;
            semanticStack.push(G.value);
            OP = null;
            analyseStack.remove(0);
        } 
        else if (top.name.equals("@COMPARE_OP"))// D-><|>|==|!=  比较操作符
        {
            D.value = firstWord.value;
            semanticStack.push(D.value);
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@IF_FJ"))// if语句错误跳转
        {
            OP = "FJ";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, RES,ARG1, "/");
            if_fj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } 
        else if (top.name.equals("@SCANF"))// 输入语句
        {
            OP = "SCANF";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, ARG1,"/", "/");
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@PRINTF"))// 输出语句
        {
            OP = "PRINTF";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, ARG1,"/", "/");
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@IF_BACKPATCH_FJ"))// if错误回填
        {
            backpatch(if_fj.pop(), fourElemCount + 2);
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@IF_RJ"))// if rj 无条件跳转
        {
            OP = "RJ";
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, "/","/", "/");
            if_rj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@IF_BACKPATCH_RJ"))// if无条件回填
        {
            backpatch(if_rj.pop(), fourElemCount + 1);
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@WHILE_FJ"))// while错误跳转
        {
            OP = "FJ";
            ARG1 = semanticStack.pop();
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, "/",ARG1, "/");
            while_fj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } 
        else if (top.name.equals("@WHILE_RJ"))// while 无条件跳转
        {
            OP = "RJ";
            RES = (while_fj.peek() - 1) + "";
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, RES,"/", "/");
            for_rj.push(fourElemCount);
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@WHILE_BACKPATCH_FJ"))// while错误回填
        {
            backpatch(while_fj.pop(), fourElemCount + 1);
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@FOR_FJ"))// for fj错误跳转
        {
            OP = "FJ";
            ARG1 = semanticStack.pop();//从语义桟中取出操作数
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, "/",ARG1, "/");
            for_fj.push(fourElemCount);//将当前四元式序号放进跳转地址桟
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
        } 
        else if (top.name.equals("@FOR_RJ"))// for Rj无条件跳转
        {
            OP = "RJ";
            RES = (for_fj.peek() - 1) + "";//从for_fj跳转地址桟中读取字符指针并不移动
            FourElement fourElem = new FourElement(++fourElemCount,OUTPORT, OP, RES,"/", "/");
            for_rj.push(fourElemCount);//压桟
            fourElemList.add(fourElem);
            OP = null;
            analyseStack.remove(0);
            
        } 
        else if (top.name.equals("@FOR_BACKPATCH_FJ"))// for错误回填
        {
            backpatch(for_fj.pop(), fourElemCount + 1);//从桟中取出回填序号
            analyseStack.remove(0);
        }
        
    }
    
    private void backpatch(int i, int res)// 回填函数，回填第i个四元式的跳转地址
    {
        FourElement temp = fourElemList.get(i - 1);
        temp.arg1 = res + "";
        fourElemList.set(i - 1, temp);
    }
    
    
    public String outputLL1() throws IOException// 语法分析输出到文件中
    {
        File file = new File("./result/");// 创建新的文件
        if (!file.exists())
        {
            file.mkdirs();
            file.createNewFile();// 如果这个文件不存在就创建它
        }
        String path = file.getAbsolutePath();
        FileOutputStream fos = new FileOutputStream(path + "/LL1.txt");// 文件地址加文件名
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        OutputStreamWriter osw1 = new OutputStreamWriter(bos, "utf-8");// 文件编码
        PrintWriter pw1 = new PrintWriter(osw1);
        pw1.println(bf.toString());
        bf.delete(0, bf.length());
        if (graErrorFlag)
        {
            Error error;
            pw1.println("错误信息如下：");
            
            pw1.println("错误序号\t错误信息\t错误所在行 \t错误单词");
            for (int i = 0; i < errorList.size(); i++)
            {
                error = errorList.get(i);
                pw1.println(error.id + "\t" + error.info + "\t\t" + error.line+ "\t" + error.word.value);
            }
        } else
        {
            pw1.println("语法分析通过：");
        }
        pw1.close();
        return path + "/LL1.txt";
    }
    
    public String outputFourElem() throws IOException// 语义分析输出到文件
    {
        
        File file = new File("./result/");
        if (!file.exists())
        {
            file.mkdirs();
            file.createNewFile();// 如果这个文件不存在就创建它
        }
        String path = file.getAbsolutePath();
        FileOutputStream fos = new FileOutputStream(path + "/FourElement.txt");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        OutputStreamWriter osw1 = new OutputStreamWriter(bos, "utf-8");
        PrintWriter pw1 = new PrintWriter(osw1);
        pw1.println("生成的四元式如下");
        pw1.println("序号（OP,ARG1，ARG2，RESULT）");
        FourElement temp;
        for (int i = 0; i < fourElemList.size(); i++)
        {
            temp = fourElemList.get(i);
            pw1.println(temp.id + "(" + temp.op + "," + temp.arg1 + ","
                    + temp.arg2 + "," + temp.result + ")");
        }
        pw1.close();
        
        return path + "/FourElement.txt";
    }
    
    public static void main(String[] args)
    {
        
    }
    
}
