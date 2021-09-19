(ns event-driven-compiler.event-engine (:gen-class))
(require '[event-driven-compiler.event-queue :as q])

(defrecord Event
           [type timestamp data])

(defn new-event [type timestamp data]
  (Event. type timestamp data))

(defn handle-start-event [event]
  (println event)
  (let [{file-data :file-data} (-> event :data)]
    (println file-data)))

(defn select-event-handler
  "Seleciona uma função para lidar com um evento, baseando-se no tipo recebido"
  [event-type]
  (case event-type
    :start handle-start-event
    ;:new-atom
    ;:new-token
    :end #(%1)
    handle-start-event))

(defn handle-event
  [event]
  ((select-event-handler (-> event :type)) event))

(defn run-engine []
  (let [next-event (q/peek-event)]
    (q/pop-event)
    (handle-event next-event)))