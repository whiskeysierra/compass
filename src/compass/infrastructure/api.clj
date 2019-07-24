(ns compass.infrastructure.api
  (:require [compass.core.maps :as maps]
            [compass.domain.relation :refer :all]
            [compass.domain.repository :refer :all]
            [compass.infrastructure.memory :as memory]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as ring-json]
            [clj-json-patch.core :refer :all]))

(def dimension-repo (memory/dimension-repository))
(def key-repo (memory/key-repository))

; TODO this should be in a repo/service/domain function
(def relations
  (let [identifiers (-> rel methods keys)
        relations (map rel identifiers)]
    (maps/index :id relations)))

(def app-routes
  (routes
    (context "/relations" []
      (GET "/" []
        {:body {:relations (sort-by :title (vals relations))}})
      (GET "/:id" [id :<< keyword]
        {:body (id relations)}))

    (context "/dimensions" []
      (GET "/" []
        {:body {:dimensions (read-all-dimensions dimension-repo)}})
      (PUT "/:id" [id :as {:keys [body]}]
        (let [dimension (assoc body :id id)]
          (create-dimension dimension-repo dimension)
          {:body dimension}))
      (GET "/:id" [id]
        {:body (read-dimension dimension-repo id)})
      (PATCH "/:id" [id :as {patches :body}]
        (let [before (read-dimension dimension-repo id)
              after  (patch before patches)]
          {:body (update-dimension dimension-repo after)}))
      (DELETE "/:id" [id]
        (delete-dimension dimension-repo id)
        {:status 204}))

    (context "/keys" []
      (GET "/" []
        {:body {:keys (read-all-keys key-repo)}})
      (PUT "/:id" [id :as {:keys [body]}]
        (let [key (assoc body :id id)]
          (create-key key-repo key)
          {:body key}))
      (GET "/:id" [id]
        {:body (read-key key-repo id)})
      (PATCH "/:id" [id :as {patches :body}]
        (let [before (read-key key-repo id)
              after  (patch before patches)]
          {:body (update-key key-repo after)}))
      (DELETE "/:id" [id]
        (delete-key key-repo id)
        {:status 204}))

    (context "/keys/:id/values" [id]
      (PUT "/" [request]
        {:body {}})
      (GET "/" [request]
        {:body {}})
      (PATCH "/" [request]
        {:body {}})
      (DELETE "/" [request]
        {:body {}}))

    (context "/keys/:id/value" [id]
      (PUT "/" [request]
        {:body {}})
      (GET "/" [request]
        {:body {}})
      (PATCH "/" [request]
        {:body {}})
      (DELETE "/" [request]
        {:body {}}))

    (route/not-found {:status 400 :detail "Not Found"})))

(defn nil-not-found
  [handler]
  (fn [request]
    (let [response (handler request)]
      (if (and (nil? (:body response))
               (not= 204 (:status response)))
        ; TODO problem?!
        (assoc request :status 404)
        response))))

(defn problem-handling
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch Exception e
        (let [status 400]
          {:status status
           :body {:status status
                  :detail (.getMessage e)}})))))

(def app
  (-> app-routes
      handler/api
      ring-json/wrap-json-response
      ring-json/wrap-json-body
      nil-not-found
      problem-handling))
