(ns compass.domain.repository)

(defprotocol DimensionRepository
  (create-dimension [this dimension])
  (read-all-dimensions [this])
  (read-dimensions [this ids])
  (read-dimension [this id])
  ; TODO use a function/patch
  (update-dimension [this dimension])
  (delete-dimension [this dimension]))

(defprotocol KeyRepository
  (create-key [this key])
  (read-all-keys [this])
  (read-keys [this ids])
  (read-key [this id])
  ; TODO use a function/patch
  (update-key [this key])
  (delete-key [this key]))

(defprotocol ValueRepository
  (create-value [this key value])
  (read-all-values [this key])
  ; TODO use a function/patch
  (update-value [this key value])
  (delete-value [this key value]))
