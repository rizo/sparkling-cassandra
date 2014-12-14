(ns flambo-cassandra.temp
  (:import (java.util HashMap)))

(require '[flambo.conf :as conf]
         '[flambo.api :as f]
         '[flambo-cassandra.core :as fc]
         '[flambocassandra.bean :as fcb]
         :reload-all)

(fcb/defbean flambocassandra.bean.Bean1 {Id String Name String Age Integer})
(flambocassandra.bean.Bean1.)
(flambocassandra.bean.Bean1. {:id "asd" :name "jack" :age 54})



;(ns-publics 'user)
;(macroexpand-1 '(fcb/defbean bean1 {bar String baz Integer}))
;(macroexpand '(fcb/defbean bean1 {bar String baz Integer}))

;CREATE KEYSPACE test WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1 };
;CREATE TABLE test.person (id int PRIMARY KEY, name text , age int);
;INSERT INTO test.person (id, name, age) VALUES (1, 'Illidan',999);
;INSERT INTO test.person (id, name, age) VALUES (2, 'Diablo',1000);
;INSERT INTO test.person (id, name, age) VALUES (3, 'Ajani',30);


;CREATE TABLE test.output1 (col1 text PRIMARY KEY, col2 text , col3 text);

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
    (f/map fc/row->clj)
    (f/filter (f/fn [s] (= (:age s) 999)))
    (f/collect)
    )

;filter on cassandra
;this test data needs a secondary index for this


;(defprotocol ICassandraOutput
;  "documentation"
;  (getCol1 [this] "documentation")
;  (setCol1 [this v] "documentation")
;
;  (getCol2 [this] "documentation")
;  (setCol2 [this v] "documentation")
;
;  (getCol3 [this] "documentation")
;  (setCol3 [this v] "documentation")
;  )
;
;(deftype CassandraOutput
;  [^:unsynchronized-mutable col1
;   ^:unsynchronized-mutable col2
;   ^:unsynchronized-mutable col3]
;  ICassandraOutput
;  (getCol1 [this] col1)
;  (setCol1 [this v] (set! col1 v))
;
;  (getCol2 [this] col2)
;  (setCol2 [this v] (set! col2 v))
;
;  (getCol3 [this] col3)
;  (setCol3 [this v] (set! col3 v))
;
;  Object
;  (toString [this] (str col1 col2 col3))
;
;  )


;(let [x (CassandraOutput. nil nil nil)]
;  (.setCol1 x "5")
;  (.getCol1 x))




(-> (fc/ctable sc "test" "person")
    ;(fc/where "age=?" 999)
    (f/map fc/row->clj)
    (f/map (f/fn [x] (flambocassandra.bean.Bean1. x)))
    ;(f/collect)
    ;(f/save-as-text-file "/tmp/1.txt")
    ;
    (fc/save "test" "output1" flambocassandra.bean.Bean1 {"id"   "col1"
                                               "name" "col2"
                                               "age"  "col3"})
    )


(-> (fc/ctable sc "test" "person")
    (f/map (f/fn [s] (fc/row->clj s)))
    (f/collect)
    )

;
(class (f/first data))
;save example
;(fc/save data "test" "person2" {"id" "id" "age" "age" "name" "name"})




