(ns event-driven-compiler.printer
  (:gen-class)
  (:require [event-driven-compiler.syntactical-node-tree :as syntree]))

;; preâmbulos

(def classname (ref "C"))

(defn set-classname [name]
  (dosync (alter classname (constantly (str "C" name)))))

;; preâmbulos de classe
(defn class-def-pre [filename]
  (str ".source " filename ".j\n"
       ".class public " @classname "\n"
       ".super java/lang/Object\n\n"))

(def init-pre
  (str ";init\n.method public <init>()V\naload 0\ninvokespecial java/lang/Object/<init>()V\nreturn\n.end method\n\n"))

(defn class-pre [filename]
  (str (class-def-pre filename) init-pre))

;; preâmbulos de lógica
(def equals-pre
  (str ".method public static equals(II)I\n.limit stack 2\n.limit locals 2\niload 0\niload 1\nif_icmpeq RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n\n"))

(def diff-pre
  (str ".method public static diff(II)I\n.limit stack 2\n.limit locals 2\niload 0\niload 1\nif_icmpeq RET_0\nldc 1\nireturn\nRET_0:\nldc 0\nireturn\n.end method\n\n"))

(def not-pre
  (str ".method public static not(I)I\n.limit stack 1\n.limit locals 1\niload 0\nifeq RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n\n"))

(def grt-pre
  (str ".method public static grt(II)I\n.limit stack 2\n.limit locals 2\niload 0\niload 1\nif_icmpgt RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n\n"))

(def les-pre
  (str ".method public static les(II)I\n.limit stack 2\n.limit locals 2\niload 0\niload 1\nif_icmplt RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n\n"))

(def greq-pre
  (str ".method public static greq(II)I\n.limit stack 2\n.limit locals 2\niload 0\niload 1\nif_icmpge RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n\n"))

(def lseq-pre
  (str ".method public static lseq(II)I\n.limit stack 2\n.limit locals 2\niload 0\niload 1\nif_icmple RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n\n"))

(def and-pre
  (str ".method public static and(II)I\n.limit stack 2\n.limit locals 2\niload 0\nifeq RET_0\niload 1\nifeq RET_0\nldc 1\nireturn\nRET_0:\nldc 0\nireturn\n.end method\n\n"))

(def or-pre
  (str ".method public static or(II)I\n.limit stack 2\n.limit locals 2\niload 0\nifne RET_1\niload 1\nifne RET_1\nldc 0\nireturn\nRET_1:\nldc 1\nireturn\n.end method\n\n"))

(def logic-preambles
  (str equals-pre diff-pre not-pre grt-pre les-pre greq-pre lseq-pre and-pre or-pre))

;; preâmbulos de I/O

(def read-pre
  (str ".method public static read()I\n.limit stack 3\n.limit locals 1\nnew java/util/Scanner\ndup\ngetstatic java/lang/System/in Ljava/io/InputStream;\ninvokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V\ninvokevirtual java/util/Scanner/nextInt()I\nireturn\n.end method\n\n"))

(def print-pre
  (str ".method public static print(I)V\n.limit stack 3\n.limit locals 3\n\nnew java/lang/StringBuilder\ndup\ninvokespecial java/lang/StringBuilder/<init>()V\niload 0\ninvokevirtual java/lang/StringBuilder/append(I)Ljava/lang/StringBuilder;\ninvokevirtual java/lang/StringBuilder/toString()Ljava/lang/String;\nastore 1\ngetstatic java/lang/System/out Ljava/io/PrintStream;\naload 1\ninvokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\nreturn\n.end method\n\n"))

(def io-pre
  (str read-pre print-pre))

;; preâmbulo completo

(defn full-preamble [filename]
  (str (class-pre filename) logic-preambles io-pre))

;; impressão de tokens sintáticos (synts)

;; contadores de ocorrência

(def occurrence-counters (ref {:WHILE 0
                               :FOR 0
                               :IF 0
                               :IFELSE 0}))

(defn increment-counter [counter]
  (dosync (alter occurrence-counters (fn [counters] (into counters {counter (+ 1 (counters counter))})))))

;;(def context-key (ref "main"))

;;(def main-var-pool (ref {}))
;;(def main-reg-counter (ref {}))

;;(def function-header-pool (ref {}))
;;(def function-var-pools (ref {}))
;;(def function-reg-counters (ref {}))

;; function context
(def printer-context (ref "main"))
(defn set-printer-context [new-context]
  (dosync (alter printer-context (constantly new-context))))
(defn get-var-pool []
  (if (= @printer-context "main")
    @syntree/main-var-pool
    (@syntree/function-var-pools @printer-context)))

(def compiled-functions (ref ""))
(defn write-function [compiled-func]
  (dosync (alter compiled-functions str compiled-func)))

(declare comp-synt)

;; program root synt
(defn comp-prog-synt [synt]
  (let [main-decl (str ".method public static main([Ljava/lang/String;)V\n.limit stack 9\n.limit locals 9\n\n")
        compiled-body (reduce str (map comp-synt (:expr-synts synt)))
        end-decl (str "return\n.end method\n")]
    (str main-decl compiled-body end-decl)))

;; atom synt
(defn comp-atom-synt [synt]
  (case (:type synt)
    :integer (str "ldc " (:content synt) "\n")
    :string (str "ldc \"" (:content synt) "\"\n")))

;; ifelse synts
(defn comp-if-synt [synt]
  (let [idx (@occurrence-counters :IF)]
    (increment-counter :IF)
    (str "IF_" idx ":\n"
         (comp-synt (:cond-synt synt))
         "ifeq IF_" idx "_SKIP\n"
         (comp-synt (:expr-synt synt))
         ;;"pop\n"
         "IF_" idx "_SKIP:\n")))

(defn comp-if-else-synt [synt]
  (let [idx (@occurrence-counters :IFELSE)]
    (increment-counter :IFELSE)
    (str "IFELSE_" idx ":\n"
         (comp-synt (:cond-synt synt))
         "ifeq IFELSE_" idx "_ELSE\n"
         (comp-synt (:expr1-synt synt))
         "goto IFELSE_" idx "_END\n"
         "IFELSE_" idx "_ELSE:\n"
         (comp-synt (:expr2-synt synt))
         "IFELSE_" idx "_END:\n")))

;; loop synts
(defn comp-while-synt [synt]
  (let [idx (@occurrence-counters :WHILE)]
    (increment-counter :WHILE)
    (str "WHILE_" idx "\n"
         "WHILE_" idx "_CHECK:\n"
         (comp-synt (:cond-synt synt))
         "ifeq WHILE_" idx "_BREAK\n"
         "WHILE_" idx "_DO:\n"
         (reduce str (map comp-synt (:body-synt synt)))
         "goto WHILE_" idx "_CHECK\n"
         "WHILE_" idx "_BREAK:\n")))
(defn comp-for-synt [synt]
  (let [idx (@occurrence-counters :FOR)
        var-name (:content (:identif-synt synt))
        var-reg ((get-var-pool) var-name)]
    (increment-counter :FOR)
    (str "FOR_" idx ":\n"
         (comp-synt (:start-synt synt))
         "istore " var-reg "\n"
         "FOR_" idx "_CHECK:\n"
         "iload " var-reg "\n"
         (comp-synt (:stop-synt synt))
         "if_icmpge FOR_" idx "_BREAK\n"
         "FOR_" idx "_DO:\n"
         (reduce str (map comp-synt (:body-synt synt)))
         "FOR_" idx "_STEP:\n"
         "iload " var-reg "\n"
         (comp-synt (:step-synt synt))
         "iadd\n"
         "istore " var-reg "\n"
         "goto FOR_" idx "_CHECK\n"
         "FOR_" idx "_BREAK:\n")))

;; function synts
(defn comp-func-synt [synt]
  (let [func-name (:content (:name-synt synt))]
    (set-printer-context func-name)
    (let [regs-count (@syntree/function-reg-counters func-name)
          func-header (str ".method public static " func-name "(" (apply str (repeat (@syntree/function-header-pool func-name) "I")) ")I\n")
          func-limits (str ".limit stack " (+ 10 regs-count) "\n.limit locals " (+ 10 regs-count) "\n")
          func-tail (str "ireturn\n.end method\n\n")]
      (write-function (str func-header
                           func-limits
                           (reduce str (map comp-synt (:body-synt synt)))
                           func-tail))
      (set-printer-context "main")
      "")))

(defn comp-call-synt [synt]
  (let [func-name (:content (:name-synt synt))
        func-call (str "invokestatic " @classname "/" func-name "(" (apply str (repeat (@syntree/function-header-pool func-name) "I")) ")I\n")]
    (str (reduce str (map comp-synt (:args-synts synt)))
         func-call)))

;; arithmetic synts
(defn arithm-operator-to-asm [operator]
  (case operator
    "+" "iadd\n"
    "-" "isub\n"
    "*" "imul\n"
    "/" "idiv\n"
    "%" "irem\n"))
(defn comp-arithm-synt [synt]
  (str (comp-synt (:first-operand-synt synt))
       (comp-synt (:second-operand-synt synt))
       (arithm-operator-to-asm (:content (:operator-synt synt)))))

;; logic synts
(defn logic-operator-to-asm [operator]
  (case operator
    "&&" (str "invokestatic " @classname "/and(II)I\n")
    "||" (str "invokestatic " @classname "/or(II)I\n")
    "!" (str "invokestatic " @classname "/not(I)I\n")))

(defn comp-binary-logic-synt [synt]
  (str (comp-synt (:first-operand-synt synt))
       (comp-synt (:second-operand-synt synt))
       (logic-operator-to-asm (:content (:operator-synt synt)))))
(defn comp-unary-logic-synt [synt]
  (str (comp-synt (:operand-synt synt))
       (logic-operator-to-asm (:content (:operator-synt synt)))))

;; comparison synts
(defn comparison-operator-to-asm [operator]
  (case operator
    "=" (str "invokestatic " @classname "/equals(II)I\n")
    "!=" (str "invokestatic " @classname "/diff(II)I\n")
    ">" (str "invokestatic " @classname "/grt(II)I\n")
    "<" (str "invokestatic " @classname "/les(II)I\n")
    ">=" (str "invokestatic " @classname "/greq(II)I\n")
    "<=" (str "invokestatic " @classname "/lseq(II)I\n")))
(defn comp-comparison-synt [synt]
  (str (comp-synt (:first-operand-synt synt))
       (comp-synt (:second-operand-synt synt))
       (comparison-operator-to-asm (:content (:operator-synt synt)))))

;; expr block synt
(defn comp-do-synt [synt]
  (reduce str (map comp-synt (:expr-synts synt))))

;; vars synts
(defn comp-set-synt [synt]
  (let [var-reg ((get-var-pool) (:content (:identif-synt synt)))]
    (str (comp-synt (:value-synt synt))
         "istore " var-reg "\n")))
(defn comp-var-synt [synt]
  (str "iload " ((get-var-pool) (:content synt)) "\n"))

;; IO synts
(defn comp-print-synt [synt]
  (str (comp-synt (:value-synt synt))
       "invokestatic " @classname "/print(I)V\n"))
(defn comp-read-synt [synt]
  (str "invokestatic " @classname "/read()I\n"))

;; vector synts
(defn comp-vector-synt [synt]
  (let [vec-reg ((get-var-pool) (:content (:name-synt synt)))]
    (str (comp-synt (:length-synt synt))
         "newarray int\n"
         "astore " vec-reg "\n")))
(defn comp-vecget-synt [synt]
  (let [vec-reg ((get-var-pool) (:content (:name-synt synt)))]
    (str "aload " vec-reg "\n"
         (comp-synt (:index-synt synt))
         "iaload\n")))
(defn comp-vecset-synt [synt]
  (let [vec-reg ((get-var-pool) (:content (:name-synt synt)))]
    (str "aload " vec-reg "\n"
         (comp-synt (:index-synt synt))
         (comp-synt (:value-synt synt))
         "iastore\n")))
(defn comp-len-synt [synt]
  (let [vec-reg ((get-var-pool) (:content (:name-synt synt)))]
    (str "aload " vec-reg "\n"
         "arraylength\n")))

;; labels synts
(defn label-synt [name-synt expr-synt]
  {:type :LABEL
   :name-synt name-synt
   :expr-synt expr-synt})
(defn goto-synt [name-synt]
  {:type :GOTO
   :name-synt name-synt})
(defn if-goto-synt [cond-synt name-synt]
  {:type :IFGOTO
   :cond-synt cond-synt
   :name-synt name-synt})

;; compilation generic function
(defn comp-synt [synt]
  (println (str "synt fornecido: " synt))
  (case (:type synt)
    :PROGRAM (comp-prog-synt synt)
    :ARITHM (comp-arithm-synt synt)
    :IF (comp-if-synt synt)
    :IFELSE (comp-if-else-synt synt)
    :PRINT (comp-print-synt synt)
    :READ (comp-read-synt synt)
    :FOR (comp-for-synt synt)
    :WHILE (comp-while-synt synt)
    :SET (comp-set-synt synt)
    :DO (comp-do-synt synt)
    :BIN-LOGIC (comp-binary-logic-synt synt)
    :UN-LOGIC (comp-unary-logic-synt synt)
    :COMP (comp-comparison-synt synt)
    :VECTOR (comp-vector-synt synt)
    :VECGET (comp-vecget-synt synt)
    :VECSET (comp-vecset-synt synt)
    :LEN (comp-len-synt synt)
    :DEF (comp-func-synt synt)
    :CALL (comp-call-synt synt)
    :identifier (comp-var-synt synt)
    :integer (comp-atom-synt synt)
    :string (comp-atom-synt synt)
    :else nil))

;; compilation root function
(defn print-full [filename program-synt-tree]
  (println (str "árvore sintática: " program-synt-tree))
  (let [preamble (full-preamble filename)
        main-target (comp-synt program-synt-tree)]
    (str preamble @compiled-functions main-target)))

