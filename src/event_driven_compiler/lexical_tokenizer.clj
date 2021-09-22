(ns event-driven-compiler.lexical-tokenizer
  (:require [event-driven-compiler.event-engine :as ngn])
  (:require [event-driven-compiler.event-queue :as q]))

(def lexical-event-handlers
  {:digit (fn [])
   :whitespace (fn [])
   :special (fn [])
   :character (fn [])
   :reserved-word (fn [])
   :symbol-word (fn [])})

(defn lexical-event-handler-selector
  [event]
  (lexical-event-handlers (-> event :type)))

(def lexical-queue (q/build-queue))

(def lexical-engine
  (ngn/build-engine
   lexical-queue
   lexical-event-handler-selector
   "lexical-engine"))