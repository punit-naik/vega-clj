(ns org.clojars.punit-naik.canvas-test
  (:require [clojure.test :refer [deftest is]]
            [org.clojars.punit-naik.canvas :as canvas]))

(deftest set-height-test
  (is (= {:height 100}
         (canvas/set-height {} 100))))

(deftest set-width-test
  (is (= {:width 100}
         (canvas/set-width {} 100))))

(deftest set-backgroud-test
  (is (= {:background "transparent"}
         (canvas/set-backgroud {} "transparent"))))

(deftest set-config-test
  (is (= {:config {:view {:stroke "transparent"}
                   :mark {:cursor "pointer"}}}
         (canvas/set-config {} {:view {:stroke "transparent"}
                                :mark {:cursor "pointer"}}))))

(deftest set-padding-test
  (is (= {:padding 10}
         (canvas/set-padding {} 10))))

(deftest add-title-test
  (is (= {:title "Test"}
         (canvas/add-title {} "Test"))))

(deftest add-desc-test
  (is (= {:description "Test Description"}
         (canvas/add-desc {} "Test Description"))))

(deftest add-mark-test
  (is (= {:mark {:type "line"}}
         (canvas/add-mark {} {:type "line"}))))