datasg segment
T1 dw 0
T2 dw 0
T3 dw 0
scanf n 2 db input n:$'
printf n 5 db n:$'
datasg ends
stacks segment
stacks ends
codesg segment
assume cs:code,ds:data
start:
MOV AX,data
MOV ds,AX
L1:
mov AX,1
mov sum,AX
lea dx,n
mov ah,0ah ;输入
int 21h
mov ah,1  ;中断使程序暂停一下
int 21h
;输出回车 换行
mov ah,02h ;2号功能：字符输出
mov dl,0dh ;0dh即为回车符
int 21h ;此处输出回车
mov ah,02h
mov dl,0ah ;0ah即为换行符
int 21h ;此处输出换行
mov AX, n
sub AX, 0
sub AX,1
mov T1,AX
cmp T1,0
jns 7
L2:
lea dx,n
mov ah,9  ;21h的9号功能输出
int 21h
mov ah,1  ;中断使程序暂停一下
int 21h
;输出回车 换行
mov ah,02h ;2号功能：字符输出
mov dl,0dh ;0dh即为回车符
int 21h ;此处输出回车
mov ah,02h
mov dl,0ah ;0ah即为换行符
int 21h ;此处输出换行
jmp TheEnd
L3:
mov AX,1
mov i,AX
L4:
mov AX, i
sub AX, n
sub AX,1
mov T2,AX
jmp TheEnd
L5:
mov BX, sum
mul BX,i
mov T3, BX
mov AX,T3
mov sum,AX
mov AX, i
add AX, 1
mov i, AX
jmp L8
TheEnd:nop
mov ax,4c00h ;退出程序返回操作系统。
int 21h;调用系统中断
codesg ends
end start
