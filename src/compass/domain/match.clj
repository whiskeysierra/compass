(ns compass.domain.match
  (:require [clojure.core :as core]
            [compass.core.maps :refer :all]
            [compass.domain.relation :refer :all]))

(defn- match-dimensions [dimensions filter relations]
  "Matches dimensions against a given filter.

  * filter and dimensions are maps from dimension id to value, e.g. {:version \"1.0\"}
  * relations is a map from dimension id to relation id, e.g. {:version :=}"
  {:static true}
  (let [{unmatched :only-on-left :keys [in-common differing]} (map-difference dimensions filter)]
    (and (empty? unmatched)
         (every? (fn [[dimension value]] (evaluate (relations dimension) value value)) in-common)
         (every? (fn [[dimension [configured requested]]]
                   (evaluate (relations dimension) configured requested)) differing))))

(defn- match-value [{:keys [dimensions] :as _value} filter relations]
  (match-dimensions dimensions filter relations))

(defn select [values filter relations]
  (core/filter #(match-value % filter relations) values))
