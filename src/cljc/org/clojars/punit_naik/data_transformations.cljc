(ns org.clojars.punit-naik.data-transformations)

(defn apply-aggregate
  "Adds a vega aggregate element in the Vega-lite spec"
  [vl-spec
   {:keys [fld-name fld-alias grp-by-flds op]
    :or {fld-alias (str fld-name "_agg")}}]
  (update vl-spec :transform conj {:aggregate
                                   [{:op op :field fld-name :as fld-alias}]
                                   :groupby (if (coll? grp-by-flds)
                                              grp-by-flds
                                              [grp-by-flds])}))

(defn do-calculation
  "Does a calculation on some fields in the data in the Vega-lite spec"
  [vl-spec
   {:keys [calc-str calc-fld-alias]}]
  (update vl-spec :transform conj {:calculate calc-str :as calc-fld-alias}))

(defn apply-filter
  "Applies filter on the Dataset in the Vega-lite spec"
  [vl-spec filter-clauses]
  (update vl-spec :transform conj {:filter filter-clauses}))