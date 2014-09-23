(ns flambo-cassandra.temp)


(require '[flambo.conf :as conf]
         '[flambo.api :as f]
         '[flambo-cassandra.core :as fc]
         :reload-all)


(def c (-> (conf/spark-conf)
           (conf/master "local")
           (conf/app-name "flame_princess")
           (conf/set "spark.cassandra.connection.host", "localhost")
           ))




(def sc (f/spark-context c))


(def data (fc/ctable sc "test" "person"))
data

(def account (f/first (fc/select data "name")))
(def account (f/first (fc/select data)))

(fc/row->clj account)

(f/collect account)


;filter on spark
(-> data
    (f/map (f/fn [s] (fc/row->clj s)))
    (f/filter (f/fn [s] (= (:age s) 999)))
    (f/collect)
    )

;filter on cassandra
;this test data needs a secondary index for this
(-> (fc/ctable sc "test" "person")
    (fc/where "age=?" 999)
    (f/map (f/fn [s] (fc/row->clj s)))
    (f/collect)
    )

