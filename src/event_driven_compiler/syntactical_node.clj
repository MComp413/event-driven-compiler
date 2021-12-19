(ns event-driven-compiler.syntactical-node
  (:gen-class))

;; atom synt
(defn atom-synt [type content]
  {:type type :content content})

;; ifelse synts
(defn if-synt [cond-synt expr-synt]
  {:type :IF :cond-synt cond-synt :expr-synt expr-synt})
(defn if-else-synt [cond-synt expr1-synt expr2-synt]
  {:type :IFELSE :cond-synt cond-synt :expr1-synt expr1-synt :expr2-synt expr2-synt})

;; loop synts
(defn while-synt [cond-synt body-synt]
  {:type :WHILE :cond-synt cond-synt :body-synt body-synt})
(defn for-synt [identif-synt start-synt step-synt stop-synt body-synt]
  {:type :FOR
   :identif-synt identif-synt
   :start-synt start-synt
   :step-synt step-synt
   :stop-synt stop-synt
   :body-synt body-synt})

;; function synt
(defn func-synt [name-synt params-synt body-synt]
  {:type :DEF
   :name-synt name-synt
   :params-synt params-synt
   :body-synt body-synt})
(defn call-synt [name-synt args-synts]
  {:type :CALL
   :name-synt name-synt
   :args-synts args-synts})
(defn params-synt [identifiers-synts]
  {:type :PARAMS
   :identifiers-synts identifiers-synts})

;; arithmetic synts
(defn arithm-synt [operator-synt first-operand-synt second-operand-synt]
  {:type :ARITHM
   :operator-synt operator-synt
   :first-operand-synt first-operand-synt
   :second-operand-synt second-operand-synt})

;; logic synts
(defn binary-logic-synt [operator-synt first-operand-synt second-operand-synt]
  {:type :BIN-LOGIC
   :operator-synt operator-synt
   :first-operand-synt first-operand-synt
   :second-operand-synt second-operand-synt})
(defn unary-logic-synt [operator-synt operand-synt]
  {:type :UN-LOGIC
   :operator-synt operator-synt
   :operand-synt operand-synt})

;; comparison synts
(defn comparison-synt [operator-synt first-operand-synt second-operand-synt]
  {:type :COMP
   :operator-synt operator-synt
   :first-operand-synt first-operand-synt
   :second-operand-synt second-operand-synt})

;; expr block synt
(defn do-synt [& expr-synts]
  {:type :DO
   :expr-synts expr-synts})

;; vars synts
(defn set-synt [identif-synt value-synt]
  {:type :SET
   :identif-synt identif-synt
   :value-synt value-synt})

;; IO synts
(defn print-synt [value-synt]
  {:type :PRINT
   :value-synt value-synt})
(defn read-synt []
  {:type :READ})

;; vector synts
(defn vector-synt [name-synt length-synt]
  {:type :VECTOR
   :name-synt name-synt
   :length-synt length-synt})
(defn vec-get-synt [name-synt index-synt]
  {:type :VECGET
   :name-synt name-synt
   :index-synt index-synt})
(defn vec-set-synt [name-synt index-synt value-synt]
  {:type :VECSET
   :name-synt name-synt
   :index-synt index-synt
   :value-synt value-synt})
(defn len-synt [name-synt]
  {:type :LEN
   :name-synt name-synt})

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

