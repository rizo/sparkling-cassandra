(ns flambo-cassandra.bean
  (:import (java.util Map)))

(defn ignore-case [k]
  (->> k name clojure.string/lower-case keyword))

(defn -init
  ([] [[] (atom {})])
  ([^Map x] [[] (atom x)])
  )

(defn -toString
  [this]
  (str @(.state this)))

(defn -equals
  [this other]
  (= @(.state this) @(.state other)))

(defn -hashCode
  [this]
  (hash @(.state this)))

(defn set-field
  [this key value]
  (swap! (.state this) into {(ignore-case key) value}))

(defn get-field
  [this key]
  (@(.state this) (ignore-case key)))

(defn gen-method-defs [fields]
  (mapcat (fn [[name type]] [[(str "set" name) [type] 'void]
                             [(str "get" name) [] type]]) fields))

(defn def-access-methods [fields]
  (mapcat (fn [field] [`(defgetter ~field) `(defsetter ~field)]) fields))

(defmacro defsetter [field]
  `(intern 'flambo-cassandra.bean '~(symbol (str "-set" field))
           (fn [this# value#]
             (set-field this# ~(keyword field) value#))))

;(macroexpand '(defsetter asd))

(defmacro defgetter [field]
  `(intern 'flambo-cassandra.bean '~(symbol (str "-get" field))
           (fn [this#]
             (get-field this# ~(keyword field)))))

;(macroexpand '(defgetter asd))

(defmacro defbean [bean-name fields]
  `(do
     (gen-class
       :main false
       :impl-ns flambo-cassandra.bean
       :state ~'state
       :init ~'init
       :name ~bean-name
       :methods ~(gen-method-defs fields)
       :implements [java.io.Serializable]
       :constructors {[]    []
                      [Map] []}
       )
     ~@(def-access-methods (keys fields))
     ))
