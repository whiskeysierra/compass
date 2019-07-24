(ns compass.domain.relation
  (:require [compass.core.compare :refer :all]
            [clojure.string :refer [starts-with?]]))

(defprotocol Relation
  (evaluate [_ left right]))

(defrecord ^{:private true} LessThan [id title description]
  Relation
  (evaluate [_ left right]
    (less-than? left right)))

(defrecord ^{:private true} LessThanOrEqual [id title description]
  Relation
  (evaluate [_ left right]
    (less-than-or-equal? left right)))

(defrecord ^{:private true} Equality [id title description]
  Relation
  (evaluate [_ left right]
    (equal? left right)))

(defrecord ^{:private true} GreaterThanOrEqual [id title description]
  Relation
  (evaluate [_ left right]
    (greater-than-or-equal? left right)))

(defrecord ^{:private true} GreaterThan [id title description]
  Relation
  (evaluate [_ left right]
    (greater-than? left right)))

(defrecord ^{:private true} PrefixMatch [id title description]
  Relation
  (evaluate [_ left right]
    (starts-with? (str right) (str left))))

; TODO potential memory leak!
(def ^{:private true} re-compile (memoize re-pattern))

(defrecord ^{:private true} RegularExpression [id title description]
  Relation
  (evaluate [_ left right]
    (re-matches (re-compile left) right)))

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
