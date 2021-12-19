.class public Desvios
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

    ;;Read
    new java/util/Scanner
    dup
    getstatic java/lang/System/in Ljava/io/InputStream;
    invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
    invokevirtual java/util/Scanner/nextInt()I
    istore_2
    iload_2

    ;test condition

    ifeq Skip
        getstatic java/lang/System/out Ljava/io/PrintStream;
        ldc "noSkip"
        invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    Skip:
    
    iload_2

    ifeq Else
        getstatic java/lang/System/out Ljava/io/PrintStream;
        ldc "if"
        invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
        goto Endif
    Else:
        getstatic java/lang/System/out Ljava/io/PrintStream;
        ldc "else"
        invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
    Endif:

    return
.end method