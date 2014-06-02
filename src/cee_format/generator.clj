(ns cee-format.generator
  "Generate subsets of CEE maps, i.e. maps filling particular fields"
  (:require (cee-format [constant :as constant]
                        [util :as util])
            [clojure.string :as string]))

(defn compute-priority
  "Return a map of values corresponding to the syslog priority field"
  [facility severity]
  (let [facility-num (or (constant/facility-levels facility) facility)
        severity-num (or (constant/severity-levels severity) severity)
        severity-keyword (constant/severity-level-keywords severity-num)
        priority-num (+ (* facility-num 8) severity-num)]
    {:pri (string/upper-case (name severity-keyword))
     :sev severity-num
     :syslog {:fac facility-num
              :pri priority-num}}))

(defn get-priority
  "Given a CEE map, return a tuple [facility severity]"
  [m]
  [(get-in m [:syslog :fac])
   (:sev m)])

(def priority
  "Given a CEE map with the minimal `get-priority` fields filled in,
  return a map with all of the consequent facility/severity/priority
  fields calculated and filled in."
  #(apply compute-priority (get-priority %)))

(defn system-defaults-once
  "Return a CEE map of default values that should only need to be
  filled in once at setup-time"
  []
  {:host (util/hostname)
   :sev (constant/severity-levels :info)
   :syslog {:fac (constant/facility-levels :local0)
            :ver 1}})

(defn system-defaults-each
  "Return a CEE map of default values that will need to be added
  for each logging call."
  []
  {:time (-> (util/time-now) util/timestamp)})
