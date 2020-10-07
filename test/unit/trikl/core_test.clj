(ns trikl.core-test
  (:require [trikl.core :as t]
            [clojure.test :refer :all]
            [lambdaisland.ansi :as ansi]))

;; TODO: a bunch of tests passed after substituting `{:row 1, :col 1}` with `nil`; why?

(deftest basic-tests
  (is (=
       (let [sb (StringBuilder.)
             styles (t/diff-row
                     sb
                     0
                     {}
                     [t/BLANK]
                     [(t/map->Charel {:char \x})])]
         [(ansi/token-stream (str sb)) styles])

       ;; Expected
       [[nil "x"] {}]
       ))

  (is (=
       (let [sb (StringBuilder.)
             styles (t/diff-row
                     sb
                     0
                     {}
                     [(t/map->Charel {:char \x})]
                     [(t/map->Charel {:char \x})])]
         [(ansi/token-stream (str sb)) styles])

       ;; Expected
       [[] {}]
       ))

  (is (=
       (let [sb (StringBuilder.)
             styles (t/diff-row
                     sb
                     0
                     {}
                     [t/BLANK]
                     [(t/map->Charel {:char \y :fg [10 20 30]})])]
         [(ansi/token-stream (str sb)) styles])

       ;; Expected
       [[nil
         {:foreground [:rgb 10 20 30]}
         "y"]
        #trikl.core.Charel{:char \y, :fg [10 20 30], :bg nil}]))

  (is (=
       (let [sb (StringBuilder.)
             styles (t/diff-row
                     sb
                     0
                     {}
                     [t/BLANK t/BLANK]
                     [(t/map->Charel {:char \x :fg [10 20 30]})
                      (t/map->Charel {:char \y :fg [10 20 30]})])]
         [(ansi/token-stream (str sb)) styles])
       [[nil
         {:foreground [:rgb 10 20 30]}
         "xy"]
        #trikl.core.Charel{:char \x, :fg [10 20 30], :bg nil}]))


  (is (=
       (let [sb (StringBuilder.)
             styles (t/diff-row
                     sb
                     0
                     {}
                     []
                     [(t/map->Charel {:char \x :fg [10 20 30]})
                      (t/map->Charel {:char \y :fg [10 20 30]})])]
         [(ansi/token-stream (str sb)) styles])

       [[nil
         {:foreground [:rgb 10 20 30]}
         "xy"]
        #trikl.core.Charel{:char \x, :fg [10 20 30], :bg nil}]))


  (is (=
       (let [sb (StringBuilder.)
             styles (t/diff-row
                     sb
                     0
                     {}
                     [(t/map->Charel {:char \x :fg [10 20 30]})
                      (t/map->Charel {:char \y :fg [10 20 30]})]
                     [])]
         [(ansi/token-stream (str sb)) styles])
       [[nil " "] {}]))

  (is (=
       (let [sb (StringBuilder.)
             styles (t/diff-row
                     sb
                     0
                     {}
                     [t/BLANK t/BLANK t/BLANK]
                     [(t/map->Charel {:char \x :fg [10 20 30]})
                      t/BLANK
                      (t/map->Charel {:char \y :fg [10 20 30]})])]
         [(ansi/token-stream (str sb)) styles])

       [[nil
         {:foreground [:rgb 10 20 30]}
         "x"
         nil
         "y"]
        #trikl.core.Charel{:char \x, :fg [10 20 30], :bg nil}])))


;; Manual Benchmarks
(t/time-info
 (let [sb (StringBuilder.)
       styles (t/diff-row
               sb
               0
               {}
               [t/BLANK t/BLANK t/BLANK]
               [(t/map->Charel {:char \x :fg [10 20 30]})
                t/BLANK
                (t/map->Charel {:char \y :fg [10 20 30]})])]
   [(ansi/token-stream (str sb)) styles])
 "")
