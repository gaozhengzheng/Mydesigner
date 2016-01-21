package com.compiler;

import java.util.ArrayList;

/**
 * 分析栈节点类
 * 
 * @author KB String type;//节点类型 String name;//节点名 Object value;//节点值
 */
public class AnalyseNode
{
    public final static String NONTERMINAL = "非终结符";
    public final static String TERMINAL    = "终结符";
    public final static String ACTIONSIGN  = "动作符";
    public final static String END         = "结束符";
    static ArrayList<String>   nonterminal = new ArrayList<String>(); // 非终结符集合
    static ArrayList<String>   actionSign  = new ArrayList<String>(); // 动作符集合
    static
    {
        // S A　B C D E F G H L M O P Q X Y Z R T U
        nonterminal.add("S");
        nonterminal.add("A");
        nonterminal.add("B");
        nonterminal.add("C");
        nonterminal.add("D");
        nonterminal.add("E");
        nonterminal.add("F");
        nonterminal.add("G");
        nonterminal.add("H");
        nonterminal.add("L");
        nonterminal.add("M");
        nonterminal.add("O");
        nonterminal.add("P");
        nonterminal.add("Q");
        nonterminal.add("X");
        nonterminal.add("Y");
        nonterminal.add("Z");
        nonterminal.add("R");
        nonterminal.add("T");
        nonterminal.add("U");
        // Z' U' E' H' L' T'
        nonterminal.add("Z'");
        nonterminal.add("U'");
        nonterminal.add("E'");
        nonterminal.add("H'");
        nonterminal.add("L'");
        nonterminal.add("T'");
        
        actionSign.add("@ADD_SUB");// 加减法
        actionSign.add("@ADD");// 加法
        actionSign.add("@SUB");// 减法
        actionSign.add("@DIV_MUL");// 乘除
        actionSign.add("@DIV");// 除法
        actionSign.add("@MUL");// 乘法
        
        actionSign.add("@SINGLE");// 在for循环中的单目运算符++ --
        actionSign.add("@SINGTLE_OP");// 
        
        actionSign.add("@ASS_R");// 赋值语句
        actionSign.add("@ASS_Q");
        actionSign.add("@ASS_F");
        actionSign.add("@ASS_U");
        
        actionSign.add("@TRAN_LF");
        
        actionSign.add("@EQ");// 等于
        actionSign.add("@EQ_U'");
        
        actionSign.add("@COMPARE");// 比较
        actionSign.add("@COMPARE_OP");// 比较结果的操作符
        
        actionSign.add("@IF_RJ");// if rj 无条件跳转
        actionSign.add("@IF_FJ");// 错误跳转
        actionSign.add("@IF_BACKPATCH_FJ");// if错误回填
        actionSign.add("@IF_BACKPATCH_RJ");// if无条件回填
        
        actionSign.add("@WHILE_FJ");// while错误跳转
        actionSign.add("@WHILE_BACKPATCH_FJ");// while错误回填
        
        actionSign.add("@FOR_FJ");// for fj错误跳转
        actionSign.add("@FOR_RJ");// for Rj无条件跳转
        actionSign.add("@FOR_BACKPATCH_FJ");// for错误回填
    }
    
    String  type;  // 节点类型
    String  name;  // 节点名
    String  value;  // 节点值
                                                                      
    public static boolean isNonterm(AnalyseNode node)//判断一个分析桟节点是否为非终结符
    {
        return nonterminal.contains(node.name);
    }
    
    public static boolean isTerm(AnalyseNode node)//判断一个分析桟节点是否为终结符
    {
        return Word.isKey(node.name) || Word.isOperator(node.name)
                || Word.isBoundarySign(node.name) || node.name.equals("id")
                || node.name.equals("num") || node.name.equals("ch")
                || node.name.equals("\"%d\"") || node.name.equals("&")||node.name.equals("0");//修改处
    }
    
    public static boolean isActionSign(AnalyseNode node)//判断一个分析桟节点是否为动作符号
    {
        return actionSign.contains(node.name);
    }
    
    public AnalyseNode()
    {
        
    }
    
    public AnalyseNode(String type, String name, String value)//节点的赋值
    {
        this.type = type;
        this.name = name;
        this.value = value;
    }//构造函数
    
}
