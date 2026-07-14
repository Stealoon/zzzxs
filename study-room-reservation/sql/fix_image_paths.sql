-- 修正缺少 /upload/ 前缀的图片路径数据
-- 适用于从外卖项目迁移后部分图片路径不一致的情况

-- 修正 reservation_package 表中缺少 /upload/ 前缀的图片路径
UPDATE reservation_package 
SET package_image = CONCAT('/upload/', package_image) 
WHERE package_image IS NOT NULL 
AND package_image != '' 
AND package_image NOT LIKE '/upload/%';

-- 修正 seat 表中缺少 /upload/ 前缀的图片路径
UPDATE seat 
SET seat_image = CONCAT('/upload/', seat_image) 
WHERE seat_image IS NOT NULL 
AND seat_image != '' 
AND seat_image NOT LIKE '/upload/%';
