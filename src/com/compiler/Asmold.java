package com.compiler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Asmold {
	private ArrayList<String> asmCodeList = new ArrayList<String>();// 保存汇编代码动态字符数组
	// 集合中为string类型
private File file = new File("./result/"); // 新建文件名为result
private ArrayList<FourElement> fourElemList; // 数组集合中为four element类型
private ArrayList<String> id;

/**
* @param fourElemList
*            //四元式
* @param id
* @param fourElemT
*/
//fourElemT存放临时变量名  fourElemList存放四元式对象
public Asmold(ArrayList<FourElement> fourElemList, ArrayList<String> id, ArrayList<String> fourElemT) {
this.fourElemList = fourElemList;
this.id = id;
asmHead(fourElemT);// 调用函数
asmCode(fourElemList);
asmTail();
for (int i = 0; i < asmCodeList.size(); i++)
System.out.println(asmCodeList.get(i));
}

/**
* 获取asm文件地址
* 
* @return
* @throws IOException
*/
public String getAsmFile() throws IOException {

File file = new File("./result/");
if (!file.exists()) {
file.mkdirs(); // 创建目录
file.createNewFile();// 如果这个文件不存在就创建它
}
String path = file.getAbsolutePath(); // 获取地址给path
FileOutputStream fos = new FileOutputStream(path + "/c_to_asm.asm");
BufferedOutputStream bos = new BufferedOutputStream(fos);
OutputStreamWriter osw1 = new OutputStreamWriter(bos, "utf-8");

PrintWriter pw1 = new PrintWriter(osw1); // 文本输出流打印对象

for (int i = 0; i < asmCodeList.size(); i++)
pw1.println(asmCodeList.get(i)); // 获取i行的元素

pw1.close();
return path + "/c_to_asm.asm";

}

/**
* 判断运算结束位置。
* 
* @param fourElemList
* @return
*/
public int endPosition(ArrayList<FourElement> fourElemList) {

int num = fourElemList.size();
int position;
for (int i = num - 1; i >= 0; i--) {

if (!fourElemList.get(i).op.equals("PRINTF")) { // 返回最后一个不是printf的四元式序号
return i;
}

}
return 0;

}

/**
* 汇编头部
* 
* @param fourElemT
*/
public void asmHead(ArrayList<String> fourElemT) {
// 添加数据段代码
asmCodeList.add("data segment");


for (int j = 0; j < fourElemT.size(); j++) {
asmCodeList.add(fourElemT.get(j) + " dw 0"); // 为临时变量T1/2...申请定义字数据区
}

for (int i = 0; i < fourElemList.size(); i++) { // 定义scanf 和printf变量
if (fourElemList.get(i).op.equals("PRINTF")) {

asmCodeList.add("printf " + fourElemList.get(i).arg1 + " " + (i + 1) + " db " + fourElemList.get(i).arg1
+ ":$'"); // (i+1)为行号

} else if (fourElemList.get(i).op.equals("SCANF")) {
asmCodeList.add("scanf " + fourElemList.get(i).arg1 + " " + (i + 1) + " db input "
+ fourElemList.get(i).arg1 + ":$'");
}
}
asmCodeList.add("data ends");
asmCodeList.add("stacks segment");
asmCodeList.add("temp db 10 dup  (0)"); // 在内存中开辟一段字节
asmCodeList.add("stacks ends");
asmCodeList.add("code segment");
asmCodeList.add("assume cs:code,ds:data");
asmCodeList.add("start:");
asmCodeList.add("MOV AX,data");
asmCodeList.add("MOV ds,AX");

}

/**
* 生成代码段代码
* 
* @param fourElemList
*/
public void asmCode(ArrayList<FourElement> fourElemList) {

int position = endPosition(fourElemList);// 获取运算的出口位置

for (int i = 0; i < fourElemList.size(); i++) {

int flag = 0;// 是否跳转到运算的出口位置TheEnd，结束运算。
if (fourElemList.get(i).arg1.equals(position + 2 + "")) { // 如果arg1指向最终四元式的下一个，结束

if (fourElemList.get(i).op.equals("FJ") || fourElemList.get(i).op.equals("RJ")) {
// 如果操作符是RJ/LJ
fourElemList.get(i).arg1 = "TheEnd";
flag = 1;
}
}

if (fourElemList.get(i).op.equals("=")) {
// 如果op是 =
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov AX," + fourElemList.get(i).arg1); //读
asmCodeList.add("mov "+fourElemList.get(i).result + ",AX"); //写
} else if (fourElemList.get(i).op.equals("+")) {
// 如果op是 +(有arg2)
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
asmCodeList.add("add AX, " + fourElemList.get(i).arg2);
asmCodeList.add("mov " + fourElemList.get(i).result + ", AX");
} else if (fourElemList.get(i).op.equals("++")) {
// 如果op是 ++(无arg2)
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
asmCodeList.add("add AX, 1"); // 就加1即可
asmCodeList.add("mov " + fourElemList.get(i).result + ", AX");
} else if (fourElemList.get(i).op.equals("-")) {
// 如果op是 -
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
asmCodeList.add("sub AX, " + fourElemList.get(i).arg2);
asmCodeList.add("mov " + fourElemList.get(i).result + ", AX");
} else if (fourElemList.get(i).op.equals("*")) {
// 如果op是 *
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov BX, " + fourElemList.get(i).arg1);
asmCodeList.add("mul BX," + fourElemList.get(i).arg2);
asmCodeList.add("mov " + fourElemList.get(i).result + ", BX");
} else if (fourElemList.get(i).op.equals("/")) {
// 如果op是 /
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
asmCodeList.add("div AX," + fourElemList.get(i).arg2);
asmCodeList.add("mov " + fourElemList.get(i).result + ", Ax");
}

else if (fourElemList.get(i).op.equals("FJ")) {
// 如果op是 FJ
if (flag == 1) {// 如果=1跳到TheEnd,完成运算 => jmp TheEnd
// jns:大于0跳转
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("jmp " + fourElemList.get(i).arg1);//jmp TheEnd
} else {
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("cmp " + fourElemList.get(i).arg2+",0");
asmCodeList.add("jns " + fourElemList.get(i).arg1);  //T...临时变量>0说明是假，跳转到arg1

}

} else if (fourElemList.get(i).op.equals("RJ")) {
// 如果op是 RJ
if (flag == 1) {// 如果=1跳到TheEnd,完成运算。=> jmp TheEnd
// jmp：无条件跳转
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add( "jmp " + fourElemList.get(i).arg1);
} else {
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add( "jmp L" + fourElemList.get(i).arg1);

}

} else if (fourElemList.get(i).op.equals("<")) {
// 如果op是 <
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
asmCodeList.add("sub AX, " + fourElemList.get(i).arg2);  //arg1-arg2
asmCodeList.add("mov " + fourElemList.get(i).result+",AX");
} else if (fourElemList.get(i).op.equals(">")) {
// 如果op是 >
// asmCodeList.add("L" + (i + 1) + ": mov AX, " +
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov AX,"+fourElemList.get(i).arg2);
asmCodeList.add("sub AX, " + fourElemList.get(i).arg1);//arg2-arg1
asmCodeList.add("mov " + fourElemList.get(i).result+",AX");


} else if (fourElemList.get(i).op.equals(">=")) {
// 如果op是 >=
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov AX, " + fourElemList.get(i).arg2);
asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
asmCodeList.add("sub AX,1");
asmCodeList.add("mov "+fourElemList.get(i).result+",AX");

} else if (fourElemList.get(i).op.equals("<=")) {
// 如果op是 <=
asmCodeList.add("L" + (i + 1) + ":" );
asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
asmCodeList.add("mov AX, " + fourElemList.get(i).arg2);
asmCodeList.add("sub AX,1");
asmCodeList.add("mov "+fourElemList.get(i).result+",AX");

}
else if (fourElemList.get(i).op.equals("PRINTF")) {
//op为PRINTF
asmCodeList.add("L" + (i + 1) + ":");
asmCodeList.add("lea dx,"+fourElemList.get(i).arg1);  //取偏移地址  （数据寄存器）
asmCodeList.add("mov ah,9  ;21h的9号功能输出");
asmCodeList.add("int 21h");
asmCodeList.add("mov ah,1  ;中断使程序暂停一下");
asmCodeList.add("int 21h");

asmCodeList.add(";输出回车 换行");
asmCodeList.add("mov ah,02h ;2号功能：字符输出");
asmCodeList.add("mov dl,0dh ;0dh即为回车符");
asmCodeList.add("int 21h ;此处输出回车");
asmCodeList.add("mov ah,02h");
asmCodeList.add("mov dl,0ah ;0ah即为换行符");
asmCodeList.add("int 21h ;此处输出换行");
}
//else if (fourElemList.get(i).op.equals("PRINTF")) {
//// 如果op是 printf
//asmCodeList.add("\n");
//asmCodeList.add(";PRINTF");
//asmCodeList.add("L" + (i + 1) + ":");
//asmCodeList.add("lea dx,printf_" + fourElemList.get(i).arg1 + (i + 1) + ";lea:取偏移地址");
//asmCodeList.add("mov ah,9;显示字符串");
//asmCodeList.add("int 21h");
//
//asmCodeList.add("mov ax," + fourElemList.get(i).arg1);
//asmCodeList.add("xor cx,cx;异或  cx清零");
//asmCodeList.add("mov bx,10");
//asmCodeList.add("PT0" + (i + 1) + ":xor dx,dx"); // ?????
//asmCodeList.add("div bx");
//asmCodeList.add("or dx,0e30h;0e:显示字符");
//asmCodeList.add("push dx");
//asmCodeList.add("inc cx;自增1");
//asmCodeList.add("cmp ax,0;ZF=1则AX=0,ZF=0则AX！=0");
//asmCodeList.add("jnz PT0" + (i + 1) + ";相等时跳转");
//asmCodeList.add("PT1" + (i + 1) + ":pop ax");
//asmCodeList.add("int 10h;显示一个字符");
//asmCodeList.add("loop PT1" + (i + 1));
//asmCodeList.add("mov ah,0 ");
//asmCodeList.add(";int 16h ;键盘中断");
//
//asmCodeList.add(";换行");
//asmCodeList.add("mov dl,0dh");
//asmCodeList.add("mov ah,2;显示输出");
//asmCodeList.add("int 21h");
//asmCodeList.add("mov dl,0ah");
//asmCodeList.add("mov ah,2");
//asmCodeList.add("int 21h");
//asmCodeList.add("\n");
//
//} 
//else if (fourElemList.get(i).op.equals("SCANF")) {
//// 如果op是 scanf
//asmCodeList.add("L" + (i + 1) + ":");
//asmCodeList.add(";SCANF");
//
//asmCodeList.add("lea dx,scanf_" + fourElemList.get(i).arg1 + (i + 1));
//asmCodeList.add("mov ah,9");
//asmCodeList.add("int 21h");
//
//asmCodeList.add(";输入中断");
//asmCodeList.add("mov al,0h;");
//asmCodeList.add("mov tem[1],al;");
//asmCodeList.add("lea dx,tem;");
//asmCodeList.add(" mov ah,0ah");
//asmCodeList.add("int 21h");
//asmCodeList.add(";处理输入的数据，并赋值给变量");
//asmCodeList.add("mov cl,0000h;");
//asmCodeList.add("mov al,tem[1];");
//asmCodeList.add("sub al,1;");
//asmCodeList.add("mov cl,al;");
//
//asmCodeList.add("mov ax,0000h;");
//asmCodeList.add("mov bx,0000h;");
//
//asmCodeList.add("mov al,tem[2];");
//asmCodeList.add("sub al,30h;");
//asmCodeList.add("mov " + fourElemList.get(i).arg1 + ",ax;");
//
//asmCodeList.add("mov ax,cx");
//asmCodeList.add("sub ax,1");
//asmCodeList.add("jc inputEnd" + (i + 1));
//
//asmCodeList.add(";");
//asmCodeList.add("MOV SI,0003H;");
//
//asmCodeList.add("ln" + (i + 1) + ":mov bx,10;");
//asmCodeList.add("mov ax," + fourElemList.get(i).arg1 + ";");
//
//asmCodeList.add("mul bx;");
//asmCodeList.add("mov " + fourElemList.get(i).arg1 + ",ax;");
//asmCodeList.add("mov ax,0000h;");
//asmCodeList.add("mov al,tem[si]");
//asmCodeList.add("sub al,30h;");
//asmCodeList.add("add ax," + fourElemList.get(i).arg1 + ";");
//asmCodeList.add("mov " + fourElemList.get(i).arg1 + ",ax");
//asmCodeList.add("INC SI");
//asmCodeList.add("loop ln" + (i + 1));
//asmCodeList.add("inputEnd" + (i + 1) + ": nop");
//asmCodeList.add("");;
//asmCodeList.add("");
//
//asmCodeList.add(";换行");
//asmCodeList.add("mov dl,0dh");
//asmCodeList.add("mov ah,2");
//asmCodeList.add("int 21h");
//asmCodeList.add("mov dl,0ah");
//asmCodeList.add("mov ah,2");
//asmCodeList.add("int 21h");
//asmCodeList.add("\n");
//
//}
else if (fourElemList.get(i).op.equals("SCANF")) {
//op为SCANF
asmCodeList.add("L" + (i + 1) + ":");
asmCodeList.add("lea dx,"+fourElemList.get(i).arg1);
asmCodeList.add("mov ah,0ah ;输入");
asmCodeList.add("int 21h");
asmCodeList.add("mov ah,1  ;中断使程序暂停一下");
asmCodeList.add("int 21h");

asmCodeList.add(";输出回车 换行");
asmCodeList.add("mov ah,02h ;2号功能：字符输出");
asmCodeList.add("mov dl,0dh ;0dh即为回车符");
asmCodeList.add("int 21h ;此处输出回车");
asmCodeList.add("mov ah,02h");
asmCodeList.add("mov dl,0ah ;0ah即为换行符");
asmCodeList.add("int 21h ;此处输出换行");
}

if (i == position) {
// 到达出口
asmCodeList.add("TheEnd:nop");// 设置运算的出口位置 空操作
}

}

}

/**
* 汇编尾部
*/
public void asmTail() {

asmCodeList.add("mov ax,4ch ;退出程序返回操作系统。");
asmCodeList.add("int 21h;调用系统中断");
asmCodeList.add("code ends");
asmCodeList.add("end start");

}
}
