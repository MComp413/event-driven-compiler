(ns event-driven-compiler.syntactical-recognizer
  (:gen-class)
  (:require [event-driven-compiler.event-queue :as q]
            [clojure.data.json :as json]))

; Estado do motor sintático
(def syntactical-queue (q/build-queue))

(defn get-current-token []
  ((syntactical-queue :peek)))

(defn get-next-token []
  ((syntactical-queue :pop))
  ((syntactical-queue :peek)))

; Funções de reconhecimento recursivas
; - estratégia descendente

(defn match-terminal
  [& {:keys [type content] :or {type nil, content nil}}]
  (let [current-token (get-current-token)]
    (cond
      (not= content nil)
      (if (= content (-> current-token :content))
        (do
          (get-next-token)
          true)
        false)

      (not= type nil)
      (if (= type (-> current-token :type))
        (do
          (get-next-token)
          true)
        false)

      :else
      false)))

(declare slip-list)

(defn slip-list-head []
  (println (str "slip-list-head: " (json/write-str (get-current-token))))
  (if (or
       (match-terminal :type :operator)
       (match-terminal :type :reserved)
       (match-terminal :type :identifier))
    true
    (slip-list)))

(defn slip-atom []
  (println (str "slip-atom: " (json/write-str (get-current-token))))
  (or
   (match-terminal :type :integer)
   (match-terminal :type :string)
   (match-terminal :type :identifier)))

(defn slip-expression []
  (println (str "slip-expression: " (json/write-str (get-current-token))))
  (if (slip-atom)
    true
    (slip-list)))

(defn slip-rest-list []
  (println (str "slip-rest-list: " (json/write-str (get-current-token))))
  (if (slip-expression)
    (slip-rest-list)
    (match-terminal :content ")")))

(defn slip-list []
  (println (str "slip-list: " (json/write-str (get-current-token))))
  (if (match-terminal :content "(")
    (if (slip-list-head)
      (slip-rest-list)
      false)
    false))

(defn slip-program-end []
  (println (str "slip-program-end: " (json/write-str (get-current-token))))
  (nil? (get-current-token)))

(defn slip-rest-program []
  (println (str "slip-rest-program: " (json/write-str (get-current-token))))
  (if (slip-expression)
    (slip-rest-program)
    (slip-program-end)))

(defn slip-program []
  (println (str "slip-program: " (json/write-str (get-current-token))))
  (if (slip-expression)
    (slip-rest-program)
    false))


; Motor sintático
(defn init-syntactical-engine [lexical-token-queue]
  (apply (syntactical-queue :push) lexical-token-queue))

(defn run-syntactical-engine [lexical-token-queue]
  (init-syntactical-engine lexical-token-queue)
  (slip-program))

; Definições auxiliares
;; (def grammar {:expr ["(" "PRINT" :var ")"]
;;               :var ["x"]})



; Gerenciadores de evento sintático
;; (def syntactical-event-handlers
;;   {:token (fn [event] nil)})

;; (defn syntactical-queue-handler-selector
;;   [event]
;;   (println event)
;;   (syntactical-event-handlers (-> event :type)))

;; (def syntactical-engine (ngn/build-engine
;;                          syntactical-queue
;;                          syntactical-queue-handler-selector
;;                          "syntactical-engine"))
