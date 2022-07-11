(ns org.clojars.punit-naik.draw
  (:require [org.clojars.punit-naik.canvas :refer [add-mark]]
            [org.clojars.punit-naik.chart-actions :refer [select-on-click]]
            [org.clojars.punit-naik.data-transformations :refer [apply-filter]]))

(defn change-axis-color
  [spec color]
  (update-in
   spec
   [:config :axis]
   assoc
   :gridColor color
   :labelColor color
   :tickColor color
   :titleColor color))

(defn change-legend-color
  [spec color]
  (update-in spec [:config :legend] assoc
             :labelColor color
             :titleColor color))

(defn generate-colour-range
  "Generates `vega-lite` spec for colour ranges for field values"
  [data range-mapping-fn]
  {:domain data
   :range (map range-mapping-fn data)})

(defn add-axes
  "Adds axes to the plot in the Vega-lite spec"
  [vl-spec
   {:keys [x-fld-name x-fld-type x-agg-op x-fld-opts
           y-fld-name y-fld-type y-agg-op y-fld-opts]
    :or {x-fld-opts {} y-fld-opts {}}}]
  (let [x-axis (conj {:field x-fld-name
                      :type x-fld-type}
                     x-fld-opts)
        y-axis (conj {:field y-fld-name
                      :type y-fld-type}
                     y-fld-opts)
        x-axis-with-agg (if x-agg-op (assoc x-axis :aggregate x-agg-op) x-axis)
        y-axis-with-agg (if y-agg-op (assoc y-axis :aggregate y-agg-op) y-axis)
        axes {:x x-axis-with-agg :y y-axis-with-agg}]
    (update vl-spec :encoding merge axes)))

(defn get-fld-info
  "Gets the info of the field on `fld`-Axis"
  [spec fld]
  (get-in spec [:encoding fld]))

(defn add-dashes
  "Adds dashes to the stroke of a line chart"
  ([spec fld-name] (add-dashes spec fld-name "nominal"))
  ([spec fld-name fld-type]
   (update spec :encoding assoc :strokeDash {:field fld-name :type fld-type
                                             :legend nil})))

(defn add-opacity
  "Adds opacity/transparency to the stroke of a line chart based on `fld-name`"
  ([spec fld-name] (add-opacity spec fld-name "quantitative"))
  ([spec fld-name fld-type]
   (update spec :encoding assoc :strokeOpacity {:field fld-name :type fld-type
                                                :legend nil})))

(defn add-rule-for-line
  "Adds a rule (vertical line) when hovered over any point, works even when not exactly hovered over a point
   Makes clicking on line charts easier"
  [spec chart-type stack-fld-name chart-interpolate]
  (assoc spec :layer [(select-on-click {:encoding {:color {:value "transparent", :condition {:field stack-fld-name, :selection "hover"}}}, :mark {:strokeOpacity 0.5, :strokeDash [4 2], :type "rule", :point {:size 60, :filled true}}, :selection {:hover {:nearest true, :type "single", :empty "none", :on "mouseover", :clear "mouseout"}}} {:select-name :B, :select-fld stack-fld-name})
                      (-> {}
                          (add-mark (cond-> {:type chart-type
                                             :invalid "filter"}
                                      (= chart-type "moving-avg") (assoc :type "line")
                                      (or (= chart-type "line")
                                          (= chart-type "moving-avg")) (assoc :interpolate chart-interpolate)))
                          (select-on-click {:select-fld stack-fld-name :select-name :company})
                          (apply-filter {:field "x" :valid true})
                          (apply-filter {:field "y" :valid true}))]))

(defn stack
  "Stacks charts based on a particular field on the aggregated field of the chart
   NOTE: Only to be used when there is an aggregated field in the chart in `encoding` and not in `transform`"
  [vl-spec
   {:keys [stack-fld stack-fld-type stack-fld-opts]
    :or {stack-fld-opts {}}}]
  (let [color {:color (merge {:field stack-fld :type stack-fld-type} stack-fld-opts)}]
    (update vl-spec :encoding merge color)))

(defn get-stack-fld-info
  "Gets the info the the field using which the chart is being stacked (coloured)"
  [spec]
  (get-fld-info spec :color))

(defn get-step-size
  "Gets the step size for grouped bar charts based on data"
  [{:keys [width] :as spec}]
  (let [labels (->> spec :data :values
                    (map :label))
        total-groups (->> labels
                          (partition-by identity)
                          first count)
        step (double (/ (- width (* (dec total-groups) 2))
                        (count labels)))
        step (if (< step 1) 1 step)
        new-width {:step step
                   ;; Just to keep track of the width of the line chart
                   ;; Does not have to do anything with the actual spec
                   :original-value width}]
    new-width))

(defn header-for-grouped-bar-chart
  [x-fld-info total-records bucket]
  (merge (:axis x-fld-info)
         {:labelAngle 90
          :labelPadding 0
          :labelAlign "left"}
         (when (> total-records 100)
           {:labelExpr (str "[(timeFormat(datum.value, "
                            (cond
                              (contains? #{"1d" "1w"}
                                         bucket) "'%d'"
                              (contains? #{"1M" "1q"}
                                         bucket) "'%m'"
                              (contains? #{"1Y"}
                                         bucket) "'%Y'")
                            ") % " (if (= bucket "1d")
                                     7 3) ") == 0 ? "
                            "timeFormat (datum.value, '%d') + "
                            "'-' +"
                            "timeFormat (datum.value, '%m') + "
                            "'-' +"
                            "timeFormat(datum.value, '%Y') "
                            ": '']")
            :labelFontSize 9})))

(defn line->grouped-bar
  "Converts a spec with a multi line series (stacked) chart to a grouped bar chart
   Calculates width of each group's bar based on the number of stacks (colours) and current width of the chart"
  [spec bucket]
  (let [spacing 0
        total-records (->> spec :data :values count)
        x-fld-info (get-fld-info spec :x)
        stack-fld-info (get-stack-fld-info spec)
        new-width (get-step-size spec)
        new-x-fld-info {:x (-> stack-fld-info
                               (dissoc :scale)
                               (assoc :title "")
                               (assoc :axis (merge (:axis x-fld-info)
                                                   {:labels false :ticks false})))}
        column-info {:column (assoc x-fld-info
                                    :spacing spacing
                                    :header (header-for-grouped-bar-chart x-fld-info total-records bucket))}
        new-mark (assoc (:mark spec) :type "bar")]
    (-> spec
        (add-mark new-mark)
        (update-in [:encoding :y] dissoc :impute)
        (update :encoding merge new-x-fld-info)
        (update :encoding merge column-info)
        (assoc :width new-width))))

(defn grouped-bar->line
  "Converts a spec with a grouped bar chart to a multi line series (stacked) chart"
  [spec]
  (let [width {:width (get-in spec [:width :original-value])}
        new-x-fld-info {:x (-> spec
                               (get-in [:encoding :column])
                               (dissoc :spacing)
                               (update :axis dissoc :labelExpr)
                               (update :header dissoc :labelExpr))}
        new-mark (assoc (:mark spec)
                        :type "line"
                        :interpolate "monotone")]
    (-> spec
        (add-mark new-mark)
        (update :encoding dissoc :column)
        (update :encoding merge new-x-fld-info)
        (merge width))))

(defn custom-stack-colours
  "Adds custom colour to every label/stack field
   `colours-map` is a map with field values as keys and colours (hex strings) as values"
  [vl-spec colours-map]
  (assoc-in
   vl-spec
   [:encoding :color :scale]
   (reduce
    (fn [scale [field-value colour]]
      (-> scale
          (update :domain conj field-value)
          (update :range conj colour)))
    {:domain [] :range []}
    colours-map)))