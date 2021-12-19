(ns event-driven-compiler.printer
  (:gen-class))

(def expr-node-tree {:type :if :cond {:type :< :ex1 {:type :value :content 5} :ex2 {:type :identifier :content "x"}}})

(def print-node)

;; preâmbulos

;; preâmbulos de classe
(defn class-def-pre [filename]
  (str ".source " filename ".j\n"
       ".class public C" filename "\n"
       ".super java/lang/Object\n\n"))

(def init-pre
  (str ";init\n.method public <init>()V\naload 0\n invokespecial java/lang/Object/<init>()V\nreturn\n.end method\n"))

(defn class-pre [filename]
  (str (class-def-pre filename) init-pre))

;; preâmbulos de lógica
(def equals-pre
  (str ".method public static equals(II)I\n.limit stack 2\n.limit local 2\niload 0\niload 1\nif_icmpeq RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n"))

(def diff-pre
  (str ".method public static diff(II)I\n.limit stack 2\n.limit local 2\niload 0\niload 1\nif_icmpeq RET_0\nldc 1\nireturn\nRET_0:\nldc 0\nireturn\n.end method\n"))

(def not-pre
  (str ".method public static not(I)I\n.limit stack 1\n.limit local 1\niload 0\nieq RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n"))

(def grt-pre
  (str ".method public static grt(II)I\n.limit stack 2\n.limit local 2\niload 0\niload 1\nif_icmpgt RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n"))

(def les-pre
  (str ".method public static les(II)I\n.limit stack 2\n.limit local 2\niload 0\niload 1\nif_icmplt RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n"))

(def greq-pre
  (str ".method public static greq(II)I\n.limit stack 2\n.limit local 2\niload 0\niload 1\nif_icmpge RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n"))

(def lseq-pre
  (str ".method public static lseq(II)I\n.limit stack 2\n.limit local 2\niload 0\niload 1\nif_icmple RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n"))

(def and-pre
  (str ".method public static and(II)I\n.limit stack 2\n.limit local 2\niload 0\nifeq RET_0\niload 1\nifeq RET_0\nldc 1\nireturn\nRET_0:\nldc 0\nireturn\n.end method\n"))

(def or-pre
  (str ".method public static or(II)I\n.limit stack 2\n.limit local 2\niload 0\nifne RET_1\niload 1\nifne RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n"))

(def logic-preambles
  (str equals-pre diff-pre not-pre grt-pre les-pre greq-pre lseq-pre and-pre or-pre))

;; preâmbulos de I/O

(def read-pre
  (str ".method public static read()I\n.limit stack 3\n.limit locals 1\nnew java/util/Scanner\ndup\ngetstatic java/lang/System/in Ljava/io/InputStream;\ninvokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V\ninvokevirtual java/util/Scanner/nextInt()I\nireturn\n.end method\n"))

(def print-pre
  (str ".method public static print(I)V\n.limit stack 3\n.limit locals 3\n\nnew java/lang/StringBuilder\ndup\ninvokespecial java/lang/StringBuilder/<init>()V\niload 0\ninvokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;\ninvokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;\nastore 1\ngetstatic java/lang/System/out Ljava/io/PrintStream;\naload 1\ninvokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\nreturn\n.end method\n"))

(def io-pre
  (str read-pre print-pre))

;; preâmbulo completo

(defn full-preamble [filename]
  (str (class-pre filename) logic-preambles io-pre))

;; impressão de tokens sintáticos (synts)
