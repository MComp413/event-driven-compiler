(ns event-driven-compiler.core
  (:require [event-driven-compiler.event-engine :as ngn])
  (:require [event-driven-compiler.test-engine :as test-ngn]))

(defn -main
  [filename]
  (let [file-data (slurp filename)]
    (((test-ngn/test-engine :queue) :push) (ngn/new-event :start 0 file-data))
    (println ((test-ngn/test-engine :queue) :ref))
    ((test-ngn/test-engine :run))))
