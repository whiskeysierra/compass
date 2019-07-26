(ns compass.domain.match-test
  (:require [clojure.test :refer :all]
            [compass.domain.relation :refer :all]
            [compass.domain.match :refer :all]))

(def relations
  {:after (rel :>=)
   :before (rel :<=)
   :country (rel :=)
   :postal-code (rel :=)
   :locale (rel (keyword "^"))
   :email (rel (keyword "~"))})

(def values
  [{:dimensions {:country "CH" :before "2014-01-01T00:00:00Z"} :value ""}
   {:dimensions {:country "CH" :before "2015-01-01T00:00:00Z"} :value ""}
   {:dimensions {:country "CH" :after "2018-01-01T00:00:00Z"} :value ""}
   {:dimensions {:country "CH" :after "2017-01-01T00:00:00Z"} :value ""}
   {:dimensions {:country "DE" :after "2018-01-01T00:00:00Z"} :value ""}
   {:dimensions {:country "DE" :after "2017-01-01T00:00:00Z"} :value ""}
   {:dimensions {:country "DE" :postal-code "27498"} :value ""}
   {:dimensions {:country "CH"} :value ""}
   {:dimensions {:country "DE"} :value ""}
   {:dimensions {:after "2017-01-01T00:00:00Z"} :value ""}
   {:dimensions {:locale "de-DE"} :value ""}
   {:dimensions {:locale "en-DE"} :value ""}
   {:dimensions {:locale "de"} :value ""}
   {:dimensions {:email ".*@zalando\\.de"} :value ""}
   {:dimensions {:email ".*@github\\.com"} :value ""}
   {:value ""}])

(deftest match
  (testing "equality"
    (is (= (select values {:country "DE"} relations)
           [{:dimensions {:country "DE"} :value ""}
            {:value ""}]))
    (is (= (select values {:country "UK"} relations)
           [{:value ""}]))
    (is (= (select values {:country "CH" :before "2013-12-20T11:47:19Z"} relations)
           [{:dimensions {:country "CH" :before "2014-01-01T00:00:00Z"} :value ""}
            {:dimensions {:country "CH" :before "2015-01-01T00:00:00Z"} :value ""}
            {:dimensions {:country "CH"} :value ""}
            {:value ""}]))
    (is (= (select values {:country "CH" :before "2014-01-01T00:00:00Z"} relations)
           [{:dimensions {:country "CH" :before "2014-01-01T00:00:00Z"} :value ""}
            {:dimensions {:country "CH" :before "2015-01-01T00:00:00Z"} :value ""}
            {:dimensions {:country "CH"} :value ""}
            {:value ""}]))
    (is (= (select values {:country "CH" :after "2019-12-20T11:47:19Z"} relations)
           [{:dimensions {:country "CH" :after "2018-01-01T00:00:00Z"} :value ""}
            {:dimensions {:country "CH" :after "2017-01-01T00:00:00Z"} :value ""}
            {:dimensions {:country "CH"} :value ""}
            {:dimensions {:after "2017-01-01T00:00:00Z"} :value ""}
            {:value ""}]))
    (is (= (select values {:country "CH" :after "2018-01-01T00:00:00Z"} relations)
           [{:dimensions {:country "CH" :after "2018-01-01T00:00:00Z"} :value ""}
            {:dimensions {:country "CH" :after "2017-01-01T00:00:00Z"} :value ""}
            {:dimensions {:country "CH"} :value ""}
            {:dimensions {:after "2017-01-01T00:00:00Z"} :value ""}
            {:value ""}]))
    (is (= (select values {:locale "de-AT"} relations)
           [{:dimensions {:locale "de"} :value ""}
            {:value ""}]))
    (is (= (select values {:email "user@zalando.de"} relations)
           [{:dimensions {:email ".*@zalando\\.de"} :value ""}
            {:value ""}]))
    (is (= (select values {} relations)
           [{:value ""}]))
    (is (= (select values {:foo "bar"} (assoc relations :foo :=))
           [{:value ""}]))
    (is (= (select values {:postal-code "12345"} relations)
           [{:value ""}]))
    (is (= (select values {:country "DE" :foo :bar} (assoc relations :foo :=))
           [{:dimensions {:country "DE"} :value ""}
            {:value ""}]))))

