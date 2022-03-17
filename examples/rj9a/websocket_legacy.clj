(ns rj9a.websocket-legacy
  (:gen-class)
  (:require [ring.adapter.jetty9 :as jetty]))

(defn dummy-app [req] {:body "<h1>It works</h1>" :status 200})

(def echo-handler {:on-text (fn [ws text] (jetty/send! ws text))})

(def ping-thing {:on-connect (fn [ws] (jetty/ping! ws "PING!"))
                 :on-pong (fn [ws bytebuffer] (jetty/send! ws "PONG!"))})

(defn websocket-accept [req]
  (println req)
  (assoc echo-handler
         :subprotocol (first (:websocket-subprotocols req))))

(defn websocket-reject [req]
  {:error {:code 403 :message "Forbidden"}})

(defn -main [& args]
  (jetty/run-jetty dummy-app {:port 5000 :websockets {"/path1" echo-handler
                                                      "/path2" websocket-accept
                                                      "/path3" websocket-reject
                                                      "/path4" ping-thing}}))
