(ns event-driven-compiler.test-engine
  (:require [event-driven-compiler.event-engine :as ngn])
  (:require [event-driven-compiler.event-queue :as q])
  (:require [clojure.string :as string]))

(def test-queue (q/build-queue))

(defn handle-start-event [event]
  (println event)
  (let [file-data (-> event :data)]
    ((test-queue :push) (ngn/new-event :middle
                                       (+ (-> event :timestamp) 1)
                                       (string/split file-data #" ")))))

(defn handle-middle-event [event]
  (apply
   (test-queue :push)
   (for [word (-> event :data)]
     (ngn/new-event :end
                    (+ (-> event :timestamp) 1)
                    word))))

(defn handle-end-event [event]
  (println event)
  nil)

(def test-event-handlers
  {:start handle-start-event
   :middle handle-middle-event
   :end handle-end-event})

(defn test-event-handler-selector
  [event]
  (test-event-handlers (-> event :type)))

(def test-engine
  (ngn/build-engine
   test-queue
   test-event-handler-selector
   "test-engine"))