(defproject flambo-cassandra "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [yieldbot/flambo "0.4.0"]
                           [org.apache.spark/spark-core_2.10 "1.1.0"]
                           [org.apache.spark/spark-streaming_2.10 "1.1.0"]
                           [com.datastax.spark/spark-cassandra-connector-java_2.10 "1.1.0"]]
            ;had to be done
            :aot [flambo-cassandra.core
                  ;flambocassandra.bean
                  flambo-cassandra.temp
                  ]
            :profiles {:dev {:dependencies [[clojurewerkz/cassaforte "2.0.0-beta4"]]}}
            )
