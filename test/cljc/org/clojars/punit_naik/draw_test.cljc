(ns org.clojars.punit-naik.draw-test
  (:require [clojure.test :refer [deftest is]]
            [org.clojars.punit-naik.canvas :as c]
            [org.clojars.punit-naik.core :as core]
            [org.clojars.punit-naik.data :as d]
            [org.clojars.punit-naik.data-test :as dt]
            [org.clojars.punit-naik.draw :as draw]))

(deftest change-axis-color-test
  (is (= {:config {:axis {:gridColor "#FFFFFF"
                          :labelColor "#FFFFFF"
                          :tickColor "#FFFFFF"
                          :titleColor "#FFFFFF"}}}
         (draw/change-axis-color {} "#FFFFFF"))))

(deftest change-legend-color-test
  (is (= {:config {:legend {:labelColor "#FFFFFF"
                            :titleColor "#FFFFFF"}}}
         (draw/change-legend-color {} "#FFFFFF"))))

(deftest generate-colour-range-test
  (is (= {:domain ["Positive" "Negative" "Neutral"]
          :range ["#00FF00" "#FF0000" "#0000FF"]}
         (draw/generate-colour-range
          ["Positive" "Negative" "Neutral"]
          {"Positive" "#00FF00"
           "Negative" "#FF0000"
           "Neutral" "#0000FF"}))))

(deftest add-axes-test
  (is (= {:encoding {:x {:field "x" :type "temporal"}
                     :y {:field "y" :type "quantitative"}}}
         (draw/add-axes
          {}
          {:x-fld-name "x" :x-fld-type "temporal"
           :y-fld-name "y" :y-fld-type "quantitative"}))))

(deftest get-fld-info-test
  (is (= {:field "x" :type "temporal"}
         (draw/get-fld-info
          {:encoding {:x {:field "x" :type "temporal"}}}
          :x))))

(deftest add-dashes-test
  (is (= {:encoding {:strokeDash {:field "label" :type "nominal" :legend nil}}}
         (draw/add-dashes
          {} "label" "nominal"))))

(deftest add-opacity-test
  (is (= {:encoding {:strokeOpacity {:field "y" :type "quantitative" :legend nil}}}
         (draw/add-opacity
          {} "y" "quantitative"))))

(deftest add-rule-for-line-test
  (is (= {:layer
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
             {:filter {:field "x", :valid true}}]}]}
         (draw/add-rule-for-line
          {}
          "line"
          "label"
          "monotone"))))

(deftest stack-test
  (is (= {:encoding {:color {:field "label" :type "nominal"}}}
         (draw/stack {} {:stack-fld "label" :stack-fld-type "nominal"}))))

(deftest get-stack-fld-info-test
  (is (= {:field "label" :type "nominal"}
         (draw/get-stack-fld-info {:encoding {:color {:field "label" :type "nominal"}}}))))

(deftest get-step-size-test
  (is (= {:step 28.1 :original-value 600}
         (-> (d/add-dataset {} dt/sample-data-2)
             (c/set-width 600)
             draw/get-step-size))))

(deftest header-for-grouped-bar-chart-test
  (is (= {:labelAngle 90
          :labelPadding 0
          :labelAlign "left"}
         (draw/header-for-grouped-bar-chart nil 2 "1d"))))

(deftest line->grouped-bar-test
  (is (= {:encoding
          {:x
           {:field "label",
            :type "nominal",
            :title "",
            :legend {:orient "top"},
            :axis
            {:labels false,
             :titleFontSize 14,
             :labelOverlap false,
             :grid false,
             :labelAngle -25,
             :tickCount 4,
             :values ["2010-01-01" "2010-01-02" "2010-01-03" "2010-01-04"],
             :ticks false,
             :labelFontSize 12}},
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
             :tickCount 4}},
           :color
           {:field "label",
            :type "nominal",
            :title "Labels",
            :legend {:orient "top"}},
           :tooltip
           [{:field "x", :title "Time", :type "temporal"}
            {:field "y", :title "Count", :type "quantitative"}
            {:field "label", :title "Labels", :type "nominal"}],
           :column
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
            :timeUnit "datemonthyear",
            :spacing 0,
            :header
            {:titleFontSize 14,
             :labelOverlap false,
             :grid false,
             :labelPadding 0,
             :labelAngle 90,
             :tickCount 4,
             :values ["2010-01-01" "2010-01-02" "2010-01-03" "2010-01-04"],
             :labelAlign "left",
             :labelFontSize 12}}},
          :transform
          [{:filter {:field "y", :valid true}}
           {:filter {:field "x", :valid true}}],
          :config {:view {:stroke "transparent"}, :mark {:cursor "pointer"}},
          :mark {:type "bar", :invalid "filter", :interpolate "monotone"},
          :width {:step 150.0, :original-value 600},
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
         (draw/line->grouped-bar
          (core/gen-chart-spec
           1
           [{:x "2010-01-01" :y 3 :label "odd"}
            {:x "2010-01-02" :y 7 :label "even"}
            {:x "2010-01-03" :y 1 :label "odd"}
            {:x "2010-01-04" :y 5 :label "even"}]
           nil nil nil {:stack-fld-name "label"})
          "1d"))))

(deftest grouped-bar->line-test
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
            :timeUnit "datemonthyear",
            :header
            {:titleFontSize 14,
             :labelOverlap false,
             :grid false,
             :labelPadding 0,
             :labelAngle 90,
             :tickCount 4,
             :values ["2010-01-01" "2010-01-02" "2010-01-03" "2010-01-04"],
             :labelAlign "left",
             :labelFontSize 12}},
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
             :tickCount 4}},
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
         (draw/grouped-bar->line
          (draw/line->grouped-bar
           (core/gen-chart-spec
            1
            [{:x "2010-01-01" :y 3 :label "odd"}
             {:x "2010-01-02" :y 7 :label "even"}
             {:x "2010-01-03" :y 1 :label "odd"}
             {:x "2010-01-04" :y 5 :label "even"}]
            nil nil nil {:stack-fld-name "label"})
           "1d")))))

(deftest custom-stack-colours-test
  (is (= {:encoding
          {:color
           {:scale
            {:domain ["even" "odd"]
             :range ["#FFFFFF" "#000000"]}}}}
         (draw/custom-stack-colours
          {}
          {"even" "#FFFFFF"
           "odd" "#000000"}))))