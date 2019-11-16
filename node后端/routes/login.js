var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');


let Users = mongoose.model('users');

/* GET users listing. */
/**
 * 101  用户名不存在
 * 102  用户名或密码错误
 * 103  邮箱不存在
 * 104  邮箱或密码错误
 * 105  登录成功
 */
router.post('/', function (req, res, next) {
    let uname = req.body.uname;
    let password = req.body.password;
    let email = req.body.email;

    if (uname) {
        Users.find({"uname": uname},{"uname":0,"email":0,"_id":0,"__v":0}, function (err, users) {
            if (users.length == 0) {
                res.send("101");
            } else {
                if(users[0].password != password) res.send("102");
                else res.send(uname);
            }
        })
    }
    else {
        Users.find({"email": email},{"email":0,"_id":0,"__v":0}, function (err, users) {
            if (users.length == 0) {
                res.send("103");
            } else {
                console.log(users[0].password);
                if(users[0].password != password) res.send("104");
                else res.send(users[0].uname);
            }
        })
    }
})

module.exports = router;
