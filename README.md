cee-format
==========

Tools for the CEE structured logging format

CEE is a JSON/XML structured log format more powerful and easy to use
than logging and parsing custom string formats.  Standard syslog
daemons such as `rsyslog` and `syslog-ng` can interpret CEE, and the
project is supported by Red Hat and [Project Lumberjack] [lumberjack].

[lumberjack]: https://fedorahosted.org/lumberjack/

This library provides the backing data structure transformation
in Clojure.  This can enable a variety of utilities, though it doesn't
do any logging or syslog network communication per se.


Basic Usage
-----------

```clojure
(require '[cee-format.core :as cee])

;; Simple CEE generation
(cee/cee "Hello world!") ; string message
(cee/cee {:my-key "My Value!"}) ; object (i.e. map) values
```

`cee/cee` returns CEE strings, e.g.:

```clojure
user=> (cee/cee "Hello world!")
"@cee:
{\"pri\":\"INFO\",\"time\":\"2014-06-01T21:01:23.464Z\",\"msg\":\"Hello
world!\",\"host\":\"t16\",\"sev\":6,\"syslog\":{\"pri\":134,\"fac\":16,\"ver\":1}}"
```

CEE Object Usage
----------------

CEE is designed to represent named, structurally nested objects as
part of the log format.  It uses JSON, which we can generate from
Clojure data structures.  CEE specifies a set of standard fields
derived from syslog:

https://fedorahosted.org/lumberjack/wiki/FieldList

```clojure
;; CEE generation with options specified as per official CEE field list
(cee/cee {:sev :error
          :syslog {:fac :local1}}
         {:my-key "My value!"})

;; Making a CEE generator that always has the default options you want
(def ce e-with-my-opts (cee/generator {:sev :warn}))
(cee-with-my-opt "Hello!") ; has severity=warn by default, from above
(cee-with-my-opts {:sev :info} "Hello!") ; may still be overridden later
```


Motivation
----------

Logging is a surprisingly complicated, unsurprisingly important
activity.  From your app's perspective:

1. Your app should not need to understand how the system (or
   cluster's) log management works--it should just be able
   to emit log events.

2. The log events emitted by the app should be able to be composable
   data structures, not deal with custom string parsing/unparsing.

The [twelve-factor](http://12factor.net/logs) site has the right
gist of things, in general.  UNIX-like environments already give us
a host of good, standard tools for managing logs, such as `syslog` and
other tools.  We should use those as a common foundation for logging,
routing, log management, and let the apps remain simple.

Furthermore, CEE gives us the ability to log structured data, not
strings.  This is the proper level of abstraction for an app, now
that there are so many useful things that we can do downstream from
the log event stream, e.g.:

- route and categorize the logs based on data therein
- perform analytics on the events
- monitor and alert based on the events


How do I log this to syslog?
----------------------------

`cee-format` only provides data transformation, it doesn't actually
provide any syslog logging facilities.  However, we already have tools
to do this, e.g.

- `(println (cee/cee "Hello world!"))`: prints log output to STDOUT
- `myprog | logger -t prog_id`: pipes to the standard
  `logger` utility which sends data to syslog, tagging all the
  messages with `prog_id` so they can be filtered/selected.
- Your syslog daemon probably already knows how to filter messages
  tagged with `prog_id`, interpret the CEE, take action.  E.g. for the
  rsyslog that comes with Ubuntu:

  http://www.rsyslog.com/json-elasticsearch/

Separate libraries such as [timbre](https://github.com/ptaoussanis/timbre)
can provide actual logging functionality for controller how the CEE
output gets printed, whether it goes to STDOUT or (one day) directly
to the syslog socket.


Internals
---------

Internally the code is all just Clojure map transformation, which is
applied in a function composition pipeline, and then finally rendered
out to JSON.  You can modify the map transformations and pipeline
all you like:

```clojure
;; Get the underlying CEE map as as a Clojure map
(def m (cee/cee-map {:sev :debug} "Hello!"))

;; fiddle any map structure you like here

;; convert to JSON and output as a CEE string
(require 'cee-format.util)
(println (cee-format.util/str<-map m))
```


License
-------

Copyright © 2014 Framed Data, Inc.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
