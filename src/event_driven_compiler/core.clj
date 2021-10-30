(ns event-driven-compiler.core
  (:gen-class)
  (:require [event-driven-compiler.event-engine :as ngn]
            [event-driven-compiler.lexical-tokenizer :as lx-ngn]
            [event-driven-compiler.syntactical-recognizer :as stx-ngn]
            [clojure.data.json :as json]))

(defn is-lexical-state-successful
  []
  (let [stack (-> @lx-ngn/token-builder-automata :stack)
        state (-> @lx-ngn/token-builder-automata :state)
        event-queue @((lx-ngn/lexical-engine :queue) :ref)]
    (and (empty? stack) (not= state :error) (empty? event-queue))))

(defn token-map-to-token [token-map]
  (lx-ngn/new-token (keyword (token-map :type)) (token-map :content)))

(defn do-syntactical-recognition
  [filename]
  (let [tokens-json (slurp filename)
        token-maps (json/read-json tokens-json)
        tokens (map token-map-to-token token-maps)]
    (println (str "Arquivo sintaticamente aceito: " (stx-ngn/run-syntactical-engine tokens)))))

(defn do-lexical-recognition
  [filename output-filename]
  (((lx-ngn/lexical-engine :queue) :push) (ngn/new-event :start 0 filename))
  (spit output-filename "")
  ((lx-ngn/lexical-engine :run))
  (spit output-filename
        (json/write-str @lx-ngn/output-tokens)
        :append true)
  (println (str "remaning stack: " (-> @lx-ngn/token-builder-automata :stack)))
  (println (str "remaining events: " @((lx-ngn/lexical-engine :queue) :ref)))
  (println (str "Arquivo lexicamente aceito: " (is-lexical-state-successful))))

(defn -main
  [routine filename & [output-filename & _]]
  (case routine
    "syntax"
    (do-syntactical-recognition filename)

    "lexicon"
    (do-lexical-recognition filename output-filename)))
