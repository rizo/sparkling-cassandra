(ns flambo-cassandra.core
  (:import (com.datastax.spark.connector CassandraJavaUtil SparkContextJavaFunctions CassandraRow)
           (com.datastax.spark.connector.rdd CassandraJavaRDD)
           (scala.collection JavaConverters)
           (scala.collection.convert Decorators$AsJava))
  (:require [flambo.conf :as conf]
            [flambo.api :as f]))


;;conversion stuff
(defprotocol ConvertibleToClojure
  (->clj [o]))

(extend-protocol ConvertibleToClojure
  java.util.Map
  (->clj [o] (let [entries (.entrySet o)]
               (reduce (fn [m [^String k v]]
                         (assoc m (keyword k) (->clj v)))
                       {} entries)))

  java.util.List
  (->clj [o] (vec (map ->clj o)))

  java.lang.Object
  (->clj [o] o)

  nil
  (->clj [_] nil))

(defn as-clj-map
  [m]
  (->clj m))



;cassandra related operations

(defn ctable [spark-context keyspace table]
  (let [funcs ^SparkContextJavaFunctions (CassandraJavaUtil/javaFunctions spark-context)]
    (.cassandraTable funcs keyspace table)))

(defn select
  ([^CassandraJavaRDD cass-rdd & args]
   (.select cass-rdd (into-array String args))
   )
  ([^CassandraJavaRDD cass-rdd]
   cass-rdd))

(defn where [^CassandraJavaRDD cass-rdd where-clause & args]
  (.where cass-rdd where-clause (into-array Object args)))



(f/defsparkfn row->clj [^CassandraRow row]
              (let [scala-map (.toMap row)
                    java-map (.asJava ^Decorators$AsJava (JavaConverters/mapAsJavaMapConverter scala-map))]
                (as-clj-map java-map)))

