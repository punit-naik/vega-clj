(ns org.clojars.punit-naik.chart-actions)

(defn add-tooltip
  [vl-spec
   {:keys [x-fld-name x-fld-type x-fld-title]
    :or {x-fld-name "x"
         x-fld-type "temporal"
         x-fld-title "Time"}}
   {:keys [y-fld-name y-fld-type y-fld-title]
    :or {y-fld-name "y"
         y-fld-type "quantitative"
         y-fld-title "Count"}}
   {:keys [stack-fld-name stack-fld-type stack-fld-title]
    :or {stack-fld-name "label"
         stack-fld-type "nominal"
         stack-fld-title "Labels"}
    :as stack-info}]
  (update
   vl-spec
   :encoding
   assoc
   :tooltip
   (cond-> [{:field x-fld-name :title x-fld-title :type x-fld-type}
            {:field y-fld-name :title y-fld-title :type y-fld-type}]
     (seq stack-info)
     (conj {:field stack-fld-name :title stack-fld-title :type stack-fld-type}))))

(defn increase-hover-area
  "Adds a selection in the `spec` to increase the hover area of a point on the chart
   So that tooltips show even if the cursor is not exactly on the point"
  [spec]
  (update spec :selection assoc :hover {:nearest true :type "single" :empty "none" :on "mouseover" :clear "mouseout"}))

(defn select-on-click
  "Selects data only belonging to a particular label which was clicked
   and fades out others from the legend bar"
  [spec {:keys [select-fld select-type select-bind select-name]
         :or {select-fld "label"
              select-type "multi"
              select-bind "legend"
              select-name :A}}]
  (-> spec
      ;; Selection
      (update :selection assoc select-name {:type select-type :fields [select-fld] :bind select-bind})
      ;; Selection Action
      (update :encoding assoc :opacity {:condition {:selection (name select-name) :value 1} :value 0.05})))

(defn zoom-on-scroll
  "Zooms in/out the graph"
  [spec]
  (update spec :selection assoc :grid {:type "interval" :bind "scales"}))