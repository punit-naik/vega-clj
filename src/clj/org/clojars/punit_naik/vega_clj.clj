(ns org.clojars.punit-naik.vega-clj
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [oz.core :as oz]))

(defn hex->rgb
  [colour]
  (->> (rest colour)
       (partition-all 2)
       (map (comp read-string str/join #(conj % "0x")))))

(defn convert-colours-to-rgb
  "Coverts HEX colour strings to RGB values"
  [vl-spec]
  (walk/postwalk
   (fn [x]
     (if (and (map? x)
              (contains? x :range))
       (update x :range (partial map hex->rgb)) x))
   vl-spec))

(defn plot
  [spec]
  (-> spec
      convert-colours-to-rgb
      oz/view!))