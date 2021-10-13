(ns event-driven-compiler.syntactical-tokenizer
  (:require [event-driven-compiler.event-engine :as ngn]
            [event-driven-compiler.event-queue :as q]
            [event-driven-compiler.stack-automata :as stk-atm]
            [clojure.string :as string]))

; Estado do motor sintático

; Definições auxiliares
; Preciso de uma função que transforme uma estrutura de dados que descrava uma gramática em um
; autômato reconhecedor das formas sintáticas desta gramática.

(defn build-grammar-automata
  [grammar]
  nil)

; Autômatos de estruturas sintáticas

; Gerenciadores de evento sintático