.source functions.j
.class public Funcs
.super java/lang/Object

; init
.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

; plus
.method public static plus(II)I
    .limit stack 2
    .limit locals 2
    iload 0
    iload 1
    iadd
    ireturn
.end method

; main
.method public static main([Ljava/lang/String;)V
    .limit stack 5
    .limit locals 5

    ;;Read
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    invokevirtual java/util/Scanner/nextInt()I
    istore 2
    iload 2
    
    
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    invokevirtual java/util/Scanner/nextInt()I
    istore 3
    iload 3
    iload 2

    invokestatic Funcs/plus(II)I
    istore 2

    new java/lang/StringBuilder
    dup
    invokespecial java/lang/StringBuilder/<init>()V
    iload 2
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;
    astore 4
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload 4
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V

    return
.end method