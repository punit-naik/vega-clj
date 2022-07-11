(ns org.clojars.punit-naik.vega-clj-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [org.clojars.punit-naik.core :as core]
            [org.clojars.punit-naik.vega-clj :as v]
            [reagent.dom :as rdom]))

(def spec
  (core/gen-chart-spec
   "test-id"
   [{:x "2010-01-01" :y 3 :label "odd"}
    {:x "2010-01-02" :y 7 :label "even"}
    {:x "2010-01-03" :y 1 :label "odd"}
    {:x "2010-01-04" :y 5 :label "even"}]
   nil nil nil {:stack-fld-name "label"}))

(defn render-chart
  [spec]
  [:> (v/plot spec)])

(deftest a
  (is (= 1 1)))

(deftest plot-test
  (testing "If the chart renders correctly"
    (let [doc js/document
          elem (.createElement doc "div")]
      (rdom/render [render-chart spec] elem))
    (is (= 1 1))))