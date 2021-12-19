.class public Loops
.super java/lang/Object

; init
.method public <init>()V
    aload 0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method


; main
.method public static main([Ljava/lang/String;)V
    .limit stack 5
    .limit locals 5

    ;;Read
    invokestatic Loops/read()I
    istore 0
    invokestatic Loops/read()I
    istore 1

    ;;for loop
    For_Start:
        iload 0
        istore 2
    For_Check:
        iload 2
        iload 1
        if_icmpgt For_Break
    For_Do:
        iload 2
        invokestatic Loops/print(I)V
    For_Step:
        iload 2
        ldc 2
        iadd
        istore 2
        goto For_Check
    For_Break:

    ;;while loop
    While:
        iload 0
        istore 2
    W1_Check:
        iload 2
        ifeq W1_Break
    W1_Do:
        iload 2
        invokestatic Loops/print(I)V
        ldc -1
        iload 2
        iadd
        istore 2
        goto W1_Check
    W1_Break:

    return
.end method

; input function 
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

; print function
.method public static print(I)V
    .limit stack 3
    .limit locals 2
    
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