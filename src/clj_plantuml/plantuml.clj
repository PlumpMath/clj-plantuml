(ns clj-plantuml.plantuml
  (:require
   [clojure.core.memoize :as m])
  (:import
   (java.io ByteArrayOutputStream)
   (net.sourceforge.plantuml FileFormat FileFormatOption SourceStringReader)))

(defn- type-from-extension [extension]
  (case extension
    "png" FileFormat/PNG
    "svg" FileFormat/SVG))

(defn- render [source extension]
  (let [reader (SourceStringReader. source)]
    (with-open [outputStream (ByteArrayOutputStream.)]
      (.generateImage reader outputStream
                      (FileFormatOption. (type-from-extension extension)))
      (.toByteArray outputStream))))

(def render-cached (m/lu render :lu/threshold 400))
