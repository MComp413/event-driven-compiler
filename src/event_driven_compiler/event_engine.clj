(ns event-driven-compiler.event-engine (:gen-class))

(def event-types
  #{:start
    :new-atom
    :new-token
    :end})

(defrecord Event
           [type timestamp data])

(defn select-event-handler
  "Seleciona uma função para lidar com um evento, baseando-se no tipo recebido"
  [event-type]
  (case [event-type]))

(defn handle-event [event]
  ((select-event-handler (-> event :type)) event))