(ns flambo-cassandra.core-test
  (:require [clojure.test :refer :all]
            [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql :as cql]
            [clojurewerkz.cassaforte.query :as q]
            [flambo.conf :as conf]
            [flambo.api :as f]
            [flambo-cassandra.core :as fc])
  )

(defmacro swallow-exceptions [& body]
  `(try ~@body (catch Exception e#)))

(def cs (atom nil))

(defn create-table! []
  (let [conn (cc/connect ["127.0.0.1"])]
    (cql/use-keyspace conn "test")
    (cql/create-table conn "person2"
                      (q/column-definitions {:id          :int
                                             :name        :text
                                             :age         :int
                                             :primary-key [:id]}))
    (reset! cs conn)
    )
  )

(defn insert! [data]
  (cql/insert @cs "person2" data))

(def c (-> (conf/spark-conf)
           (conf/master "local")
           (conf/app-name "flame_princess")
           (conf/set "spark.cassandra.connection.host", "localhost")
           ))

(use-fixtures :once (fn [f]
                      (do
                        (swallow-exceptions (do
                                              (create-table!)
                                              (cc/execute @cs "CREATE INDEX person2_age ON test.person2 (age);")
                                              (insert! {:id 1 :name "Jack" :age 50})
                                              (insert! {:id 2 :name "Jason" :age 40})))
                        (f))))

(deftest select-test
  (testing "select single column"
    (is (= [{:name "Jack"} {:name "Jason"}]
           (let [sc (f/spark-context c)]
             (-> (fc/ctable sc "test" "person2")
                 (fc/select "name")
                 (f/map (f/fn [s] (fc/row->clj s)))
                 (f/collect))
             )))
    (is (= [{:name "Jack", :age 50, :id 1} {:name "Jason", :age 40, :id 2}]
           (let [sc (f/spark-context c)]
             (-> (fc/ctable sc "test" "person2")
                 (f/map (f/fn [s] (fc/row->clj s)))
                 (f/collect))
             )))
    (is (= [{:name "Jack", :age 50, :id 1}]
           (let [sc (f/spark-context c)]
             (-> (fc/ctable sc "test" "person2")
                 (fc/where "age=?" 50)
                 (f/map (f/fn [s] (fc/row->clj s)))
                 (f/collect))
             )))
    ))
