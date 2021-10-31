(ns event-driven-compiler.lexical-tokenizer
  (:gen-class)
  (:require [event-driven-compiler.event-engine :as ngn]
            [event-driven-compiler.event-queue :as q]
            [event-driven-compiler.stack-automata :as stk-atm]))

; Definição de tokens e símbolos/palavras da linguagem
(def token-types #{:reserved
                   :identifier
                   :integer
                   :string
                   :operator
                   :symbol})

(defrecord Token [type content])
(defn new-token [type content] (Token. type content))

; Estrutura de dados para output do aoutômato
(def output-tokens (ref []))

; Definições auxiliares
(def single-char-operator-set #{"+" "-" "*" "/" "!" "=" ">" "<"})
(defn is-single-char-operator?
  [token-content]
  (contains? single-char-operator-set token-content))

(def ambiguous-char-operator-set #{"!" ">" "<"})
(defn is-ambiguous-char-operator?
  [token-content]
  (contains? ambiguous-char-operator-set token-content))

(def multi-char-operator-set #{"&&" "||" "!=" ">=" "<="})
(defn is-multi-char-operator?
  [token-content]
  (contains? multi-char-operator-set token-content))

(def reserved-word-set #{"DO" "DEF" "PARAMS" "SET" "IF" "IFELSE"
                         "PRINT" "READ" "VECTOR" "VECSET" "VECGET"
                         "GOTO" "IFGOTO" "FOR" "WHILE" "TRUE" "FALSE"
                         "#"})
(defn is-reserved-word?
  [token-content]
  (contains? reserved-word-set token-content))

(def blank-characters-regex #"[\s\n\r\t,]")
(defn is-whitespace-char?
  [character]
  (not-empty (re-seq blank-characters-regex (str character))))

(def symbol-characters-regex #"[\(\)]")
(defn is-symbol-char?
  [character]
  (not-empty (re-seq symbol-characters-regex (str character))))

(def word-character-regex #"[A-Za-z]")
(defn is-word-char?
  [character]
  (not-empty (re-seq word-character-regex (str character))))

(def digit-character-regex #"[0-9]")
(defn is-digit-char?
  [character]
  (not-empty (re-seq digit-character-regex (str character))))

(defn get-char-category [character]
  (cond (is-word-char? character) :word-char
        (is-digit-char? character) :digit-char
        (is-whitespace-char? character) :whitespace
        (is-symbol-char? character) :symbol
        :else :special-char))

; Estado do motor léxico
(def lexical-queue (q/build-queue))


; Autômato de tokens léxicos
(def token-builder-transitions
  {:empty (fn [stack event]
            (let [next-char (-> event :data)
                  char-type (get-char-category next-char)]
              (case char-type
                :word-char
                (stk-atm/new-automaton-state :word
                                             (str stack next-char)
                                             token-builder-transitions
                                             [])
                :digit-char
                (stk-atm/new-automaton-state :number
                                             (str stack next-char)
                                             token-builder-transitions
                                             [])
                :whitespace
                (stk-atm/new-automaton-state :empty
                                             ""
                                             token-builder-transitions
                                             [])
                :special-char
                (stk-atm/new-automaton-state (case (str next-char)
                                               "\"" :string
                                               ";" :comment
                                               :special)
                                             (str stack next-char)
                                             token-builder-transitions
                                             [])
                :symbol
                (stk-atm/new-automaton-state :empty
                                             ""
                                             token-builder-transitions
                                             [(ngn/new-event :token
                                                             (+ 1 (-> event :timestamp))
                                                             (new-token :symbol
                                                                        (str next-char)))])

                (stk-atm/new-automaton-state :error
                                             stack
                                             token-builder-transitions
                                             []))))
   :word (fn [stack event]
           (let [next-char (-> event :data)
                 char-type (get-char-category next-char)]
             (case char-type
               (:word-char :digit-char)
               (stk-atm/new-automaton-state :word
                                            (str stack next-char)
                                            token-builder-transitions
                                            [])
               :whitespace
               (stk-atm/new-automaton-state :empty
                                            ""
                                            token-builder-transitions
                                            [(ngn/new-event :token
                                                            (+ 1 (-> event :timestamp))
                                                            (new-token
                                                             (if (is-reserved-word? stack)
                                                               :reserved
                                                               :identifier)
                                                             stack))])
               :special-char
               (stk-atm/new-automaton-state (case (str next-char)
                                              "\"" :string
                                              ";" :comment
                                              :special)
                                            (str next-char)
                                            token-builder-transitions
                                            [(ngn/new-event :token
                                                            (+ 1 (-> event :timestamp))
                                                            (new-token
                                                             (if (is-reserved-word? stack)
                                                               :reserved
                                                               :identifier)
                                                             stack))])
               :symbol
               (stk-atm/new-automaton-state :empty
                                            ""
                                            token-builder-transitions
                                            [(ngn/new-event :token
                                                            (+ 1 (-> event :timestamp))
                                                            (new-token
                                                             (if (is-reserved-word? stack)
                                                               :reserved
                                                               :identifier)
                                                             stack))
                                             (ngn/new-event :token
                                                            (+ 1 (-> event :timestamp))
                                                            (new-token :symbol
                                                                       (str next-char)))])

               (stk-atm/new-automaton-state :error
                                            stack
                                            token-builder-transitions
                                            []))))
   :number (fn [stack event]
             (let [next-char (-> event :data)
                   char-type (get-char-category next-char)]
               (case char-type
                 :word-char
                 (stk-atm/new-automaton-state :error
                                              stack
                                              token-builder-transitions
                                              [])
                 :digit-char
                 (stk-atm/new-automaton-state :number
                                              (str stack next-char)
                                              token-builder-transitions
                                              [])
                 :special-char
                 (stk-atm/new-automaton-state (case (str next-char)
                                                "\"" :string
                                                ";" :comment
                                                :special)
                                              (str next-char)
                                              token-builder-transitions
                                              [(ngn/new-event :token
                                                              (+ 1 (-> event :timestamp))
                                                              (new-token :integer stack))])
                 :whitespace
                 (stk-atm/new-automaton-state :empty
                                              ""
                                              token-builder-transitions
                                              [(ngn/new-event :token
                                                              (+ 1 (-> event :timestamp))
                                                              (new-token :integer stack))])
                 :symbol
                 (stk-atm/new-automaton-state :empty
                                              ""
                                              token-builder-transitions
                                              [(ngn/new-event :token
                                                              (+ 1 (-> event :timestamp))
                                                              (new-token :integer stack))
                                               (ngn/new-event :token
                                                              (+ 1 (-> event :timestamp))
                                                              (new-token :symbol
                                                                         (str next-char)))])

                 (stk-atm/new-automaton-state :error
                                              stack
                                              token-builder-transitions
                                              []))))
   :special (fn [stack event]
              (let [next-char (-> event :data)
                    char-type (get-char-category next-char)]
                (case char-type
                  :word-char
                  (stk-atm/new-automaton-state :word
                                               (str next-char)
                                               token-builder-transitions
                                               [(ngn/new-event :token
                                                               (+ 1 (-> event :timestamp))
                                                               (new-token :operator stack))])
                  :digit-char
                  (stk-atm/new-automaton-state :number
                                               (str next-char)
                                               token-builder-transitions
                                               [(ngn/new-event :token
                                                               (+ 1 (-> event :timestamp))
                                                               (new-token :operator stack))])
                  :special-char
                  (cond
                    (is-ambiguous-char-operator? stack)
                    (stk-atm/new-automaton-state :special
                                                 (str stack next-char)
                                                 token-builder-transitions
                                                 [])

                    (is-single-char-operator? stack)
                    (stk-atm/new-automaton-state (case (str next-char)
                                                   "\"" :string
                                                   ";" :comment
                                                   :special)
                                                 (str next-char)
                                                 token-builder-transitions
                                                 [(ngn/new-event :token
                                                                 (+ 1 (-> event :timestamp))
                                                                 (new-token :operator stack))])

                    (is-multi-char-operator? (str stack next-char))
                    (stk-atm/new-automaton-state :empty
                                                 ""
                                                 token-builder-transitions
                                                 [(ngn/new-event :token
                                                                 (+ 1 (-> event :timestamp))
                                                                 (new-token
                                                                  :operator
                                                                  (str stack next-char)))]))
                  :whitespace
                  (stk-atm/new-automaton-state :empty
                                               ""
                                               token-builder-transitions
                                               [(ngn/new-event :token
                                                               (+ 1 (-> event :timestamp))
                                                               (new-token :operator stack))])
                  :symbol
                  (stk-atm/new-automaton-state :empty
                                               ""
                                               token-builder-transitions
                                               [(ngn/new-event :token
                                                               (+ 1 (-> event :timestamp))
                                                               (new-token :operator stack))
                                                (ngn/new-event :token
                                                               (+ 1 (-> event :timestamp))
                                                               (new-token :symbol
                                                                          (str next-char)))]))))

   :comment (fn [_ event]
              (let [next-char (-> event :data)]
                (case (str next-char)
                  "\n"
                  (stk-atm/new-automaton-state :empty
                                               ""
                                               token-builder-transitions
                                               [])
                  (stk-atm/new-automaton-state :comment
                                               ""
                                               token-builder-transitions
                                               []))))
   :string (fn [stack event]
             (let [next-char (-> event :data)]
               (case (str next-char)
                 "\""
                 (stk-atm/new-automaton-state :empty
                                              ""
                                              token-builder-transitions
                                              [(ngn/new-event :token
                                                              (+ 1 (-> event :timestamp))
                                                              (new-token :string (str stack next-char)))])
                 (stk-atm/new-automaton-state :string
                                              (str stack next-char)
                                              token-builder-transitions
                                              []))))})

(defn get-next-token-builder-state
  [state-type stack event]
  ((token-builder-transitions state-type) stack event))

(def token-builder-automata
  (stk-atm/create-stack-automaton
   :empty
   ""
   token-builder-transitions))

; Gerenciadores de evento léxico
(def lexical-event-handlers
  {:start (fn [event]
            ((lexical-queue :push)
             (ngn/new-event :file
                            (+ (-> event :timestamp) 1)
                            (str (slurp (-> event :data) :encoding "UTF-8") "\n"))))
   :file (fn [event]
           (apply
            (lexical-queue :push)
            (for [character (seq (-> event :data))]
              (ngn/new-event :character
                             (+ (-> event :timestamp) 1)
                             character))))
   :character (fn [event]
                (let [state @token-builder-automata
                      next-state (get-next-token-builder-state
                                  (-> state :state-type)
                                  (-> state :stack)
                                  event)
                      events-to-push (-> next-state :outgoing-events)]
                  (dosync
                   (alter
                    token-builder-automata
                    (constantly next-state)))
                  (apply
                   (lexical-queue :push)
                   events-to-push)))
   :token (fn [event]
            (dosync (alter output-tokens (fn [state] (conj state (into {} (-> event :data)))))))})

(defn lexical-event-handler-selector
  [event]
  (println event)
  (lexical-event-handlers (-> event :type)))

(def lexical-engine
  (ngn/build-engine
   lexical-queue
   lexical-event-handler-selector
   "lexical-engine"))