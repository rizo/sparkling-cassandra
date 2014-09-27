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
(class data)

(def account (f/first (fc/select data "name")))
(def account (f/first (fc/select data)))

account

(fc/row->clj account)


;filter on spark
(-> (fc/ctable sc "test" "person")
    (f/map (f/fn [s] (fc/row->clj s)))
    (f/filter (f/fn [s] (= (:age s) 999)))
    (f/collect)
    )

;filter on cassandra
;this test data needs a secondary index for this
(-> (fc/ctable sc "test" "person")
    (fc/where "age=?" 999)
    (f/map (f/fn [s] (fc/row->clj s)))
    ;(f/collect)
    ;(f/save-as-text-file "/tmp/1.txt")
    (fc/save "test" "person2")
    )


(-> (fc/ctable sc "test" "person")
    (f/map (f/fn [s] (fc/row->clj s)))
    (f/collect)
    )

;
(class (f/first data))
;save example
;(fc/save data "test" "person2" {"id" "id" "age" "age" "name" "name"})




