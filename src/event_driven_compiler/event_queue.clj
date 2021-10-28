(ns event-driven-compiler.event-queue (:gen-class))

(defn build-pop
  [queue-ref]
  (fn []
    (if (empty? @queue-ref)
      nil
      (dosync
       (alter queue-ref (fn [state]
                          (subvec state 1)))))))

(defn build-peek
  [queue-ref]
  (fn []
    (if (empty? @queue-ref)
      nil
      (queue-ref 0))))

(defn build-push
  [queue-ref]
  (fn
    ([] queue-ref)
    ([item]
     (dosync
      (alter queue-ref
             (fn [state]
               (conj state item)))))
    ([item & others]
     (dosync
      (alter queue-ref
             (fn [state]
               (into (conj state item) others)))))))

(defn build-queue
  []
  (let [new-queue (ref [])
        pop (build-pop new-queue)
        peek (build-peek new-queue)
        push (build-push new-queue)]
    {:ref new-queue
     :pop pop
     :peek peek
     :push push}))