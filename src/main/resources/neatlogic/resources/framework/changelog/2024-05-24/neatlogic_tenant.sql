ALTER TABLE `region`
ADD COLUMN `upward_id_path` text NULL COMMENT '所有父节点id路径' AFTER `rht`;

ALTER TABLE `region`
DROP COLUMN `upward_name_pupward_name_path`,
ADD COLUMN `upward_name_path` text NULL COMMENT '所有父节点name路径' AFTER `upward_id_path`;