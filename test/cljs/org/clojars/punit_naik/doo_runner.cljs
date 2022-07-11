(ns org.clojars.punit-naik.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [org.clojars.punit-naik.vega-clj-test]))

(doo-tests 'org.clojars.punit-naik.vega-clj-test)

