(ns event-driven-compiler.lexical-tokenizer)

(def lexical-event-types #{:digit
                           :whitespace
                           :special
                           :character
                           :reserved-word
                           :symbol-word})