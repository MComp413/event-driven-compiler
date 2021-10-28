(ns event-driven-compiler.core (:gen-class)
    (:require [event-driven-compiler.event-engine :as ngn]
              [event-driven-compiler.test-engine :as test-ngn]
              [event-driven-compiler.lexical-tokenizer :as lx-ngn]))

(defn is-lexical-state-successful
  []
  (let [stack (-> @lx-ngn/token-builder-automata :stack)
        state (-> @lx-ngn/token-builder-automata :state)
        event-queue @((lx-ngn/lexical-engine :queue) :ref)]
    (and (empty? stack) (not= state :error) (empty? event-queue))))

(defn -main
  [filename]
  (((lx-ngn/lexical-engine :queue) :push) (ngn/new-event :start 0 filename))
  ((lx-ngn/lexical-engine :run))
  (println (str "remaning stack: " (-> @lx-ngn/token-builder-automata :stack)))
  (println (str "remaining events: " @((lx-ngn/lexical-engine :queue) :ref)))
  (println (str "Arquivo lexicamente aceito: " (is-lexical-state-successful))))

(defn test-main
  [filename]
  (let [file-data (slurp filename :encoding "UTF-8")]
    (((test-ngn/test-engine :queue) :push) (ngn/new-event :start 0 file-data))
    (println @((test-ngn/test-engine :queue) :ref))
    ((test-ngn/test-engine :run))))
