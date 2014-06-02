(ns cee-format.core
  "Tools for working with the CEE syslog format"
  (:require (cee-format [generator :as generator]
                        [util :as util])))

(defn intake-handler
  "Given a map of general CEE options, and a value to log,
  associate the log value properly into the map and return
  the map.

  Use in conjunction with a transformation pipeline to
  get the fully transformed CEE map/output string."
  ([log-value]
   (intake-handler {} log-value))
  ([opts log-value]
   (assoc opts
          (util/log-value-key log-value)
          log-value)))

(def default-map-transformation-pipeline
  (let [default-values (generator/system-defaults-once)]
    (comp #(util/merge-or-replace % (generator/priority %))
          #(util/merge-or-replace % (generator/system-defaults-each))
          (partial util/merge-or-replace default-values))))

(defn map-generator
  "Return a function that generates CEE maps, with the given default
  values always included automatically."
  [default-opts]
  (comp default-map-transformation-pipeline
        (partial util/merge-or-replace default-opts)
        intake-handler))

(defn generator
  "Return a function that generates CEE strings, with the given
  default values always included automatically."
  [default-opts]
  (comp util/str<-map (map-generator default-opts)))

(def cee-map
  "Given a map of general CEE options, and a value to log, return a
  fully transformed CEE map.  See `handler` for the params for this
  fn."
  (map-generator {}))

(def cee
  "Given a map of general CEE options, and a value to log, return
  the full CEE string.  See `handler` for the params for this fn."
  (generator {}))
