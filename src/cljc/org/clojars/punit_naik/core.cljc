(ns org.clojars.punit-naik.core
  (:require [org.clojars.punit-naik.canvas :refer [add-mark set-backgroud set-config set-height set-width]]
            [org.clojars.punit-naik.chart-actions :refer [add-tooltip increase-hover-area select-on-click zoom-on-scroll]]
            [org.clojars.punit-naik.data :refer [add-dataset tick-count]]
            [org.clojars.punit-naik.data-transformations :refer [apply-filter]]
            [org.clojars.punit-naik.draw :refer [add-axes add-dashes add-opacity add-rule-for-line custom-stack-colours stack]]))

(defn init
  "Initialises the Vega-lite spec with default values and returns the same"
  []
  (-> {}
      (set-height 600)
      (set-width 600)
      (set-backgroud "transparent")
      (set-config {:view {:stroke "transparent"}
                   :mark {:cursor "pointer"}})))

(defn axis-data
  [data axis-fld-name]
  (map axis-fld-name data))

(defn mark-config
  [{:keys [chart-type chart-interpolate point?]
    :or {chart-type "line"
         chart-interpolate "monotone"}}]
  (cond-> {:type chart-type
           :invalid "filter"}
    (= chart-type "moving-avg") (assoc :type "line")
    (or (= chart-type "line")
        (= chart-type "moving-avg")) (assoc :interpolate chart-interpolate)
    (and (or (= chart-type "line")
             (= chart-type "moving-avg"))
         point?) (assoc :point {:filled true :size 60})))

(defmulti axis-config
  (fn [axis _ _ _ _] axis))

(defmethod axis-config :x
  [_
   data
   {:keys [x-fld-name x-fld-type x-fld-title
           x-label-angle x-title-font-size
           x-label-font-size x-label-overlap
           x-grid x-tick-count]
    :or {x-fld-name "x"
         x-fld-type "temporal"
         x-fld-title "Time"
         x-label-angle 0
         x-title-font-size 14
         x-label-font-size 12
         x-label-overlap false
         x-grid false
         x-tick-count (tick-count data)}}
   {:keys [y-fld-type]
    :or {y-fld-type "quantitative"}}
   {:keys [chart-type bucket bucket-mapper]
    :or {chart-type "line"
         bucket "1d"
         bucket-mapper {"1d" "datemonthyear"
                        "1w" "datemonthyear"
                        "1M" "monthyear"
                        "1q" "datemonthyear"
                        "1y" "year"}}}]
  (let [x-fld-temporal? (= x-fld-type "temporal")
        y-fld-temporal? (= y-fld-type "temporal")
        time-unit (if (= chart-type "moving-avg")
                    "datemonthyear"
                    (get bucket-mapper bucket))]
    {:x-fld-name x-fld-name :x-fld-type x-fld-type
     :x-fld-opts (cond-> {:title x-fld-title
                          :axis (merge {:labelAngle x-label-angle
                                        :labelOverlap x-label-overlap
                                        :grid x-grid
                                        :titleFontSize x-title-font-size
                                        :labelFontSize x-label-font-size
                                        :tickCount x-tick-count}
                                       (when (and (not= x-fld-type "temporal")
                                                  (= chart-type "bar"))
                                         {:tickOffset {:expr (str "width/" (* -2.25 x-tick-count))}}))}
                   x-fld-temporal? (assoc :timeUnit time-unit)
                   x-fld-temporal? (assoc-in [:axis :labelAngle] -25)
                   x-fld-temporal? (assoc-in [:axis :values] (->> (axis-data data (keyword x-fld-name))
                                                                  distinct
                                                                  sort))
                   y-fld-temporal? (assoc :impute {:value 0}))}))

(defmethod axis-config :y
  [_
   data
   {:keys [x-fld-type]
    :or {x-fld-type "temporal"}}
   {:keys [y-fld-name y-fld-type y-fld-title
           y-label-angle y-title-font-size
           y-label-font-size y-label-overlap
           y-grid y-tick-count]
    :or {y-fld-name "y"
         y-fld-type "quantitative"
         y-fld-title "Count"
         y-label-angle 0
         y-title-font-size 14
         y-label-font-size 12
         y-label-overlap false
         y-grid false
         y-tick-count (tick-count data)}}
   {:keys [chart-type bucket bucket-mapper]
    :or {chart-type "line"
         bucket "1d"
         bucket-mapper {"1d" "datemonthyear"
                        "1w" "datemonthyear"
                        "1M" "monthyear"
                        "1q" "datemonthyear"
                        "1y" "year"}}}]
  (let [x-fld-temporal? (= x-fld-type "temporal")
        y-fld-temporal? (= y-fld-type "temporal")
        time-unit (if (= chart-type "moving-avg")
                    "datemonthyear"
                    (get bucket-mapper bucket))]
    {:y-fld-name y-fld-name :y-fld-type y-fld-type
     :y-fld-opts (cond-> {:title y-fld-title
                          :axis {:labelAngle y-label-angle
                                 :labelOverlap y-label-overlap
                                 :grid y-grid
                                 :titleFontSize y-title-font-size
                                 :labelFontSize y-label-font-size
                                 :tickCount y-tick-count}}
                   y-fld-temporal? (assoc :timeUnit time-unit)
                   y-fld-temporal? (assoc-in [:axis :labelAngle] -25)
                   y-fld-temporal? (assoc-in [:axis :values] (->> (axis-data data (keyword y-fld-name))
                                                                  distinct
                                                                  sort))
                   x-fld-temporal? (assoc :impute {:value 0}))}))

(defn gen-chart-spec
  [id data x-fld-info y-fld-info
   {:keys [chart-type chart-interpolate tooltip?
           dash-fld-name opacity-fld-name
           increase-hover-area? zoom-on-scroll?
           dashed-lines? add-opacity? select-on-click?
           disable-legend?]
    :or {chart-type "line"
         chart-interpolate "monotone"
         dashed-lines? false
         add-opacity? false
         select-on-click? false
         dash-fld-name "stroke"
         opacity-fld-name "opacity"
         tooltip? true
         disable-legend? false}
    :as chart-info}
   {:keys [stack-fld-name stack-fld-type stack-fld-title stack-fld-opts
           custom-stack-colours-map]
    :or {stack-fld-name "label"
         stack-fld-type "nominal"
         stack-fld-title "Labels"
         stack-fld-opts {:title stack-fld-title :legend {:orient "top"}}}
    :as stack-info}]
  (let [line-chart? (or (= chart-type "moving-avg")
                        (= chart-type "line"))
        dashed-lines? (and dashed-lines?
                           line-chart?)]
    (cond-> (-> (init)
                (add-dataset data)
                (assoc :id id)
                (add-axes (merge (axis-config :x data x-fld-info y-fld-info chart-info)
                                 (axis-config :y data x-fld-info y-fld-info chart-info)))
                (add-mark (mark-config chart-info))
                (apply-filter {:field "x" :valid true})
                (apply-filter {:field "y" :valid true}))
      add-opacity? (add-opacity opacity-fld-name)
      dashed-lines? (add-dashes dash-fld-name)
      increase-hover-area? increase-hover-area
      zoom-on-scroll? zoom-on-scroll

      (and stack-info
           (> (count data) 1)
           line-chart?)
      (add-rule-for-line chart-type stack-fld-name chart-interpolate)

      (seq stack-info)
      (stack {:stack-fld stack-fld-name :stack-fld-type stack-fld-type
              :stack-fld-opts stack-fld-opts})

      (seq custom-stack-colours-map)
      (custom-stack-colours custom-stack-colours-map)

      select-on-click? (select-on-click {:select-fld stack-fld-name :select-name :company})
      tooltip? (add-tooltip x-fld-info y-fld-info stack-info)
      disable-legend? (update :config merge {:legend {:disable true}}))))