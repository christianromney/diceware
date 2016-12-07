(ns diceware.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import [java.security SecureRandom])
  (:gen-class))

(def diceware-words
  "The filename of the Diceware word list in EDN format"
  "wordlist.edn")

(defn load-wordlist
  "Returns a future whose value will be a map 
  of die rolls to words"
  [fname]
  (-> fname io/resource slurp edn/read-string))

(defn die-rolls->word
  "Converts a sequence of 5 die rolls into a Diceware word"
  [word-list rolls]
  (let [k (apply str rolls)]
    (get word-list k)))

(defn infinite-die-rolls
  "Returns a lazy sequence of random die rolls"
  ([]
   (let [now (.toEpochMilli (java.time.Instant/now))
         rng (SecureRandom/getInstance "SHA1PRNG")]
     (.setSeed rng now)
     (infinite-die-rolls rng)))
  
  ([rng]
   (lazy-seq (cons (inc (.nextInt rng 6))
                   (infinite-die-rolls rng)))))

(defn generate-passphrase
  "Generates a passphrase of n words"
  [word-list n]
  (->> (infinite-die-rolls)
       (partition 5)
       (take n)
       (map (partial die-rolls->word word-list))
       (interpose " ")
       (apply str)))

(defn requested-phrase-length
  "Return the number of requested words in the phrase"
  [args]
  (Integer/parseInt (first args)))

(defn -main
  "Prints a diceware passphrase"
  [& args]
  (let [word-list (load-wordlist diceware-words)
        num-words (requested-phrase-length args)]
    (println (generate-passphrase word-list num-words))))
