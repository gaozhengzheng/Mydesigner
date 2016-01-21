package com.compiler;

import java.util.ArrayList;

/**
 * ����ջ�ڵ���
 * 
 * @author KB String type;//�ڵ����� String name;//�ڵ��� Object value;//�ڵ�ֵ
 */
public class AnalyseNode
{
    public final static String NONTERMINAL = "���ս��";
    public final static String TERMINAL    = "�ս��";
    public final static String ACTIONSIGN  = "������";
    public final static String END         = "������";
    static ArrayList<String>   nonterminal = new ArrayList<String>(); // ���ս������
    static ArrayList<String>   actionSign  = new ArrayList<String>(); // ����������
    static
    {
        // S A��B C D E F G H L M O P Q X Y Z R T U
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
        
        actionSign.add("@ADD_SUB");// �Ӽ���
        actionSign.add("@ADD");// �ӷ�
        actionSign.add("@SUB");// ����
        actionSign.add("@DIV_MUL");// �˳�
        actionSign.add("@DIV");// ����
        actionSign.add("@MUL");// �˷�
        
        actionSign.add("@SINGLE");// ��forѭ���еĵ�Ŀ�����++ --
        actionSign.add("@SINGTLE_OP");// 
        
        actionSign.add("@ASS_R");// ��ֵ���
        actionSign.add("@ASS_Q");
        actionSign.add("@ASS_F");
        actionSign.add("@ASS_U");
        
        actionSign.add("@TRAN_LF");
        
        actionSign.add("@EQ");// ����
        actionSign.add("@EQ_U'");
        
        actionSign.add("@COMPARE");// �Ƚ�
        actionSign.add("@COMPARE_OP");// �ȽϽ���Ĳ�����
        
        actionSign.add("@IF_RJ");// if rj ��������ת
        actionSign.add("@IF_FJ");// ������ת
        actionSign.add("@IF_BACKPATCH_FJ");// if�������
        actionSign.add("@IF_BACKPATCH_RJ");// if����������
        
        actionSign.add("@WHILE_FJ");// while������ת
        actionSign.add("@WHILE_BACKPATCH_FJ");// while�������
        
        actionSign.add("@FOR_FJ");// for fj������ת
        actionSign.add("@FOR_RJ");// for Rj��������ת
        actionSign.add("@FOR_BACKPATCH_FJ");// for�������
    }
    
    String  type;  // �ڵ�����
    String  name;  // �ڵ���
    String  value;  // �ڵ�ֵ
                                                                      
    public static boolean isNonterm(AnalyseNode node)//�ж�һ�������C�ڵ��Ƿ�Ϊ���ս��
    {
        return nonterminal.contains(node.name);
    }
    
    public static boolean isTerm(AnalyseNode node)//�ж�һ�������C�ڵ��Ƿ�Ϊ�ս��
    {
        return Word.isKey(node.name) || Word.isOperator(node.name)
                || Word.isBoundarySign(node.name) || node.name.equals("id")
                || node.name.equals("num") || node.name.equals("ch")
                || node.name.equals("\"%d\"") || node.name.equals("&")||node.name.equals("0");//�޸Ĵ�
    }
    
    public static boolean isActionSign(AnalyseNode node)//�ж�һ�������C�ڵ��Ƿ�Ϊ��������
    {
        return actionSign.contains(node.name);
    }
    
    public AnalyseNode()
    {
        
    }
    
    public AnalyseNode(String type, String name, String value)//�ڵ�ĸ�ֵ
    {
        this.type = type;
        this.name = name;
        this.value = value;
    }//���캯��
    
}
