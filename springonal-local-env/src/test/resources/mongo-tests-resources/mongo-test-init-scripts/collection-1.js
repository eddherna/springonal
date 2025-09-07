db.createCollection('users', { capped: false });
db.users.insert([
    { "username": "username1"},
    { "username": "username2"},
    { "username": "username3"},
    { "username": "username4"}
]);