(ns quartz-task.core
  (:require [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.jobs :as j]
            [clojurewerkz.quartzite.triggers :as t]
            [clojurewerkz.quartzite.schedule.cron :refer [schedule cron-schedule]]))


(defmacro defjob [job-name job-desc]
  `(let [record# (defrecord ~job-name []
                   org.quartz.StatefulJob
                   (execute [this context]
                     (~(:handler job-desc))))
         job# (j/build (j/of-type record#)
                       (j/with-identity (j/key (.getName record#))))
         trigger# (t/build
                   (t/with-identity (t/key (str (.getName record#) "-trigger")))
                   (t/start-now)
                   (t/with-schedule (schedule (cron-schedule ~(:schedule job-desc)))))]

     [job# trigger#]))


(defmacro deftasks [tasks]
  (let [job-tasks (for [task tasks]
                    (do
                      `(defjob ~(:id task) ~task)))]
    (into [] job-tasks)))


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
