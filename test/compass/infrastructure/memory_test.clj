(ns compass.infrastructure.memory-test
  (:require [compass.domain.repository :as spi]
            [compass.infrastructure.memory :as impl])
  (:use [clojure.test]))

(def unit (impl/key-repository))

(deftest key-repository
  (testing "In-memory key repository"

    (testing "should create")
    (spi/create-key unit {:id "test"})

    (testing "should read by id"
      (is (= {:id "test"} (spi/read-key unit "test"))))))
