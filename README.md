## flambo-cassandra

Cassandra Bindings for Flambo

Flambo is a Clojure DSL for Apache Spark

These bindings are for dealing with Cassandra RDD's if you are using the official spark-cassandra-connector

##TODO
write not supported atm


## Usage
 
    (require '[flambo.conf :as conf]
             '[flambo.api :as f]
             '[flambo-cassandra.core :as fc]
             :reload-all)
              
              
    (-> (fc/ctable sc "test" "person")
        (f/map (f/fn [s] (fc/row->clj s)))
        (f/collect)
        )
                     
                     
see tests for more

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
