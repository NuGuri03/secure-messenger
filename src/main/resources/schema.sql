CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    handle TEXT NOT NULL,
    public_key BLOB NOT NULL,
    nickname TEXT NOT NULL,
    bio TEXT DEFAULT "" NOT NULL
);

CREATE TABLE rooms (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE room_users (
    room_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    encrypted_key BLOB NOT NULL,

    PRIMARY KEY (room_id, user_id),
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE messages (
    id INTEGER PRIMARY KEY,
    room_id INTEGER NOT NULL,
    author_id INTEGER,
    created_at INTEGER DEFAULT (unixepoch()) NOT NULL,
    encrypted_content BLOB NOT NULL,

    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX index_messages ON messages(room_id, created_at);

-- 모든 INTEGER PRIMARY KEY는 랜덤한 8바이트 양수로 생성한다.
