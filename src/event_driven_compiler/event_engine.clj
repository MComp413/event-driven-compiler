(ns event-driven-compiler.event-engine (:gen-class))

(defrecord Event
           [type timestamp data])

(defn new-event [type timestamp data]
  (Event. type timestamp data))

(defn build-engine
  [queue event-handler-selector name]
  {:queue queue
   :run (fn []
          (loop [continue? true]
            (cond continue?
                  (let [next-event ((queue :peek))]
                    ((event-handler-selector next-event) next-event)
                    ((queue :pop))
                    (recur (not-empty @(queue :ref)))))))})