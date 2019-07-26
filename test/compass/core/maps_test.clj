(ns compass.core.maps-test
  (:require [clojure.test :refer :all]
            [compass.core.maps :refer :all]))

(deftest maps
  (testing "Group by"
    (testing "key"
      (is (= (group-by-val count {:a #{1 2} :b #{1} :c #{2}})
             {1 {:b #{1} :c #{2}} 2 {:a #{1 2}}}))))
  (testing "Intersection"
    (is (= (map-join {:a 1 :b 2 :c 3} {:b 2 :c 4 :d 4})
           {:b #{2} :c #{3 4}})))
  (testing "Difference"
    (is (= (map-difference {} {})
           {:only-on-left {}
            :only-on-right {}
            :in-common {}
            :differing {}}))
    (is (= (map-difference {:k :v} {})
           {:only-on-left {:k :v}
            :only-on-right {}
            :in-common {}
            :differing {}}))
    (is (= (map-difference {} {:k :v})
           {:only-on-left {}
            :only-on-right {:k :v}
            :in-common {}
            :differing {}}))
    (is (= (map-difference {:k :v} {:k :v})
           {:only-on-left {}
            :only-on-right {}
            :in-common {:k :v}
            :differing {}}))
    (is (= (map-difference {:k :v1} {:k :v2})
           {:only-on-left {}
            :only-on-right {}
            :in-common {}
            :differing {:k [:v1 :v2]}}))
    (is (= (map-difference {:a 1 :b 2 :c 3} {:b 2 :c 4 :d 4})
           {:only-on-left {:a 1}
            :only-on-right {:d 4}
            :in-common {:b 2}
            :differing {:c [3 4]}}))))
