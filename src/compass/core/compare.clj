(ns compass.core.compare
  (:import (clojure.lang IPersistentMap IPersistentVector)))

(defn- dispatch [left right]
  (let [left-type (type left)
        right-type (type right)]
    (cond
      ; TODO should we handle nil separately?!
      (= left-type right-type) left-type
      :else :mixed)))

(defmulti compare-to dispatch)

; TODO give them better names!
(defn less-than? [left right]
  (neg? (compare-to left right)))

(defn less-than-or-equal? [left right]
  (let [result (compare-to left right)]
    (or (neg? result) (zero? result))))

(defn equal? [left right]
  (zero? (compare-to left right)))

(defn greater-than-or-equal? [left right]
  (let [result (compare-to left right)]
    (or (zero? result) (pos? result))))

(defn greater-than? [left right]
  (pos? (compare-to left right)))

(defn- compare-lex
  "Compare two vectors lexicographically"
  [comparator left right]
  (let [left-size (count left)
        right-size (count right)]
    (if (not= left-size right-size)
      (compare left-size right-size)
      (let [pairs (map vector left right)]
        (loop [[[l r] & rest] pairs]
        (let [result (comparator l r)]
          (cond
            (not= result 0) result
            (empty? rest) 0
            :else (recur rest))))))))

(defn- compare-chain
  "Comparator that delegates to a chain of comparators, using the next in case of a tie"
  [chain left right]
  (loop [[comparator & rest] chain]
    (let [result (comparator left right)]
      (cond
        (not= result 0) result
        (empty? rest) 0
        :else (recur rest)))))

(defn- compare-pred
  "Comparator based on single-argument predicate"
  [pred]
  (fn [left right]
    (compare (pred left) (pred right))))

(defmethod compare-to IPersistentVector [left right]
  (compare-lex compare-to left right))

(defmethod compare-to IPersistentMap [left right]
  (compare-to (vec (sort-by key compare-to (vec left)))
              (vec (sort-by key compare-to (vec right)))))

(defmethod compare-to :mixed [left right]
  ; based on JSON type names (in order): array, boolean, number, object, string
  ; TODO nil doesn't belong here
  (let [comparators (map compare-pred [nil? vector? boolean? number? map? string?])]
    ; compare in reverse because we need true before false
    (compare-chain comparators right left)))

(defmethod compare-to :default [left right]
  (compare left right))

