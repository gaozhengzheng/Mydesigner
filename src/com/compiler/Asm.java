package com.compiler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * @author aile
 * 
 *         汇编代码生成类
 */
public class Asm {

	private ArrayList<String> asmCodeList = new ArrayList<String>();// 保存汇编代码动态字符数组
																	// 集合中为string类型
	private ArrayList<FourElement> fourElemList; // 获取fourelement类型的四元式序列
	public ArrayList<String> enterPos = new ArrayList<String>();
	int z, h;
	String x;
	int n = 1;
	int flagp, flags;

	/**
	 * @param fourElemList
	 *            //四元式
	 * @param fourElemT
	 */
	// fourElemT存放临时变量名 fourElemList存放四元式对象 均在parser()中获得值
	public Asm(ArrayList<FourElement> fourElemList, ArrayList<String> fourElemT) {
		this.fourElemList = fourElemList;
		asmHead(fourElemT);
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

		File file = new File("./result/");// 创建一个代表该路径的对象
		if (!file.exists()) {// 不存在该目录
			file.mkdirs(); // 创建文件夹
		}
		String path = file.getAbsolutePath(); // 获取地址给path
		FileOutputStream fos = new FileOutputStream(path + "/c_to_asm.asm");// 以字节流的方式保存到所指定文件
		BufferedOutputStream bos = new BufferedOutputStream(fos);// 增加一个缓冲区
		OutputStreamWriter osw1 = new OutputStreamWriter(bos, "utf-8");// 字节流转换为字符流

		PrintWriter pw1 = new PrintWriter(osw1); // 文本输出流打印对象

		for (int i = 0; i < asmCodeList.size(); i++)
			pw1.println(asmCodeList.get(i)); // 逐行写入文件

		pw1.close(); // 关闭文件
		return path + "/c_to_asm.asm";

	}

	/* 判断是否为基本块入口 */
	public void IsOutport(int m) {// m即为第几个四元式 m=1,2,3--13
		int x = 0, a;
		rukou(fourElemList);//生成enterPos动态数组结果
			for (int b = 0; b < enterPos.size(); b++) { // b=0--4
				a = Integer.parseInt(enterPos.get(b)) - 1;
				fourElemList.get(a).outport = 1;
		}
		x = fourElemList.get(m - 1).outport;// x即为第m个四元式的outport值
		if (m == 1) {
			asmCodeList.add("L1:");
		} else if (x == 1) {
			n++;
			asmCodeList.add("L" + n + ":");
		} else {

		}

	}

	public void rukou(ArrayList<FourElement> fourElemList) {
		for (int k = 0; k < fourElemList.size(); k++) {
			if (fourElemList.get(k).op.equals("FJ") || fourElemList.get(k).op.equals("RJ")) {
				h = Integer.parseInt(fourElemList.get(k).arg1);
				if (h == (fourElemList.size() + 1)) { // 跳转到结束 h==13+1
					if (k + 2 <= fourElemList.size())
						enterPos.add(Integer.toString(k + 2));
				} else {
					enterPos.add(fourElemList.get(k).arg1);
					if (k + 2 <= fourElemList.size()) 
						enterPos.add(Integer.toString(k + 2));
					
				}

			}
		}
	}

	/**
	 * 汇编头部
	 * 
	 * @param fourElemT
	 */
	public void asmHead(ArrayList<String> fourElemT) {
		// 添加数据段代码
		asmCodeList.add("datasg segment");// 数据段

		for (int j = 0; j < fourElemT.size(); j++) {
			asmCodeList.add(fourElemT.get(j) + " dw 0"); // 为临时变量T1/2...申请定义字数据区
		}

		for (int i = 0; i < fourElemList.size(); i++) { // 定义变量scanf 和printf
			if (fourElemList.get(i).op.equals("PRINTF")) {
				if (flagp == 0) {
					asmCodeList.add("printf " + fourElemList.get(i).arg1 + " " + (i + 1) + " db "
							+ fourElemList.get(i).arg1 + ":$'"); // (i+1)为行号
					flagp = 1;

				}
			} else if (fourElemList.get(i).op.equals("SCANF")) {
				if (flags == 0) {
					asmCodeList.add("scanf " + fourElemList.get(i).arg1 + " " + (i + 1) + " db input "
							+ fourElemList.get(i).arg1 + ":$'");
					flags = 1;
				}
			}
		}
		asmCodeList.add("datasg ends");
		asmCodeList.add("stacks segment");// 堆栈段
		asmCodeList.add("stacks ends");
		asmCodeList.add("codesg segment");
		asmCodeList.add("assume cs:code,ds:data");// 段寄存器关联说明伪指令
													// 码段寄存器cs与code关联；数据段寄存器ds与data关联；
		asmCodeList.add("start:");
		asmCodeList.add("MOV AX,data"); // 让ds指向自己定义的数据段
		asmCodeList.add("MOV ds,AX");

	}

	/**
	 * 生成代码段代码
	 * 
	 * @param fourElemList
	 */
	public void asmCode(ArrayList<FourElement> fourElemList) {

		// int position = endPosition(fourElemList);// 获取运算的出口位置
		int position = fourElemList.size() - 1;

		for (int i = 0; i < fourElemList.size(); i++) {

			int flag = 0;// 是否跳转到运算的出口位置TheEnd，结束运算。
			if (fourElemList.get(i).arg1.equals(position + 2 + "")) { // 如果arg1指向最终四元式的下一个，结束

				if (fourElemList.get(i).op.equals("FJ") || fourElemList.get(i).op.equals("RJ")) {
					// 如果操作符是RJ/LJ
					flag = 1;
				}
			}

			if (fourElemList.get(i).op.equals("=")) {
				// 如果op是 =
				IsOutport(i + 1);
				asmCodeList.add("mov AX," + fourElemList.get(i).arg1); // 读
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX"); // 写
			} else if (fourElemList.get(i).op.equals("+")) {
				// 如果op是 +(有arg2)
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("add AX, " + fourElemList.get(i).arg2);
				asmCodeList.add("mov " + fourElemList.get(i).result + ", AX");
			} else if (fourElemList.get(i).op.equals("++")) {
				// 如果op是 ++(无arg2)
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("add AX, 1"); // 就加1即可
				asmCodeList.add("mov " + fourElemList.get(i).result + ", AX");
			} else if (fourElemList.get(i).op.equals("-")) {
				// 如果op是 -
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg2);
				asmCodeList.add("mov " + fourElemList.get(i).result + ", AX");
			} else if (fourElemList.get(i).op.equals("*")) {
				// 如果op是 *
				IsOutport(i + 1);
				asmCodeList.add("mov BX, " + fourElemList.get(i).arg1);
				asmCodeList.add("mul BX," + fourElemList.get(i).arg2);
				asmCodeList.add("mov " + fourElemList.get(i).result + ", BX");
			} else if (fourElemList.get(i).op.equals("/")) {
				// 如果op是 /
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("div AX," + fourElemList.get(i).arg2);
				asmCodeList.add("mov " + fourElemList.get(i).result + ", Ax");
			}

			else if (fourElemList.get(i).op.equals("FJ")) {
				// 如果op是 FJ
				if (flag == 1) {// 如果=1跳到TheEnd,完成运算 => jmp TheEnd
					// jmp：无条件跳转 jns:大于0跳转
					IsOutport(i + 1);
					asmCodeList.add("jmp TheEnd");// jmp
																		// TheEnd
					// fourElemList.get(i + 1).outport = 1;
				} else {
					IsOutport(i + 1);
					asmCodeList.add("cmp " + fourElemList.get(i).arg2 + ",0");
					asmCodeList.add("jns " + fourElemList.get(i).arg1); // T...临时变量>0说明是假，跳转到arg1

				}

			} else if (fourElemList.get(i).op.equals("RJ")) {
				// 如果op是 RJ
				if (flag == 1) {// 如果=1跳到TheEnd,完成运算。=> jmp TheEnd
					// jmp：无条件跳转
					IsOutport(i + 1);
					asmCodeList.add("jmp TheEnd");

				} else {
					IsOutport(i + 1);
					asmCodeList.add("jmp L" + fourElemList.get(i).arg1);

				}

			} else if (fourElemList.get(i).op.equals("<")) {
				// 如果op是 <
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg2); // arg1-arg2
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX");// 存到临时变量
			} else if (fourElemList.get(i).op.equals(">")) {
				// 如果op是 >
				IsOutport(i + 1);
				asmCodeList.add("mov AX," + fourElemList.get(i).arg2);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg1);// arg2-arg1
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX");// 存到临时变量

			} else if (fourElemList.get(i).op.equals(">=")) {
				// 如果op是 >=
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg2);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("sub AX,1");
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX");

			} else if (fourElemList.get(i).op.equals("<=")) {
				// 如果op是 <=
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg2);
				asmCodeList.add("sub AX,1");
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX");

			} else if (fourElemList.get(i).op.equals("PRINTF")) {
				// op为PRINTF
				IsOutport(i + 1);
				asmCodeList.add("lea dx," + fourElemList.get(i).arg1); // 取偏移地址
																		// （数据寄存器）
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

			else if (fourElemList.get(i).op.equals("SCANF")) {
				// op为SCANF
				IsOutport(i + 1);
				asmCodeList.add("lea dx," + fourElemList.get(i).arg1);
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

		asmCodeList.add("mov ax,4c00h ;退出程序返回操作系统。");
		asmCodeList.add("int 21h;调用系统中断");
		asmCodeList.add("codesg ends");
		asmCodeList.add("end start");

	}

}
