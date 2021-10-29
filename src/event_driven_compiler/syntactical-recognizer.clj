(ns event-driven-compiler.syntactical-recognizer
  (:require [event-driven-compiler.event-engine :as ngn]
            [event-driven-compiler.event-queue :as q]
            [event-driven-compiler.stack-automata :as stk-atm]
            [clojure.string :as string]))

; Definições auxiliares

(def grammar {:expr ["(" "PRINT" :var ")"]
              :var ["x"]})

; Estado do motor sintático
(def syntactical-queue (q/build-queue))

; Gerenciadores de evento sintático
(def syntactical-event-handlers
  {:token (fn [event] nil)})

(defn syntactical-queue-handler-selector
  [event]
  (println event)
  (syntactical-event-handlers (-> event :type)))

(def syntactical-engine (ngn/build-engine
                         syntactical-queue
                         syntactical-queue-handler-selector
                         "syntactical-engine"))
