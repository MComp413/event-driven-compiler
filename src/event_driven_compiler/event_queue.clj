(ns event-driven-compiler.event-queue (:gen-class))

(def queue (ref []))

(defn peek-event []
  (if (empty? @queue)
    nil
    (queue 0)))

(defn pop-event []
  (if (empty? @queue)
    nil
    (dosync
     (alter queue (fn [state]
                    (subvec state 1))))))

(defn push-event
  ([item]
   (dosync
    (alter queue
           (fn [state]
             (conj @queue item)))))
  ([item & others]
   (dosync
    (alter queue
           (fn [state]
             (into (conj @queue item) others))))))