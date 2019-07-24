(ns compass.infrastructure.memory
  (:require [compass.domain.repository :as spi]))

(deftype InMemoryDimensionRepository [dimensions]
  spi/DimensionRepository
  (create-dimension [_ dimension]
    (swap! dimensions assoc (:id dimension) dimension))
  (read-all-dimensions [_]
    (sort-by :id (vals @dimensions)))
  (read-dimensions [_ ids]
    (select-keys @dimensions ids))
  (read-dimension [_ id]
    (@dimensions id))
  (update-dimension [_ dimension]
    (swap! dimensions assoc (:id dimension) dimension)
    dimension)
  (delete-dimension [_ dimension]
    (swap! dimensions dissoc (:id dimension))))

(deftype InMemoryKeyRepository [keys]
  spi/KeyRepository
  (create-key [_ key]
    (swap! keys assoc (:id key) key))
  (read-all-keys [_]
    (sort-by :id (vals @keys)))
  (read-keys [_ ids]
    (select-keys @keys ids))
  (read-key [_ id]
    (@keys id))
  (update-key [_ key]
    (swap! keys assoc (:id key) key)
    key)
  (delete-key [_ key]
    (swap! keys dissoc (:id key))))

(defn dimension-repository []
  (InMemoryDimensionRepository. (atom {})))

(defn key-repository []
  (InMemoryKeyRepository. (atom {})))
