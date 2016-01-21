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
 *         ������������
 */
public class Asm {

	private ArrayList<String> asmCodeList = new ArrayList<String>();// ��������붯̬�ַ�����
																	// ������Ϊstring����
	private ArrayList<FourElement> fourElemList; // ��ȡfourelement���͵���Ԫʽ����
	public ArrayList<String> enterPos = new ArrayList<String>();
	int z, h;
	String x;
	int n = 1;
	int flagp, flags;

	/**
	 * @param fourElemList
	 *            //��Ԫʽ
	 * @param fourElemT
	 */
	// fourElemT�����ʱ������ fourElemList�����Ԫʽ���� ����parser()�л��ֵ
	public Asm(ArrayList<FourElement> fourElemList, ArrayList<String> fourElemT) {
		this.fourElemList = fourElemList;
		asmHead(fourElemT);
		asmCode(fourElemList);
		asmTail();
		for (int i = 0; i < asmCodeList.size(); i++)
			System.out.println(asmCodeList.get(i));
	}

	/**
	 * ��ȡasm�ļ���ַ
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getAsmFile() throws IOException {

		File file = new File("./result/");// ����һ�������·���Ķ���
		if (!file.exists()) {// �����ڸ�Ŀ¼
			file.mkdirs(); // �����ļ���
		}
		String path = file.getAbsolutePath(); // ��ȡ��ַ��path
		FileOutputStream fos = new FileOutputStream(path + "/c_to_asm.asm");// ���ֽ����ķ�ʽ���浽��ָ���ļ�
		BufferedOutputStream bos = new BufferedOutputStream(fos);// ����һ��������
		OutputStreamWriter osw1 = new OutputStreamWriter(bos, "utf-8");// �ֽ���ת��Ϊ�ַ���

		PrintWriter pw1 = new PrintWriter(osw1); // �ı��������ӡ����

		for (int i = 0; i < asmCodeList.size(); i++)
			pw1.println(asmCodeList.get(i)); // ����д���ļ�

		pw1.close(); // �ر��ļ�
		return path + "/c_to_asm.asm";

	}

	/* �ж��Ƿ�Ϊ��������� */
	public void IsOutport(int m) {// m��Ϊ�ڼ�����Ԫʽ m=1,2,3--13
		int x = 0, a;
		rukou(fourElemList);//����enterPos��̬������
			for (int b = 0; b < enterPos.size(); b++) { // b=0--4
				a = Integer.parseInt(enterPos.get(b)) - 1;
				fourElemList.get(a).outport = 1;
		}
		x = fourElemList.get(m - 1).outport;// x��Ϊ��m����Ԫʽ��outportֵ
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
				if (h == (fourElemList.size() + 1)) { // ��ת������ h==13+1
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
	 * ���ͷ��
	 * 
	 * @param fourElemT
	 */
	public void asmHead(ArrayList<String> fourElemT) {
		// ������ݶδ���
		asmCodeList.add("datasg segment");// ���ݶ�

		for (int j = 0; j < fourElemT.size(); j++) {
			asmCodeList.add(fourElemT.get(j) + " dw 0"); // Ϊ��ʱ����T1/2...���붨����������
		}

		for (int i = 0; i < fourElemList.size(); i++) { // �������scanf ��printf
			if (fourElemList.get(i).op.equals("PRINTF")) {
				if (flagp == 0) {
					asmCodeList.add("printf " + fourElemList.get(i).arg1 + " " + (i + 1) + " db "
							+ fourElemList.get(i).arg1 + ":$'"); // (i+1)Ϊ�к�
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
		asmCodeList.add("stacks segment");// ��ջ��
		asmCodeList.add("stacks ends");
		asmCodeList.add("codesg segment");
		asmCodeList.add("assume cs:code,ds:data");// �μĴ�������˵��αָ��
													// ��μĴ���cs��code���������ݶμĴ���ds��data������
		asmCodeList.add("start:");
		asmCodeList.add("MOV AX,data"); // ��dsָ���Լ���������ݶ�
		asmCodeList.add("MOV ds,AX");

	}

	/**
	 * ���ɴ���δ���
	 * 
	 * @param fourElemList
	 */
	public void asmCode(ArrayList<FourElement> fourElemList) {

		// int position = endPosition(fourElemList);// ��ȡ����ĳ���λ��
		int position = fourElemList.size() - 1;

		for (int i = 0; i < fourElemList.size(); i++) {

			int flag = 0;// �Ƿ���ת������ĳ���λ��TheEnd���������㡣
			if (fourElemList.get(i).arg1.equals(position + 2 + "")) { // ���arg1ָ��������Ԫʽ����һ��������

				if (fourElemList.get(i).op.equals("FJ") || fourElemList.get(i).op.equals("RJ")) {
					// �����������RJ/LJ
					flag = 1;
				}
			}

			if (fourElemList.get(i).op.equals("=")) {
				// ���op�� =
				IsOutport(i + 1);
				asmCodeList.add("mov AX," + fourElemList.get(i).arg1); // ��
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX"); // д
			} else if (fourElemList.get(i).op.equals("+")) {
				// ���op�� +(��arg2)
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("add AX, " + fourElemList.get(i).arg2);
				asmCodeList.add("mov " + fourElemList.get(i).result + ", AX");
			} else if (fourElemList.get(i).op.equals("++")) {
				// ���op�� ++(��arg2)
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("add AX, 1"); // �ͼ�1����
				asmCodeList.add("mov " + fourElemList.get(i).result + ", AX");
			} else if (fourElemList.get(i).op.equals("-")) {
				// ���op�� -
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg2);
				asmCodeList.add("mov " + fourElemList.get(i).result + ", AX");
			} else if (fourElemList.get(i).op.equals("*")) {
				// ���op�� *
				IsOutport(i + 1);
				asmCodeList.add("mov BX, " + fourElemList.get(i).arg1);
				asmCodeList.add("mul BX," + fourElemList.get(i).arg2);
				asmCodeList.add("mov " + fourElemList.get(i).result + ", BX");
			} else if (fourElemList.get(i).op.equals("/")) {
				// ���op�� /
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("div AX," + fourElemList.get(i).arg2);
				asmCodeList.add("mov " + fourElemList.get(i).result + ", Ax");
			}

			else if (fourElemList.get(i).op.equals("FJ")) {
				// ���op�� FJ
				if (flag == 1) {// ���=1����TheEnd,������� => jmp TheEnd
					// jmp����������ת jns:����0��ת
					IsOutport(i + 1);
					asmCodeList.add("jmp TheEnd");// jmp
																		// TheEnd
					// fourElemList.get(i + 1).outport = 1;
				} else {
					IsOutport(i + 1);
					asmCodeList.add("cmp " + fourElemList.get(i).arg2 + ",0");
					asmCodeList.add("jns " + fourElemList.get(i).arg1); // T...��ʱ����>0˵���Ǽ٣���ת��arg1

				}

			} else if (fourElemList.get(i).op.equals("RJ")) {
				// ���op�� RJ
				if (flag == 1) {// ���=1����TheEnd,������㡣=> jmp TheEnd
					// jmp����������ת
					IsOutport(i + 1);
					asmCodeList.add("jmp TheEnd");

				} else {
					IsOutport(i + 1);
					asmCodeList.add("jmp L" + fourElemList.get(i).arg1);

				}

			} else if (fourElemList.get(i).op.equals("<")) {
				// ���op�� <
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg2); // arg1-arg2
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX");// �浽��ʱ����
			} else if (fourElemList.get(i).op.equals(">")) {
				// ���op�� >
				IsOutport(i + 1);
				asmCodeList.add("mov AX," + fourElemList.get(i).arg2);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg1);// arg2-arg1
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX");// �浽��ʱ����

			} else if (fourElemList.get(i).op.equals(">=")) {
				// ���op�� >=
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg2);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("sub AX,1");
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX");

			} else if (fourElemList.get(i).op.equals("<=")) {
				// ���op�� <=
				IsOutport(i + 1);
				asmCodeList.add("mov AX, " + fourElemList.get(i).arg1);
				asmCodeList.add("sub AX, " + fourElemList.get(i).arg2);
				asmCodeList.add("sub AX,1");
				asmCodeList.add("mov " + fourElemList.get(i).result + ",AX");

			} else if (fourElemList.get(i).op.equals("PRINTF")) {
				// opΪPRINTF
				IsOutport(i + 1);
				asmCodeList.add("lea dx," + fourElemList.get(i).arg1); // ȡƫ�Ƶ�ַ
																		// �����ݼĴ�����
				asmCodeList.add("mov ah,9  ;21h��9�Ź������");
				asmCodeList.add("int 21h");
				asmCodeList.add("mov ah,1  ;�ж�ʹ������ͣһ��");
				asmCodeList.add("int 21h");

				asmCodeList.add(";����س� ����");
				asmCodeList.add("mov ah,02h ;2�Ź��ܣ��ַ����");
				asmCodeList.add("mov dl,0dh ;0dh��Ϊ�س���");
				asmCodeList.add("int 21h ;�˴�����س�");
				asmCodeList.add("mov ah,02h");
				asmCodeList.add("mov dl,0ah ;0ah��Ϊ���з�");
				asmCodeList.add("int 21h ;�˴��������");
			}

			else if (fourElemList.get(i).op.equals("SCANF")) {
				// opΪSCANF
				IsOutport(i + 1);
				asmCodeList.add("lea dx," + fourElemList.get(i).arg1);
				asmCodeList.add("mov ah,0ah ;����");
				asmCodeList.add("int 21h");
				asmCodeList.add("mov ah,1  ;�ж�ʹ������ͣһ��");
				asmCodeList.add("int 21h");

				asmCodeList.add(";����س� ����");
				asmCodeList.add("mov ah,02h ;2�Ź��ܣ��ַ����");
				asmCodeList.add("mov dl,0dh ;0dh��Ϊ�س���");
				asmCodeList.add("int 21h ;�˴�����س�");
				asmCodeList.add("mov ah,02h");
				asmCodeList.add("mov dl,0ah ;0ah��Ϊ���з�");
				asmCodeList.add("int 21h ;�˴��������");
			}

			if (i == position) {
				// �������
				asmCodeList.add("TheEnd:nop");// ��������ĳ���λ�� �ղ���
			}

		}

	}

	/**
	 * ���β��
	 */
	public void asmTail() {

		asmCodeList.add("mov ax,4c00h ;�˳����򷵻ز���ϵͳ��");
		asmCodeList.add("int 21h;����ϵͳ�ж�");
		asmCodeList.add("codesg ends");
		asmCodeList.add("end start");

	}

}
