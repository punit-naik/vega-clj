(ns org.clojars.punit-naik.data-transformations-test
  (:require [clojure.test :refer [deftest is]]
            [org.clojars.punit-naik.data-transformations :as dt]))

(deftest apply-aggregate-test
  (is (= {:transform [{:aggregate [{:op "sum" :field "y" :as "y_agg"}] :groupby ["x"]}]}
         (dt/apply-aggregate
          {}
          {:fld-name "y" :grp-by-flds "x" :op "sum"}))))

(deftest do-calculation-test
  (is (= {:transform [{:calculate "2*datum.y" :as "2-times-y"}]}
         (dt/do-calculation
          {}
          {:calc-str "2*datum.y"
           :calc-fld-alias "2-times-y"}))))

(deftest apply-filter-test
  (is (= {:transform [{:filter "datum.b > 60"}]}
         (dt/apply-filter
          {}
          "datum.b > 60"))))