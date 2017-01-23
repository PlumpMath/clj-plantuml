(ns clj-plantuml.server
  (:require
   [clj-plantuml.plantuml :as p]
   [clojure.java.browse :as b]
   [clojure.java.io :as io]
   [clojure.string :as s]
   [compojure.core :refer [defroutes GET]]
   [compojure.route :refer [resources]]
   [org.httpkit.server :refer [run-server]]
   [ring.util.codec :as c]
   [ring.util.response :as r])
  (:gen-class))

(defn- parse-params [query]
  (reduce
   (fn [map s]
     (let [[_ k v] (re-matches #"([^=]+)=(.*)" s)]
       (assoc map (keyword k) v)))
   {}
   (s/split query #"&")))

(defn- plantuml-handler [req]
  (when-let [query (:query-string req)]
    (let [data (String. (c/base64-decode (:data (parse-params query))))
          uml (c/url-decode data)]
      (-> (p/render-cached uml "svg")
          io/input-stream
          r/response
          (r/content-type "image/svg")))))

(defroutes app
  (GET  "/" [] (slurp (io/resource "public/index.html")))
  (GET  "/plantuml" [] plantuml-handler)
  (resources "/"))

(defn -main [& args]
  (run-server #'app {:port 8080})
  (println "started server http://localhost:8080")
  (b/browse-url "http://localhost:8080"))
