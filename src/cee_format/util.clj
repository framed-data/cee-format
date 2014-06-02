(ns cee-format.util
  "General purpose utility functions"
  (:require (clj-json [core :as json])
            (clj-time [core :as time]
                      [coerce :as time.coerce]))
  (:import java.net.InetAddress))

(defn hostname []
  (.getHostName (InetAddress/getLocalHost)))

(def merge-or-replace
  (partial
    merge-with
    (fn merge-maps-replace-otherwise [v0 v1]
      (if (map? v0)
        (merge v0 v1)
        v1))))

(defn time-now []
  (time/now))

(defn timestamp [datetime]
  (time.coerce/to-string datetime))

(defn str<-map
  "Return a CEE string (JSON + CEE cookie prefix), given a fully
  transformed CEE map."
  [m]
  (str "@cee: "
       (json/generate-string m)))

(defn log-value-key
  "Given a value to log, return the appropriate CEE map."
  [log-value]
  (if (map? log-value) :native :msg))
