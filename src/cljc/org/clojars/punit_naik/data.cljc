(ns org.clojars.punit-naik.data)

(defn add-dataset
  [vl-spec data]
  (assoc-in vl-spec [:data :values] data))

(defn tick-count
  "Returns the tick count of axes based on `data`"
  [data & [max-ticks]]
  (cond
    (= (count data) 1) 2
    (and max-ticks
         (>= (count data)
             max-ticks)) max-ticks
    :else (count data)))