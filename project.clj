(defproject org.clojars.punit-naik/vega-clj "1.0.2"
  :description "A clojure(script) library that generates vega spec to be rendered as a chart by the Vega JS lib"
  :url "https://github.com/punit-naik/vega-clj"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[cheshire "5.11.0"]
                 ;; Vega
                 [cljsjs/vega-tooltip "0.27.0-0"]
                 [cljsjs/vega-embed "6.19.0-0"]
                 [cljsjs/vega-lite "4.17.0-0"]
                 [cljsjs/vega "5.17.0-0"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.11.60"]
                 [metasoarous/oz "2.0.0-alpha5"
                  :exclusions
                  [cheshire]]
                 [reagent "1.1.1"]]
  :source-paths ["src/clj" "src/cljc" "src/cljs"]
  :test-paths ["test/clj" "test/cljc" "test/cljs"]
  :repl-options {:init-ns org.clojars.punit-naik.vega-clj}
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :clean-targets ^{:protect false}
  [:target-path
   [:cljsbuild :builds :app :compiler :output-dir]
   [:cljsbuild :builds :app :compiler :output-to]
   "node_modules"
   "package.json"
   "package-lock.json"]
  :profiles
  {:uberjar {:aot :all
             :uberjar-name "vega-clj.jar"}
   :clj-test {:source-paths ["src/clj" "src/cljc"]
              :test-paths ["test/clj" "test/cljc"]}
   :cljs-test {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
               :test-paths ["test/cljc" "test/cljs"]
               :plugins      [[lein-doo "0.1.11"]]
               :cljsbuild
               {:builds
                {:browser-test
                 {:source-paths ["src/cljs" "src/cljc" "test/cljs"]
                  :compiler
                  {:optimizations :none
                   :output-to "target/out/browser_tests.js"
                   :output-dir "target/out"
                   :main "org.clojars.punit-naik.doo-runner"
                   :pretty-print true
                   :npm-deps {:karma "6.4.0"
                              :karma-chrome-launcher "3.1.1"
                              :karma-cljs-test "0.1.0"
                              :react-dom "16.8.6"
                              :react "16.8.6"}
                   :install-deps true
                   :infer-externs true
                   :language-in     :ecmascript-next
                   :language-out    :ecmascript-next}}}}
               :repl-options {:init-ns org.clojars.punit-naik.vega-clj-test
                              :timeout 120000}}})
