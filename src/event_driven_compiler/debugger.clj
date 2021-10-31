(ns event-driven-compiler.debugger
  (:gen-class))

(def debug (ref false))

(defn dbg-println [& args]
  (when @debug
    (apply println args)))