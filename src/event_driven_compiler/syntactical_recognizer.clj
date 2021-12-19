(ns event-driven-compiler.syntactical-recognizer
  (:gen-class)
  (:require [event-driven-compiler.event-queue :as q]
            [event-driven-compiler.debugger :as dbg]))

; Estado do motor sintático
(def syntactical-queue (q/build-queue))

(defn get-current-token []
  ((syntactical-queue :peek)))

(defn get-next-token []
  ((syntactical-queue :pop))
  ((syntactical-queue :peek)))

; Funções de reconhecimento recursivas
; - estratégia descendente

(defn log-current-token [caller-string]
  (dbg/dbg-println (str caller-string
                        " - Type: "
                        (-> (get-current-token) :type)
                        " Content: "
                        (-> (get-current-token) :content))))

(defn match-terminal
  [& {:keys [type content caller] :or {type nil, content nil, caller ""}}]
  (let [current-token (get-current-token)]
    (cond
      (not= content nil)
      (if (= content (-> current-token :content))
        (do
          (log-current-token caller)
          (get-next-token)
          true)
        false)

      (not= type nil)
      (if (= type (-> current-token :type))
        (do
          (log-current-token caller)
          (get-next-token)
          true)
        false)

      :else
      false)))

(declare slip-list)
(declare slip-expression)
(declare slip-rest-list)

(defn slip-atom []
  (or
   (match-terminal :type :integer :caller "slip-atom")
   (match-terminal :type :string :caller "slip-atom")
   (match-terminal :type :identifier :caller "slip-atom")
   (match-terminal :content "TRUE" :caller "slip-atom")
   (match-terminal :content "FALSE" :caller "slip-atom")))

; Comandos de palavra reservada

(defn slip-if []
  (if (match-terminal :content "IF" :caller "slip-if")
    (if (slip-expression)
      (if (slip-expression)
        (match-terminal :content ")" :caller "slip-if")
        false)
      false)
    false))

(defn slip-if-else []
  (if (match-terminal :content "IFELSE" :caller "slip-if-else")
    (if (slip-expression)
      (if (slip-expression)
        (if (slip-expression)
          (match-terminal :content ")" :caller "slip-if-else")
          false)
        false)
      false)
    false))

(defn slip-do-list []
  (if (match-terminal :content "DO" :caller "slip-do-list")
    (slip-rest-list)
    false))

(defn slip-for []
  (if (match-terminal :content "FOR" :caller "slip-for")
    (if (match-terminal :type :identifier :caller "slip-for")
      (if (slip-expression)
        (if (slip-expression)
          (if (slip-expression)
            (slip-rest-list)
            false)
          false)
        false)
      false)
    false))

(defn slip-while []
  (if (match-terminal :content "WHILE" :caller "slip-while")
    (if (slip-expression)
      (slip-rest-list)
      false)
    false))

(defn slip-rest-params []
  (if (match-terminal :type :identifier :caller "slip-rest-params")
    (slip-rest-params)
    (match-terminal :content ")" :caller "slip-rest-params")))

(defn slip-params []
  (if (match-terminal :content "PARAMS" :caller "slip-params")
    (slip-rest-params)
    false))

(defn slip-def []
  (if (match-terminal :content "DEF" :caller "slip-def")
    (if (match-terminal :type :identifier :caller "slip-def")
      (if (slip-params)
        (slip-rest-list)
        false)
      false)
    false))

(defn slip-set []
  (if (match-terminal :content "SET" :caller "slip-set")
    (if (match-terminal :type :identifier :caller "slip-set")
      (if (slip-expression)
        (match-terminal :content ")" :caller "slip-set")
        false)
      false)
    false))

(defn slip-vector []
  (if (match-terminal :content "VECTOR" :caller "slip-vector")
    (if (match-terminal :type :identifier :caller "slip-vector")
      (if (match-terminal :type :integer :caller "slip-vector")
        (match-terminal :content ")" :caller "slip-vector")
        false)
      false)
    false))

(defn slip-len []
  (if (match-terminal :content "LEN" :caller "slip-len")
    (if (match-terminal :type :identifier :caller "slip-len")
      (match-terminal :content ")" :caller "slip-len")
      false)
    false))

(defn slip-vec-get []
  (if (match-terminal :content "VECGET" :caller "slip-vec-get")
    (if (match-terminal :type :identifier :caller "slip-vec-get")
      (if (slip-expression)
        (match-terminal :content ")" :caller "slip-vec-get")
        false)
      false)
    false))

(defn slip-vec-set []
  (if (match-terminal :content "VECSET" :caller "slip-vec-set")
    (if (match-terminal :type :identifier :caller "slip-vec-set")
      (if (slip-expression)
        (if (slip-expression)
          (match-terminal :content ")" :caller "slip-vec-set")
          false)
        false)
      false)
    false))

(defn slip-label []
  (if (match-terminal :content "#" :caller "slip-label")
    (if (match-terminal :type :identifier :caller "slip-label")
      (if (slip-expression)
        (match-terminal :content ")" :caller "slip-label")
        false)
      false)
    false))

(defn slip-go-to []
  (if (match-terminal :content "GOTO" :caller "slip-go-to")
    (if (match-terminal :type :identifier :caller "slip-go-to")
      (match-terminal :content ")" :caller "slip-go-to")
      false)
    false))

(defn slip-if-go-to []
  (if (match-terminal :content "IFGOTO" :caller "slip-if-go-to")
    (if (slip-expression)
      (if (match-terminal :type :identifier :caller "slip-if-go-to")
        (match-terminal :content ")" :caller "slip-if-go-to")
        false)
      false)
    false))

(defn slip-print []
  (if (match-terminal :content "PRINT" :caller "slip-print")
    (if (slip-expression)
      (match-terminal :content ")" :caller "slip-print")
      false)
    false))

(defn slip-read []
  (if (match-terminal :content "READ" :caller "slip-read")
    (match-terminal :content ")" :caller "slip-read")
    false))

(defn slip-keyword-call []
  (if (slip-if)
    true
    (if (slip-if-else)
      true
      (if (slip-do-list)
        true
        (if (slip-for)
          true
          (if (slip-while)
            true
            (if (slip-params)
              true
              (if (slip-def)
                true
                (if (slip-set)
                  true
                  (if (slip-vector)
                    true
                    (if (slip-len)
                      true
                      (if (slip-vec-get)
                        true
                        (if (slip-vec-set)
                          true
                          (if (slip-label)
                            true
                            (if (slip-go-to)
                              true
                              (if (slip-if-go-to)
                                true
                                (if (slip-print)
                                  true
                                  (slip-read))))))))))))))))))

; Caudas de lista com dois e um elementos

(defn rest-binary []
  (if (slip-expression)
    (if (slip-expression)
      (match-terminal :content ")" :caller "rest-binary")
      false)
    false))

(defn rest-unary []
  (if (slip-expression)
    (match-terminal :content ")" :caller "rest-unary")
    false))

; Aritmética

(defn slip-add []
  (if (match-terminal :content "+" :caller "slip-add")
    (rest-binary)
    false))
(defn slip-subtract []
  (if (match-terminal :content "-" :caller "slip-subtract")
    (rest-binary)
    false))
(defn slip-multiply []
  (if (match-terminal :content "*" :caller "slip-multiply")
    (rest-binary)
    false))
(defn slip-divide []
  (if (match-terminal :content "/" :caller "slip-divide")
    (rest-binary)
    false))
(defn slip-remainder []
  (if (match-terminal :content "%" :caller "slip-remainder")
    (rest-binary)
    false))

(defn slip-arithmetic []
  (if (slip-add)
    true
    (if (slip-subtract)
      true
      (if (slip-multiply)
        true
        (if (slip-divide)
          true
          (slip-remainder))))))

; Comparação

(defn slip-equals []
  (if (match-terminal :content "=" :caller "slip-equals")
    (rest-binary)
    false))
(defn slip-not-equals []
  (if (match-terminal :content "!=" :caller "slip-not-equals")
    (rest-binary)
    false))
(defn slip-greater []
  (if (match-terminal :content ">" :caller "slip-greater")
    (rest-binary)
    false))
(defn slip-lesser []
  (if (match-terminal :content "<" :caller "slip-lesser")
    (rest-binary)
    false))
(defn slip-greater-equal []
  (if (match-terminal :content ">=" :caller "slip-greater-equal")
    (rest-binary)
    false))
(defn slip-lesser-equal []
  (if (match-terminal :content "<=" :caller "slip-lesser-equal")
    (rest-binary)
    false))

(defn slip-comparison []
  (if (slip-equals)
    true
    (if (slip-not-equals)
      true
      (if (slip-greater)
        true
        (if (slip-lesser)
          true
          (if (slip-greater-equal)
            true
            (slip-lesser-equal)))))))

; Lógica

(defn slip-and []
  (if (match-terminal :content "&&" :caller "slip-and")
    (rest-binary)
    false))
(defn slip-or []
  (if (match-terminal :content "||" :caller "slip-or")
    (rest-binary)
    false))
(defn slip-not []
  (if (match-terminal :content "!" :caller "slip-not")
    (rest-unary)
    false))

(defn slip-logical []
  (if (slip-and)
    true
    (if (slip-or)
      true
      (slip-not))))

; Expressão genérica da linguagem

(defn slip-expression []
  (if (slip-atom)
    true
    (slip-list)))

(defn slip-rest-list []
  (if (slip-expression)
    (slip-rest-list)
    (match-terminal :content ")" :caller "slip-rest-list")))

(defn slip-head-list []
  (if (match-terminal :type :identifier :caller "slip-head-list")
    true
    (slip-list)))

(defn slip-list []
  (if (match-terminal :content "(" :caller "slip-list")
    (if (slip-keyword-call)
      true
      (if (slip-arithmetic)
        true
        (if (slip-logical)
          true
          (if (slip-comparison)
            true
            (if (slip-head-list)
              (slip-rest-list)
              false)))))

    false))

(defn slip-program-end []
  (nil? (get-current-token)))

(defn slip-rest-program []
  (if (slip-expression)
    (slip-rest-program)
    (slip-program-end)))

(defn slip-program []
  (if (slip-expression)
    (slip-rest-program)
    false))


; Motor sintático
(defn init-syntactical-engine [lexical-token-queue]
  (apply (syntactical-queue :push) lexical-token-queue))

(defn run-syntactical-engine [lexical-token-queue]
  (init-syntactical-engine lexical-token-queue)
  (slip-program))
