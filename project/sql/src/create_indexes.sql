-- Indexes for use in database-messenger
-- Indexes for USER_LIST
CREATE INDEX ul_id ON USER_LIST (list_id);
CREATE INDEX ul_type ON USER_LIST (list_type);

-- Indexes for USR
CREATE INDEX u_login ON USR (login);
CREATE INDEX u_phone ON USR (phoneNum);
CREATE INDEX u_pass ON USR (password);
CREATE INDEX u_contact ON USR (contact_list);
CREATE INDEX u_block ON USR (block_list);

-- Indexes for USER_LIST_CONTAINS
CREATE INDEX ulc_id ON USER_LIST_CONTAINS (list_id);
CREATE INDEX ulc_member ON USER_LIST_CONTAINS (list_member);

-- Indexes for CHAT
CREATE INDEX c_id ON CHAT (chat_id);
CREATE INDEX c_type ON CHAT (chat_type);
CREATE INDEX c_is ON CHAT (init_sender);

-- Indexes for CHAT_LIST
CREATE INDEX cl_id ON CHAT_LIST (chat_id);
CREATE INDEX cl_member ON CHAT_LIST (member);

-- Indexes for MESSAGE
CREATE INDEX m_id ON MESSAGE (msg_id);
CREATE INDEX m_time ON MESSAGE (msg_timestamp);
CREATE INDEX m_sender ON MESSAGE (sender_login);
CREATE INDEX m_cid ON MESSAGE (chat_id);
