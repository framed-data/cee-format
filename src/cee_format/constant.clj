(ns cee-format.constant)

(def severity-levels
  "http://en.wikipedia.org/wiki/Syslog#Severity_levels"
  {:emerg 0
   :emergency 0
   :panic 0
   :alert 1
   :crit 2
   :err 3
   :error 3
   :warn 4
   :warning 4
   :notice 5
   :info 6
   :debug 7})

(def severity-level-keywords
  [:emerg :alert :crit :err :warn :notice :info :debug])

(def facility-levels
  {:kern 0
   :user 1
   :mail 2
   :daemon 3
   :auth 4
   :syslog 5
   :lpr 6
   :news 7
   :uucp 8
   :authpriv 10
   :ftp 11
   :cron 15
   :local0 16
   :local1 17
   :local2 18
   :local3 19
   :local4 20
   :local5 21
   :local6 22
   :local7 23})

