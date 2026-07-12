-- 首页演示数据：仅用于本地展示，可重复执行
SET @demo_user_id = (SELECT id FROM user WHERE username = 'demo2026' LIMIT 1);
SET @walk_id = (SELECT id FROM sport_type WHERE name = '快走' LIMIT 1);
SET @yoga_id = (SELECT id FROM sport_type WHERE name = '瑜伽' LIMIT 1);

INSERT INTO sport_record (user_id, sport_type_id, duration_minutes, calories_burned, sport_date, remark)
SELECT @demo_user_id, @walk_id, 20, 100, CURDATE() - INTERVAL 2 DAY, '晚饭后的轻快步行'
WHERE @demo_user_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sport_record WHERE user_id=@demo_user_id AND remark='晚饭后的轻快步行');
INSERT INTO sport_record (user_id, sport_type_id, duration_minutes, calories_burned, sport_date, remark)
SELECT @demo_user_id, @yoga_id, 15, 52.5, CURDATE() - INTERVAL 1 DAY, '睡前舒展'
WHERE @demo_user_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sport_record WHERE user_id=@demo_user_id AND remark='睡前舒展');
INSERT INTO sport_record (user_id, sport_type_id, duration_minutes, calories_burned, sport_date, remark)
SELECT @demo_user_id, @walk_id, 25, 125, CURDATE(), '午后散步'
WHERE @demo_user_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sport_record WHERE user_id=@demo_user_id AND remark='午后散步');

INSERT INTO health_goal (user_id, goal_type, goal_name, target_value, current_value, unit, start_date, end_date, status)
SELECT @demo_user_id, 'exercise', '每周运动150分钟', 150, 60, '分钟', CURDATE() - INTERVAL 3 DAY, CURDATE() + INTERVAL 3 DAY, 0
WHERE @demo_user_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM health_goal WHERE user_id=@demo_user_id AND goal_name='每周运动150分钟');

INSERT IGNORE INTO daily_checkin (user_id, checkin_date, sport_record_id, checkin_type)
SELECT @demo_user_id, sport_date, id, 'sport' FROM sport_record
WHERE user_id=@demo_user_id AND remark IN ('晚饭后的轻快步行', '睡前舒展', '午后散步');
