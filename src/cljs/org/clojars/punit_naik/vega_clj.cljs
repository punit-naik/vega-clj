(ns org.clojars.punit-naik.vega-clj
  (:require cljsjs.vega cljsjs.vega-lite cljsjs.vega-embed cljsjs.vega-tooltip
            [reagent.core :as r]
            [reagent.dom :as rdom]))

(defn- parse-vl-spec
  [spec elem click-handler-fn]
  (when (seq spec)
    (let [click-listener-name "data-point-click"
          opts {:renderer "canvas"
                :mode "vega-lite"
                :actions false
                :tooltip {:theme "dark"}
                ;; For click handler, we have to `patch`
                ;; As signals are not officially supported in `vega-lite`
                :patch (fn [spec]
                         (.push
                          (.-signals spec)
                          (clj->js
                           {"name" click-listener-name
                            "value" 0
                            "on" [{"events" "mousedown" "update" "datum"}]}))
                         spec)}]
      (.then (js/vegaEmbed elem
                           (clj->js spec)
                           (clj->js opts))
             (fn [result]
               (.addSignalListener (get (js->clj result) "view")
                                   click-listener-name click-handler-fn)
               result)))))

(defn vega-lite
  "Reagent component that renders vega-lite."
  [spec click-handler-fn]
  (r/create-class
   {:display-name "vega-lite"
    :component-did-mount (fn [this]
                           (parse-vl-spec spec (rdom/dom-node this) click-handler-fn))
    :component-did-update (fn [this [_ new-spec]]
                            (parse-vl-spec new-spec (rdom/dom-node this) click-handler-fn))
    :reagent-render (fn [_]
                      [:div {:id (:id spec)
                             :style {:width "100%" :height "100%"}}])}))

(defn default-click-handler
  [_ data-point]
  (js/console.log "You clicked:" data-point))

(defn plot
  "Renders the Hiccup style plot from a spec
   To be used only inside Hiccup style HTML data structure to be rendered"
  ([spec] (plot spec default-click-handler))
  ([spec on-click-handler-fn]
   (vega-lite spec on-click-handler-fn)))