(ns org.clojars.punit-naik.chart-actions-test
  (:require [clojure.test :refer [deftest is]]
            [org.clojars.punit-naik.chart-actions :as chart-actions]))

(deftest add-tooltip-test
  (is (= {:encoding {:tooltip [{:field "x" :title "Time" :type "temporal"}
                               {:field "y" :title "Count" :type "quantitative"}]}}
         (chart-actions/add-tooltip
          {}
          {:x-fld-name "x"
           :x-fld-title "Time"
           :x-fld-type "temporal"}
          {:y-fld-name "y"
           :y-fld-title "Count"
           :y-fld-type "quantitative"}
          nil))))

(deftest increase-hover-area-test
  (is (= {:selection {:hover {:nearest true :type "single" :empty "none" :on "mouseover" :clear "mouseout"}}}
         (chart-actions/increase-hover-area {}))))

(deftest select-on-click-test
  (is (= {:selection {:test-select {:type "multi" :fields ["label"] :bind "legend"}}
          :encoding {:opacity {:condition {:selection "test-select" :value 1} :value 0.05}}}
         (chart-actions/select-on-click
          {}
          {:select-fld "label"
           :select-type "multi"
           :select-bind "legend"
           :select-name :test-select}))))

(deftest zoom-on-scroll-test
  (is (= {:selection {:grid {:type "interval" :bind "scales"}}}
         (chart-actions/zoom-on-scroll {}))))