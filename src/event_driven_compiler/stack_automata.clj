(ns event-driven-compiler.stack-automata (:gen-class))

(defrecord AutomatonState [state-type stack transitions outgoing-events])
(defn new-automaton-state [state-type stack transitions outgoing-events]
  (AutomatonState. state-type stack transitions outgoing-events))

(defn get-next-state
  [state-type stack transitions]
  (fn [event]
    ((transitions state-type) stack event)))

(defn create-stack-automaton
  [initialState
   stack
   transitions]
  (ref
   (new-automaton-state
    initialState
    stack
    transitions
    [])))