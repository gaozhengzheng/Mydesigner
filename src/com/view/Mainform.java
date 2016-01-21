package com.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import com.compiler.*;

public class Mainform extends JFrame
{
    /**
     * @param args
     */
    TextArea sourseFile;//用来显示源文件的文本框
    String soursePath;// 源文件路径
    String LL1Path;
    String wordListPath;
    String fourElementPath;
    LexAnalyse lexAnalyse;
    Parser parser;
    
    JSplitPane  jSplitPane1;
    JScrollPane jScrollPane;
    String      a[];
    JList       row;
    public Mainform() {
        this.init();
    }

    public void init() {

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        setTitle("C语言编译器");
        setSize(750, 480);
        super.setResizable(true);
        super.setLocation(screen.width / 2 - this.getWidth() / 2, screen.height
                / 2 - this.getHeight() / 2);
        this.setContentPane(this.createContentPane());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JPanel createContentPane() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(BorderLayout.NORTH, createUpPane());
        p.add(BorderLayout.CENTER, createcCenterPane());
        p.add(BorderLayout.SOUTH, creatBottomPane());
        return p;
    }

    private Component createUpPane() {
        JPanel p = new JPanel(new FlowLayout());
        final FilePanel fp = new FilePanel("选择待分析文件");
        JButton button = new JButton("确定");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String text;
                try {
                    soursePath = fp.getFileName();
                    text = readFile(soursePath);
                    sourseFile.setText(text);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        p.add(fp);
        p.add(button);
        return p;
    }

    private Component createcCenterPane() { 
        jSplitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,true);
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel("  源文件：");
        sourseFile = new TextArea();
        
        Font mf = new Font("宋体",Font.PLAIN,15);
        sourseFile.setFont(mf);
        a=new String[50];
        for(int i=0;i<50;i++){
            a[i]=String.valueOf(i+1);
        }
        row=new JList(a);
        row.setForeground(Color.red);
        
        jScrollPane=new JScrollPane(sourseFile);
        mf = new Font("宋体",Font.PLAIN,14);
        row.setFont(mf);
        jScrollPane.setRowHeaderView(row);
        sourseFile.setForeground(Color.BLACK);
        p.add( label,BorderLayout.NORTH);
        p.add(jScrollPane,BorderLayout.CENTER );
        
        jSplitPane1.add(p, JSplitPane.TOP);    
        jSplitPane1.setEnabled(true); 
        jSplitPane1.setOneTouchExpandable(true);
        
        jSplitPane1.setDividerSize(4);
        return jSplitPane1;
    }

    private Component creatBottomPane() {
        JPanel p = new JPanel(new FlowLayout());
        JButton bt1 = new JButton("词法分析");
        JButton bt2 = new JButton("语法分析");
        JButton bt3 = new JButton("四元式");
        JButton bt4 = new JButton("目标代码");
        bt1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    lexAnalyse=new LexAnalyse(sourseFile.getText());
                    wordListPath = lexAnalyse.outputWordList();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                InfoFrame inf = new InfoFrame("词法分析", wordListPath);

                inf.setVisible(true);
            }
        });
        bt2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            lexAnalyse=new LexAnalyse(sourseFile.getText());
            parser=new Parser(lexAnalyse);
                try {
                    parser.grammerAnalyse();
                    LL1Path= parser.outputLL1();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                InfoFrame inf = new InfoFrame("语法分析", LL1Path);
                inf.setVisible(true);
            }
        });
        
        bt3.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    lexAnalyse=new LexAnalyse(sourseFile.getText());
                    parser=new Parser(lexAnalyse);
                    parser.grammerAnalyse();
                    fourElementPath=parser.outputFourElem();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                InfoFrame inf = new InfoFrame("四元式", fourElementPath);
                inf.setVisible(true);
            }
        });
        bt4.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                try {
                    ArrayList<String> id = new ArrayList<String>();
                    lexAnalyse=new LexAnalyse(sourseFile.getText());                
                    parser=new Parser(lexAnalyse);
                    parser.grammerAnalyse();
                    Asm asm = new Asm(parser.fourElemList, parser.fourElemT);
                    asm.getAsmFile();
                    InfoFrame inf;
                    inf = new InfoFrame("目标代码",asm.getAsmFile());
                    inf.setVisible(true);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }                
            }
        });
        p.add(bt1);
        p.add(bt2);
        p.add(bt3);
        p.add(bt4);
        return p;
    }

    public static String readFile(String fileName) throws IOException {
        StringBuilder sbr = new StringBuilder();
        String str;
        FileInputStream fis = new FileInputStream(fileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
        BufferedReader in = new BufferedReader(isr);
        while ((str = in.readLine()) != null) {
            sbr.append(str).append('\n');
        }
        in.close();
        return sbr.toString();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Mainform mf = new Mainform();
        mf.setVisible(true);
    }
}

class FilePanel extends JPanel {
    FilePanel(String str) {
        JLabel label = new JLabel(str);
        JTextField fileText = new JTextField(35);
        JButton chooseButton = new JButton("浏览...");
        this.add(label);
        this.add(fileText);
        this.add(chooseButton);
        clickAction ca = new clickAction(this);
        chooseButton.addActionListener(ca);
    }

    public String getFileName() {
        JTextField jtf = (JTextField) this.getComponent(1);
        return jtf.getText();
    }

    // 按钮响应函数
    private class clickAction implements ActionListener {
        private Component cmpt;

        clickAction(Component c) {
            cmpt = c;
        }

        public void actionPerformed(ActionEvent event) {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            int ret = chooser.showOpenDialog(cmpt);
            if (ret == JFileChooser.APPROVE_OPTION) {
                JPanel jp = (JPanel) cmpt;
                JTextField jtf = (JTextField) jp.getComponent(1);
                jtf.setText(chooser.getSelectedFile().getPath());
            }
        }
    }
    
}
