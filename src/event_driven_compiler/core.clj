(ns event-driven-compiler.core
  (:gen-class)
  (:require [event-driven-compiler.event-engine :as ngn]
            [event-driven-compiler.lexical-tokenizer :as lx-ngn]
            [event-driven-compiler.syntactical-recognizer :as stx-ngn]
            [event-driven-compiler.debugger :as dbg]
            [clojure.data.json :as json]))

(defn is-lexical-state-successful
  []
  (let [stack (-> @lx-ngn/token-builder-automata :stack)
        state (-> @lx-ngn/token-builder-automata :state)
        event-queue @((lx-ngn/lexical-engine :queue) :ref)]
    (and (empty? stack) (not= state :error) (empty? event-queue))))

(defn is-syntactical-state-successful
  []
  (let [event-queue @(stx-ngn/syntactical-queue :ref)]
    (empty? event-queue)))

(defn token-map-to-token [token-map]
  (lx-ngn/new-token (keyword (token-map :type)) (token-map :content)))

(defn do-syntactical-recognition
  [filename]
  (let [tokens-json (slurp filename)
        token-maps (json/read-json tokens-json)
        tokens (map token-map-to-token token-maps)
        result (stx-ngn/run-syntactical-engine tokens)]
    (dbg/dbg-println (str "Remaining events: " @(stx-ngn/syntactical-queue :ref)))
    (println (str "Syntactically acceptable file: " result))))

(defn do-lexical-recognition
  [filename output-filename]
  (((lx-ngn/lexical-engine :queue) :push) (ngn/new-event :start 0 filename))
  (spit output-filename "")
  ((lx-ngn/lexical-engine :run))
  (spit output-filename
        (json/write-str @lx-ngn/output-tokens)
        :append true)
  (dbg/dbg-println (str "Remaning stack: " (-> @lx-ngn/token-builder-automata :stack)))
  (dbg/dbg-println (str "Remaining events: " @((lx-ngn/lexical-engine :queue) :ref)))
  (println (str "Lexically acceptable file: " (is-lexical-state-successful))))

(defn do-full-recognition
  [filename]
  (((lx-ngn/lexical-engine :queue) :push) (ngn/new-event :start 0 filename))
  ((lx-ngn/lexical-engine :run))
  (println (str "Lexically acceptable file: " (is-lexical-state-successful)))
  (let [result (stx-ngn/run-syntactical-engine @lx-ngn/output-tokens)]
    (dbg/dbg-println (str "Remaining events: " @(stx-ngn/syntactical-queue :ref)))
    (println (str "Syntactically acceptable file: " (and result (is-syntactical-state-successful))))))

(defn get-flag-value [args-list flag]
  (let [flag-index (.indexOf args-list flag)]
    (when (and (>= flag-index 0) (< (+ 1 flag-index) (count args-list)))
      (args-list (+ 1 flag-index)))))

(defn -main
  [routine filename & others]
  (let [args (into [] others)]
    (when (.contains args "debug")
      (dosync (alter dbg/debug (constantly true))))
    (case routine
      "full"
      (do-full-recognition filename)

      "syntax"
      (do-syntactical-recognition filename)

      "lexicon"
      (let [output-filename (get-flag-value args "-o")]
        (if (not= nil output-filename)
          (do-lexical-recognition filename output-filename)
          (println "Forneca um endereco para o output na forma '-o <arquivo>.json'"))))))
