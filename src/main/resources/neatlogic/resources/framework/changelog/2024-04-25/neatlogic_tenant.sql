ALTER TABLE `form_extend_attribute`
DROP PRIMARY KEY,
  ADD PRIMARY KEY (`form_uuid`, `formversion_uuid`, `tag`, `uuid`);