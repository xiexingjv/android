var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');


let Users = mongoose.model('users');

/* GET users listing. */
/**
 * 301  原密码错误
 * 302  更改密码成功
 */

router.post('/', function (req, res, next) {
    let uname = req.body.uname;
    let npassword = req.body.npassword;
    let password = req.body.password;
    console.log(uname,npassword);

    Users.find({"uname": uname}, {"uname": 0, "email": 0, "_id": 0, "__v": 0}, function (err, users) {
        if (users[0].password != password) {
            res.send("301");
        } else {
            Users.update({"uname": uname}, {"password": npassword}, function (err, user) {
                res.send("302");
            });


        }
    })

})

module.exports = router;
