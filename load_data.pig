-- load_data.pig
grades = LOAD 'pig_data.csv' USING PigStorage(',') AS (student_id:chararray, course_id:chararray, grade:chararray);
DUMP grades;

