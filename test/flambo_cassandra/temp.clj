(ns flambo-cassandra.temp)

(require '[flambo.conf :as conf]
         '[flambo.api :as f]
         '[flambo-cassandra.core :as fc]
         '[flambo-cassandra.bean :as fcb]
         :reload-all)



;(in-ns 'flambo-cassandra.bean)
(fcb/defbean flambo-cassandra.bean.Bean1 {Id String Name String Age Long})
(println (ns-publics 'flambo-cassandra.bean))
(println (ns-interns 'flambo-cassandra.bean))
(println (.getName (flambo-cassandra.bean.Bean1. {:id "asd" :name "jack" :age 54})))
(println (flambo-cassandra.bean.Bean1.))
(println (flambo-cassandra.bean.Bean1. {:id "asd" :name "jack" :age 54}))



(ns-publics 'flambo-cassandra.bean)
;(dir flambo-cassandra.bean)

(comment

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

  (-> (fc/ctable sc "test" "person")
      (f/map fc/row->clj)
      (f/map (f/fn [x] (flambo-cassandra.bean.Bean1. x)))
      ;(f/collect)
      ;(f/save-as-text-file "/tmp/1.txt")
      ;
      (fc/save "test" "output1"
               flambo-cassandra.bean.Bean1
               {"id"   "col1"
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

  )



