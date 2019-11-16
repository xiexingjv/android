var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');

let usersSchema = {uname: String, password: String, email: String};

mongoose.model("users", usersSchema);
let Users = mongoose.model('users');

/* GET users listing. */
/**
 * 201  该用户名已被注册
 * 202  该邮箱已被注册
 * 203  注册成功
 */
router.post('/', function (req, res, next) {
    let uname = req.body.uname;
    let password = req.body.password;
    let email = req.body.email;
    Users.find({"uname":uname}, function (err, user) {
        console.log(user);
        if(user.length!=0){
            res.send("201")
        }
        else {
            Users.find({"email":email},function (err,user) {
                if(user.length != 0) res.send("202")
                else {
                    console.log(uname,password,email);
                    Users.create({"uname":uname,"password":password,"email":email},function (err,user) {
                        res.send("203")
                    });
                }
            })
        }
    })

})

module.exports = router;
