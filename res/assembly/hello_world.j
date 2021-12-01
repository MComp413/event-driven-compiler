.source arquivo.j
.class public Arquivo
.super java/lang/Object

; init
.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method


; main

.method public static main([Ljava/lang/String;)V
    .limit stack 5
    .limit locals 5
    
    ;; Print
    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Hello World!"
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    

    ;;Read
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    invokevirtual java/util/Scanner/nextInt()I
    istore_2
    

    ;; Print input
    new java/lang/StringBuilder
    dup
    invokespecial java/lang/StringBuilder/<init>()V
    iload_2
    invokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;
    invokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;
    astore_3
    getstatic java/lang/System/out Ljava/io/PrintStream;
    aload_3
    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V


    return
.end method


