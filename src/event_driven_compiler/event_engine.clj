(ns event-driven-compiler.event-engine (:gen-class)
    (:require [clojure.string :as string])
    (:require [event-driven-compiler.event-queue :as q]))

(defrecord Event
           [type timestamp data])

(defn new-event [type timestamp data]
  (Event. type timestamp data))

(defn handle-start-event [event]
  (println event)
  (let [{file-data :file-data} (-> event :data)]
    (q/push-event
     (new-event
      :middle
      (+ (-> event :timestamp) 1)
      (string/split file-data #"\n")))))

(defn handle-middle-event [event]
  (println event)
  (q/push-event
   (new-event
    :end
    (+ (-> event :timestamp) 1)
    "end")))

(defn handle-end-event [event]
  (println event)
  nil)

(defn select-event-handler
  "Seleciona uma função para lidar com um evento, baseando-se no tipo recebido"
  [event-type]
  (case event-type
    :start handle-start-event
    :middle handle-middle-event
    :end handle-end-event
    handle-start-event))

(defn handle-event
  [event]
  ((select-event-handler (-> event :type)) event))

(defn run-engine []
  (loop [continue? true]
    (cond continue?
          (let [next-event (q/peek-event)]
            (println (str "tamanho da fila: " (count @q/queue)))
            (handle-event next-event)
            (q/pop-event)
            (recur (not-empty @q/queue))))))