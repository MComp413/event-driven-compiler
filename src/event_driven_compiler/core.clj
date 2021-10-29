(ns event-driven-compiler.core (:gen-class)
    (:require [event-driven-compiler.event-engine :as ngn]
              [event-driven-compiler.lexical-tokenizer :as lx-ngn]
              [clojure.data.json :as json]))

(defn is-lexical-state-successful
  []
  (let [stack (-> @lx-ngn/token-builder-automata :stack)
        state (-> @lx-ngn/token-builder-automata :state)
        event-queue @((lx-ngn/lexical-engine :queue) :ref)]
    (and (empty? stack) (not= state :error) (empty? event-queue))))

(defn -main
  [filename output-filename]
  (((lx-ngn/lexical-engine :queue) :push) (ngn/new-event :start 0 filename))
  (spit (str "res/lexical_tokens/" output-filename) "")
  ((lx-ngn/lexical-engine :run))
  (spit (str "res/lexical_tokens/" output-filename)
        (json/write-str @lx-ngn/output-tokens)
        :append true)
  (println (str "remaning stack: " (-> @lx-ngn/token-builder-automata :stack)))
  (println (str "remaining events: " @((lx-ngn/lexical-engine :queue) :ref)))
  (println (str "Arquivo lexicamente aceito: " (is-lexical-state-successful))))
