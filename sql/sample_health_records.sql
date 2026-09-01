USE health_db;

DELETE FROM `health_record`
WHERE `user_id` IN (3, 4);

INSERT INTO `health_record`
(`user_id`, `record_date`, `height`, `weight`, `bmi`, `systolic_pressure`, `diastolic_pressure`, `blood_sugar`, `heart_rate`, `remark`)
VALUES
(3, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 162.00, 52.00, 19.8, 105, 68, 4.90, 68, '饮食规律'),
(3, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 162.00, 52.30, 19.9, 108, 70, 5.10, 70, NULL),
(3, CURDATE(), 162.00, 52.10, 19.8, 106, 69, 5.00, 69, '晨间散步'),
(4, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 170.00, 78.00, 27.0, 135, 88, 6.20, 82, '工作压力较大'),
(4, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 170.00, 77.60, 26.9, 132, 86, 6.10, 80, NULL),
(4, CURDATE(), 170.00, 77.80, 26.9, 133, 87, 6.00, 81, '注意低盐饮食');
