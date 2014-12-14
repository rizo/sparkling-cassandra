(ns flambo-cassandra.core
  (:import (com.datastax.spark.connector.japi CassandraJavaUtil SparkContextJavaFunctions CassandraRow RDDJavaFunctions)
           (com.datastax.spark.connector.japi.rdd CassandraJavaRDD)
           (scala.collection JavaConverters)
           (scala.collection.convert Decorators$AsJava)
           (org.apache.spark.rdd RDD)
           (org.apache.spark.api.java JavaRDD JavaSparkContext)
           (java.util Map List HashMap))
  (:require [flambo.conf :as conf]
            [flambo.api :as f]))


;;conversion stuff
(defprotocol ConvertibleToClojure
  (->clj [o]))

(extend-protocol ConvertibleToClojure
  Map
  (->clj [o] (let [entries (.entrySet o)]
               (reduce (fn [m [^String k v]]
                         (assoc m (keyword k) (->clj v)))
                       {} entries)))

  List
  (->clj [o] (vec (map ->clj o)))

  Object
  (->clj [o] o)

  nil
  (->clj [_] nil))

(defn as-clj-map
  [m]
  (->clj m))



;cassandra related operations

(defn ctable
  "Returns the table as RDD"
  [^JavaSparkContext spark-context keyspace table]
  (let [funcs ^SparkContextJavaFunctions (CassandraJavaUtil/javaFunctions spark-context)]
    (.cassandraTable funcs keyspace table)))

(defn select
  "select clause for the query"
  ([^CassandraJavaRDD cass-rdd & args]
   (.select cass-rdd (into-array String args))
   )
  ([^CassandraJavaRDD cass-rdd]
   cass-rdd))

(defn where
  "where clause for the query"
  [^CassandraJavaRDD cass-rdd where-clause & args]
  (.where cass-rdd where-clause (into-array Object args)))

;convert a cassandra row to clojure data structure
(f/defsparkfn row->clj
              [^CassandraRow row]
              (as-clj-map (.toMap row)))

;not working yet
(defn save
  "rdd can contain any type of object,
  for now, ask for the first item in the container and get its class"
  [^JavaRDD rdd keyspace table write-type field-to-col-mapping]
  (let [clazz (class (f/first rdd))
        funcs ^RDDJavaFunctions (CassandraJavaUtil/javaFunctions rdd)
        writer (.writerBuilder funcs keyspace table (CassandraJavaUtil/mapToRow write-type field-to-col-mapping))]
    (.saveToCassandra writer)))





