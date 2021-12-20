.source compiled.j
.class public Ccompiled
.super java/lang/Object

;init
.method public <init>()V
aload 0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static equals(II)I
.limit stack 2
.limit locals 2
iload 0
iload 1
if_icmpeq RET_1
ldc 0
ireturn
RET_1:
ldc 1
ireturn
.end method

.method public static diff(II)I
.limit stack 2
.limit locals 2
iload 0
iload 1
if_icmpeq RET_0
ldc 1
ireturn
RET_0:
ldc 0
ireturn
.end method

.method public static not(I)I
.limit stack 1
.limit locals 1
iload 0
ifeq RET_1
ldc 0
ireturn
RET_1:
ldc 1
ireturn
.end method

.method public static grt(II)I
.limit stack 2
.limit locals 2
iload 0
iload 1
if_icmpgt RET_1
ldc 0
ireturn
RET_1:
ldc 1
ireturn
.end method

.method public static les(II)I
.limit stack 2
.limit locals 2
iload 0
iload 1
if_icmplt RET_1
ldc 0
ireturn
RET_1:
ldc 1
ireturn
.end method

.method public static greq(II)I
.limit stack 2
.limit locals 2
iload 0
iload 1
if_icmpge RET_1
ldc 0
ireturn
RET_1:
ldc 1
ireturn
.end method

.method public static lseq(II)I
.limit stack 2
.limit locals 2
iload 0
iload 1
if_icmple RET_1
ldc 0
ireturn
RET_1:
ldc 1
ireturn
.end method

.method public static and(II)I
.limit stack 2
.limit locals 2
iload 0
ifeq RET_0
iload 1
ifeq RET_0
ldc 1
ireturn
RET_0:
ldc 0
ireturn
.end method

.method public static or(II)I
.limit stack 2
.limit locals 2
iload 0
ifne RET_1
iload 1
ifne RET_1
ldc 0
ireturn
RET_1:
ldc 1
ireturn
.end method

.method public static read()I
.limit stack 3
.limit locals 1
new java/util/Scanner
dup
getstatic java/lang/System/in Ljava/io/InputStream;
invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
invokevirtual java/util/Scanner/nextInt()I
ireturn
.end method

.method public static print(I)V
.limit stack 3
.limit locals 3

new java/lang/StringBuilder
dup
invokespecial java/lang/StringBuilder/<init>()V
iload 0
invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;
astore 1
getstatic java/lang/System/out Ljava/io/PrintStream;
aload 1
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 9
.limit locals 9

invokestatic Ccompiled/read()I
istore 1
ldc 3
ldc 2
iload 1
imul
iadd
istore 2
iload 2
iload 2
iload 1
idiv
isub
invokestatic Ccompiled/print(I)V
return
.end method
