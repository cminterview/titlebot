db = db.getSiblingDB('titlebot');
db.createUser({
    user: 'root',
    pwd: 'example',
    roles: [{role: 'readWrite', db: 'titlebot'}]
});
db.createCollection('pages');