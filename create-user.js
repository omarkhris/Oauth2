db = db.getSiblingDB('userdb');
db.createUser({
    user: 'moon',
    pwd: 'moon123',
    roles: [
        { role: 'readWrite', db: 'userdb' }
    ]
});
