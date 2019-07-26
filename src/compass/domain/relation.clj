(ns compass.domain.relation
  (:require [compass.core.compare :refer :all]
            [clojure.string :refer [starts-with?]]))

(defprotocol Relation
  (evaluate [_ configured requested]))

(defrecord ^{:private true} LessThan [id title description]
  Relation
  (evaluate [_ configured requested]
    (less-than? requested configured)))

(defrecord ^{:private true} LessThanOrEqual [id title description]
  Relation
  (evaluate [_ configured requested]
    (less-than-or-equal? requested configured)))

(defrecord ^{:private true} Equality [id title description]
  Relation
  (evaluate [_ configured requested]
    (equal? configured requested)))

(defrecord ^{:private true} GreaterThanOrEqual [id title description]
  Relation
  (evaluate [_ configured requested]
    (greater-than-or-equal? requested configured)))

(defrecord ^{:private true} GreaterThan [id title description]
  Relation
  (evaluate [_ configured requested]
    (greater-than? requested configured)))

(defrecord ^{:private true} PrefixMatch [id title description]
  Relation
  (evaluate [_ configured requested]
    (starts-with? (str requested) (str configured))))

; TODO potential memory leak!
(def ^{:private true} re-compile (memoize re-pattern))

(defrecord ^{:private true} RegularExpression [id title description]
  Relation
  (evaluate [_ configured requested]
    (re-matches (re-compile configured) requested)))

(defmulti rel identity)

(defmethod rel := [id]
  (->Equality
    id
    "Equality"
    "Matches values where the requested dimension values are equal to the configured ones."))

(defmethod rel :> [id]
  (->GreaterThan
    id
    "Greater than"
    "Matches values where the requested dimension values is strictly greater than the configured one."))

(defmethod rel :>= [id]
  (->GreaterThanOrEqual
    id
    "Greater than or equal"
    "Matches values where the requested dimension values is greater than or equal to the configured one."))

(defmethod rel :< [id]
  (->LessThan
    id
    "Less than"
    "Matches values where the requested dimension values is strictly less than the configured one."))

(defmethod rel :<= [id]
  (->LessThanOrEqual
    id
    "Less than or equal"
    "Matches values where the requested dimension values is less than or equal to the configured one."))

(defmethod rel (keyword "^") [id]
  (->PrefixMatch
    id
    "Prefix match"
    "Matches values where the requested dimension values shares the longest prefix with the configured one.
    Prefix matching is useful for data structures that have a natural hierarchy, including but not limited to
    locales, geohashes and IP subnet masks."))


(defmethod rel (keyword "~") [id]
  (->RegularExpression
    id
    "Regular expression"
    "Matches values where the requested dimension values matches the configured regular expression."))
