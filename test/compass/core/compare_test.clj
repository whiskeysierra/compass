(ns compass.core.compare-test
  (:use [compass.core.compare]
        [clojure.test]))

(deftest relations
  (testing "Comparing"
    (testing "mixed types"
      (is (less-than? nil []))
      (is (less-than? [] true))
      (is (less-than? true 123))
      (is (less-than? 123 {}))
      (is (less-than? {} "test"))
      (is (less-than? [] "test")))
    (testing "vectors"
      (is (less-than? [] [nil]))
      (is (less-than? [{:k "a"}] [{:k "b"}]))
      (is (less-than? [{:k "a"}] [{:l "a"}]))
      (is (less-than-or-equal? [] [nil]))
      (is (less-than-or-equal? [] []))
      (is (equal? [] []))
      (is (greater-than-or-equal? [] []))
      (is (greater-than-or-equal? [nil] []))
      (is (greater-than? [{:k "b"}] [{:k "a"}]))
      (is (greater-than? [{:l "a"}] [{:k "a"}]))
      (is (greater-than? [nil] [])))
    (testing "maps"
      (is (less-than? {:k "a"} {:k "b"}))
      (is (less-than? {} {:k "v"}))
      (is (less-than-or-equal? {} {:k "v"}))
      (is (less-than-or-equal? {} {}))
      (is (equal? {} {}))
      (is (greater-than-or-equal? {} {}))
      (is (greater-than-or-equal? {:k "v"} {}))
      (is (greater-than? {:k "v"} {}))
      (is (greater-than? {:k "b"} {:k "a"})))))
