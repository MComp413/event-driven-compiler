(ns event-driven-compiler.core
  (:require [event-driven-compiler.event-queue :as q])
  (:require [event-driven-compiler.event-engine :as ngn]))

(defn -main
  [filename]
  (let [file-data (slurp filename)]
    (q/push-event (ngn/new-event :start 0 {:file-data file-data}))
    (ngn/run-engine)))
