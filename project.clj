(defproject compass "0.1.0"
  :plugins [[lein-tools-deps "0.4.5"]
            [lein-ring "0.12.5"]]
  :middleware [lein-tools-deps.plugin/resolve-dependencies-with-deps-edn]
  :lein-tools-deps/config {:config-files [:install :user :project]}
  :ring {:handler compass.infrastructure.api/app
         :port 8080
         :auto-reload? true
         :auto-refresh? true})
