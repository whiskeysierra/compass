(ns compass.core.maps
  (:require [clojure.core :as core]
            [flatland.ordered.set :refer [ordered-set]]))

(defn map-kv [f map]
  (into {} (core/map f map)))

(defn map-val [f map]
  (map-kv (juxt key (comp f val)) map))

(defn group-by-kv [f map]
  (map-val #(into {} %) (group-by f map)))

(defn group-by-val [f map]
  (group-by-kv (comp f val) map))

(defn index [f coll]
  (into {} (map (juxt f identity) coll)))

(defn map-join
  "Joins two maps on keys, returning a map containing all common keys mapped to tuples of values:

  * common values, size 1
  * differing values, size 2"
  [left right]
  (let [left-inner-join  (select-keys left (keys right))
        right-inner-join (select-keys right (keys left))]
    (merge-with ordered-set left-inner-join right-inner-join)))

(defn map-difference
  "Compares two maps, returning a map with:

  * :only-on-left: map containing the entries from the left whose keys are not present on the right
  * :only-on-right: map containing the entries from the right whose keys are not present on the left
  * :in-common: map containing the entries that appear in both maps; i.e. the intersection
  * :differing: map describing keys that appear in both maps, but with different values (values are tuples of 2 values)"
  [left right]
  (let [join (map-join left right)
        {in-common 1 differing 2 :or {differing {}}} (group-by-val count join)]
    {:only-on-left (apply dissoc left (keys join))
     :only-on-right (apply dissoc right (keys join))
     :in-common (map-val first in-common)
     :differing (map-val vec differing)}))
