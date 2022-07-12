(ns org.clojars.punit-naik.core-test
  (:require [clojure.test :refer [deftest is]]
            [org.clojars.punit-naik.core :as core]))

(deftest init-test
  (is (= {:height 600
          :width 600
          :background "transparent"
          :config {:view {:stroke "transparent"}
                   :mark {:cursor "pointer"}}}
         (core/init))))

(deftest axis-data-test
  (is (= [1]
         (core/axis-data [{:x 1}] :x))))

(deftest axis-config-test
  (is (= {:x-fld-name "x",
          :x-fld-type "temporal",
          :x-fld-opts
          {:title "Time",
           :axis
           {:labelAngle -25,
            :labelOverlap false,
            :grid false,
            :titleFontSize 14,
            :labelFontSize 12,
            :tickCount 2,
            :values ["2010-01-01"]},
           :timeUnit "datemonthyear"}}
         (core/axis-config
          :x
          [{:x "2010-01-01" :y 1}]
          nil nil nil)))
  (is (= {:y-fld-name "y",
          :y-fld-type "quantitative",
          :y-fld-opts
          {:title "Count",
           :axis
           {:labelAngle 0,
            :labelOverlap false,
            :grid false,
            :titleFontSize 14,
            :labelFontSize 12,
            :tickCount 2},
           :impute {:value 0}}}
         (core/axis-config
          :y
          [{:x "2010-01-01" :y 1}]
          nil nil nil))))

(deftest gen-chart-spec-test
  (is (= {:encoding
          {:x
           {:field "x",
            :type "temporal",
            :title "Time",
            :axis
            {:labelAngle -25,
             :labelOverlap false,
             :grid false,
             :titleFontSize 14,
             :labelFontSize 12,
             :tickCount 4,
             :values ["2010-01-01" "2010-01-02" "2010-01-03" "2010-01-04"]},
            :timeUnit "datemonthyear"},
           :y
           {:field "y",
            :type "quantitative",
            :title "Count",
            :axis
            {:labelAngle 0,
             :labelOverlap false,
             :grid false,
             :titleFontSize 14,
             :labelFontSize 12,
             :tickCount 4},
            :impute {:value 0}},
           :color
           {:field "label",
            :type "nominal",
            :title "Labels",
            :legend {:orient "top"}},
           :tooltip
           [{:field "x", :title "Time", :type "temporal"}
            {:field "y", :title "Count", :type "quantitative"}
            {:field "label", :title "Labels", :type "nominal"}]},
          :transform
          [{:filter {:field "y", :valid true}}
           {:filter {:field "x", :valid true}}],
          :config {:view {:stroke "transparent"}, :mark {:cursor "pointer"}},
          :mark {:type "line", :invalid "filter", :interpolate "monotone"},
          :width 600,
          :background "transparent",
          :layer
          [{:encoding
            {:color
             {:value "transparent",
              :condition {:field "label", :selection "hover"}},
             :opacity {:condition {:selection "B", :value 1}, :value 0.05}},
            :mark
            {:strokeOpacity 0.5,
             :strokeDash [4 2],
             :type "rule",
             :point {:size 60, :filled true}},
            :selection
            {:hover
             {:nearest true,
              :type "single",
              :empty "none",
              :on "mouseover",
              :clear "mouseout"},
             :B {:type "multi", :fields ["label"], :bind "legend"}}}
           {:mark {:type "line", :invalid "filter", :interpolate "monotone"},
            :selection {:C {:type "multi", :fields ["label"], :bind "legend"}},
            :encoding
            {:opacity {:condition {:selection "C", :value 1}, :value 0.05}},
            :transform
            [{:filter {:field "y", :valid true}}
             {:filter {:field "x", :valid true}}]}],
          :id 1,
          :height 600,
          :data
          {:values
           [{:x "2010-01-01", :y 3, :label "odd"}
            {:x "2010-01-02", :y 7, :label "even"}
            {:x "2010-01-03", :y 1, :label "odd"}
            {:x "2010-01-04", :y 5, :label "even"}]}}
         (core/gen-chart-spec
          1
          [{:x "2010-01-01" :y 3 :label "odd"}
           {:x "2010-01-02" :y 7 :label "even"}
           {:x "2010-01-03" :y 1 :label "odd"}
           {:x "2010-01-04" :y 5 :label "even"}]
          nil nil nil {:stack-fld-name "label"}))))