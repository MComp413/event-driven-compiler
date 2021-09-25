(ns event-driven-compiler.lexical-tokenizer
  (:require [event-driven-compiler.event-engine :as ngn]
            [event-driven-compiler.event-queue :as q]
            [clojure.string :as string]))

; Estado do motor léxico
(def lexical-queue (q/build-queue))
(def token-builder (ref {:string "" :type :empty}))

; Definições auxiliares
(def blank-characters-regex #"[\s]")
(defn is-whitespace-char?
  [character]
  (not-empty (re-seq blank-characters-regex (str character))))


(def word-character-regex #"[A-Za-z]")
(defn is-word-char?
  [character]
  (not-empty (re-seq word-character-regex (str character))))

(def digit-character-regex #"[0-9]")
(defn is-digit-char?
  [character]
  (not-empty (re-seq digit-character-regex (str character))))


(defn get-char-category [character]
  (cond (is-word-char? character) :word-char
        (is-digit-char? character) :digit-char
        (is-whitespace-char? character) :whitespace
        :else :special-char))

;; (def ingest-char
;;   []
;;   (dosync
;;    (alter token-builder
;;           (fn [token]
;;             (if (should-append-char-to-token? character, character-type, token)
;;               (str token character)
;;               (do
;;                 ()))))))


; Gerenciadores de evento léxico
(def lexical-event-handlers
  {:start (fn [event]
            ((lexical-queue :push)
             (ngn/new-event :file
                            (+ (-> event :timestamp) 1)
                            (slurp (-> event :data)))))
   :file (fn [event]
           (apply
            (lexical-queue :push)
            (for [line (string/split (-> event :data) #"\n")]
              (ngn/new-event :line
                             (+ (-> event :timestamp) 1)
                             line))))
   :line (fn [event]
           (apply
            (lexical-queue :push)
            (for [character (seq (-> event :data))]
              (ngn/new-event (get-char-category character)
                             (+ (-> event :timestamp) 1)
                             character))))
  ;;  :character (fn [event]
  ;;               ((lexical-queue :push)
  ;;                (if (not= (-> event :data) :whitespace)
  ;;                  ((lexical-queue :push)
  ;;                   (ngn/new-event (get-char-category (-> event :data))
  ;;                                  (+ (-> event :timestamp) 1)
  ;;                                  (-> event :data)))
  ;;                  (cond (not-empty @token-builder)
  ;;                        (do
  ;;                        ;colocar evento de token na fila e esvaziar palavra
  ;;                          ((lexical-queue :push)
  ;;                           (ngn/new-event :token
  ;;                                          (+ (-> event :timestamp) 1)
  ;;                                          (str @token-builder)))
  ;;                          (dosync
  ;;                           (alter token-builder
  ;;                                  (fn [] ""))))))))
   :word-char (fn [event]
                (dosync
                 (alter token-builder
                        (fn [state]
                          (str state (-> event :data))))))
   :digit-char (fn [event])
   :special-char (fn [event])
   :whitespace (fn [event])
   :token (fn [event])})

(defn lexical-event-handler-selector
  [event]
  (lexical-event-handlers (-> event :type)))

(def lexical-engine
  (ngn/build-engine
   lexical-queue
   lexical-event-handler-selector
   "lexical-engine"))