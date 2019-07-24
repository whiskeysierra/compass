(ns compass.core.maps
  (:require [clojure.core :as core]))

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
  "Joins two maps on keys, returning a map containing all common keys mapped to sets of values:

  * common values in sets of size 1
  * differing values in sets of size 2"
  [left right]
  (let [left-inner-join  (select-keys left (keys right))
        right-inner-join (select-keys right (keys left))]
    (merge-with hash-set left-inner-join right-inner-join)))

(defn map-difference
  "Compares two maps, returning a map with:

  * :only-on-left: map containing the entries from the left whose keys are not present on the right
  * :only-on-right: map containing the entries from the right whose keys are not present on the left
  * :in-common: map containing the entries that appear in both maps; i.e. the intersection
  * :differing: map describing keys that appear in both maps, but with different values (values are tuples of 2 values)"
  [left right]
  (let [join (map-join left right)
        {in-common 1 differing 2} (group-by-val count join)]
    {:only-on-left (apply dissoc left (keys join))
     :only-on-right (apply dissoc right (keys join))
     :in-common (map-val first in-common)
     :differing differing}))
