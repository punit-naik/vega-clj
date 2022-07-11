(ns org.clojars.punit-naik.data-test
  (:require [clojure.test :refer [deftest is]]
            [org.clojars.punit-naik.data :as data]))

(defonce sample-data [{:x "a" :y 1}
                      {:x "b" :y 2}])
(defonce sample-data-2 (->> sample-data
                            (repeat 10)
                            (reduce concat)))

(deftest add-dataset-test
  (is (= {:data {:values sample-data}}
         (data/add-dataset
          {}
          sample-data))))

(deftest tick-count-test
  (is (= 2 (data/tick-count sample-data)))
  (is (= 10 (data/tick-count sample-data-2 10)))
  (is (= 20 (data/tick-count sample-data-2))))