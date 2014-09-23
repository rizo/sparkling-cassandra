(ns flambo-cassandra-bindings.temp)


(require '[flambo.conf :as conf]
         '[flambo.api :as f]
         '[flambo-cassandra-bindings.core :as b]
         :reload-all)


(def c (-> (conf/spark-conf)
           (conf/master "local")
           (conf/app-name "flame_princess")
           (conf/set "spark.cassandra.connection.host", "localhost")
           ))




(def sc (f/spark-context c))


(def data (b/ctable sc "test" "person"))
data

(def account (f/first (b/select data "name")))
(def account (f/first (b/select data)))

(b/row->clj account)

(f/collect account)


;filter on spark
(-> data
    (f/map (f/fn [s] (b/row->clj s)))
    (f/filter (f/fn [s] (= (:age s) 999)))
    (f/collect)
    )

;filter on cassandra
;this test data needs a secondary index for this
(-> (b/ctable sc "test" "person")
    (b/where "age=?" 999)
    (f/map (f/fn [s] (b/row->clj s)))
    (f/collect)
    )

