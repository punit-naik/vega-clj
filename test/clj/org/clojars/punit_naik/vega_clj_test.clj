(ns org.clojars.punit-naik.vega-clj-test
  (:require [clojure.test :refer [deftest is]]
            [org.clojars.punit-naik.vega-clj :as v]))

(deftest hex->rgb
  (is (= [255 255 255] (v/hex->rgb "#FFFFFF")))
  (is (= [0 0 0] (v/hex->rgb "#000000"))))

(deftest convert-colours-to-rgb-test
  (is (= {:a {:b {:c {:range [[255 255 255]]}}}}
         (v/convert-colours-to-rgb {:a {:b {:c {:range ["#FFFFFF"]}}}}))))