(defproject flambo-cassandra-bindings "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [yieldbot/flambo "0.3.3"]
                 [org.apache.spark/spark-core_2.10 "1.0.0"]
                 [org.apache.spark/spark-streaming_2.10 "1.0.0"]
                 [com.datastax.spark/spark-cassandra-connector-java_2.10 "1.0.0"]]
  ;had to be done
  :aot [flambo-cassandra-bindings.core]
  )
