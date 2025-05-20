(ns migration.parser-test
  (:require [clojure.test :refer :all]
            [migration.parser :refer :all]))

(deftest test-line-concat
  (testing "concat ok"
    (is (= "hello\nworld\n" (line-concat ["hello" "world"])))))

(deftest test-parse-migration-number
  (testing "found"
    (is (= 1 (parse-migration-number "001_test.sql"))))
  (testing "not found"
    (is (nil? (parse-migration-number "no_numbers.sql")))))

(deftest test-get-migration-operation
  (testing "up migration"
    (is (= :up (get-migration-operation 2 3))))
  (testing "down migration"
    (is (= :down (get-migration-operation 4 3)))))

(deftest test-parse-up-down
  (testing "up, down, and fixtures tags"
    (let [parsed (parse-up-down "SELECT 1;\n-- up\nSELECT 1;\n-- down\nSELECT 1;\n-- fixtures\nSELECT 1;\n")]
      (is (= "-- up\nSELECT 1;\n" (:up parsed)))
      (is (= "-- down\nSELECT 1;\n" (:down parsed)))
      (is (= "-- fixtures\nSELECT 1;\n" (:fixtures parsed))))))
