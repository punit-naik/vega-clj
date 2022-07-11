(ns org.clojars.punit-naik.canvas)

(defn set-height
  "Sets the height of the Vega-lite component"
  [vl-spec value]
  (assoc vl-spec :height value))

(defn set-width
  "Sets the width of the Vega-lite component"
  [vl-spec value]
  (assoc vl-spec :width value))

(defn set-backgroud
  "Sets the colour of the chart background"
  [vl-spec value]
  (assoc vl-spec :background value))

(defn set-config
  [vl-spec value]
  (assoc vl-spec :config value))

(defn set-padding
  "Sets the padding of the Vega-lite component
   `value` is a map of keys [:top :bottom :left :right]"
  [vl-spec value]
  (assoc vl-spec :padding value))

(defn add-title
  "Adds a title to the Vega-lite spec"
  [vl-spec title]
  (assoc vl-spec :title title))

(defn add-desc
  "Adds a description to the Vega-lite spec"
  [vl-spec desc]
  (assoc vl-spec :description desc))

(defn add-mark
  "Adds a mark (plot) type to the Vega-lite spec"
  [vl-spec mtype]
  (assoc vl-spec :mark mtype))