var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
const AipFaceClient = require("baidu-aip-sdk").face;
const fs = require("fs");
const FILE_PATH = "public/images/"
//文件上传中间件(指定上传的临时文件夹是/uploads)
const multer = require('multer')
let upload = multer({dest: 'uploads/'})
var ObjectId = mongoose.Schema.Types.ObjectId;


let BaiduUserSchema = {
    //ObjectId
    username: {type: String}
}

let BaiduImageSchema = {
    //ObjectId
    userId: ObjectId, //BaiduUser，只有创建人脸库的时候用
    //thumb: Buffer//太难可以不做
}

let BaiduFaceTokenSchema = {
    //ObjectId
    face_token: String,
    location_left: Number,
    location_top: Number,
    location_width: Number,
    location_height: Number,
    location_rotation: Number,
    destination: ObjectId, //BaiduUser,只有搜索人脸的时候用
    destination_score: Number,//同上
    source: ObjectId //BaiduImage
}

mongoose.model('BaiduUserSchema', BaiduUserSchema);
mongoose.model("BaiduImageSchema", BaiduImageSchema);
mongoose.model("BaiduFaceTokenSchema", BaiduFaceTokenSchema);

let UserSchema = mongoose.model('BaiduUserSchema');
let ImageSchema = mongoose.model('BaiduImageSchema');
let FaceTokenSchema = mongoose.model('BaiduFaceTokenSchema');

// 设置APPID/AK/SK
let APP_ID = "17656636";
let API_KEY = "nltSFMRQexWBUTYQ5OokGrTd";
let SECRET_KEY = "0KAVw0hbCZEvovAcyykdGDmh7npPSunQ";

// 新建一个对象，建议只保存一个对象调用服务接口
let client = new AipFaceClient(APP_ID, API_KEY, SECRET_KEY);

router.post('/addUser', function (req, res, next) {
    let username = req.body.username;
    //console.log(username);
    UserSchema.create({"username": username}, function (err, uname) {
        console.log(uname);
        res.send("username:" + username + ",userId:" + uname._id);
    })

})


//HttpServer服务的中间件(public目录下的index.html为首页)
router.use(express.static('public'))
//文件上传的处理（avatar是上传时的filedName）
router.post('/addImage', upload.single('file'), function (req, res, next) {
    //req.body是普通表单域
    //req.file是文件域

    console.log(req.body.userId);

    ImageSchema.create({"userId": mongoose.Types.ObjectId(req.body.userId)}, function (err, img) {
        //将临时文件上传到/public/images中
        console.log(img)

        let file = fs.readFileSync(req.file.path);

        let image = Buffer.from(file)
        let image64 = image.toString('base64');
        //...do your stuff...
        let imageType = "BASE64";
        client.addUser(image64, imageType, 'group1', req.body.userId).then(function (result) {
            //console.log(result);
            if (result.error_code == 0) {
                FaceTokenSchema.create({
                    "face_token": result.result.face_token,
                    "location_left": result.result.location.left,
                    "location_top": result.result.location.top,
                    "location_width": result.result.location.width,
                    "location_height": result.result.location.height,
                    "location_rotation": result.result.location.rotation,
                    "source": mongoose.Types.ObjectId(img._id)
                }, function (err, faceToken) {
                    console.log(faceToken);
                    res.send("OK")
                })
            } else {
                res.send(result)
            }


        }).catch(function (err) {
            // 如果发生网络错误
            console.log(err);
            res.send(err)
        });

        // such as write to file:
        fs.writeFile(FILE_PATH + img._id + ".jpg", image, function (err) {
            // handle error, return response, etc...
        });


    })
})


router.post('/search', upload.single('file'), function (req, res, next) {
    //req.body是普通表单域
    //req.file是文件域

    ImageSchema.create({}, function (err, img) {
        //将临时文件上传到/public/images中
        //console.log(img)

        let file = fs.readFileSync(req.file.path);

        let image = Buffer.from(file)
        let image64 = image.toString('base64');
        //...do your stuff...
        let imageType = "BASE64";
        // 如果有可选参数
        let options = {};
        options["face_field"] = "age";
        options["max_face_num"] = "10";
        options["face_type"] = "LIVE";
        options["liveness_control"] = "LOW";

        client.multiSearch(image64, imageType, 'group1', options).then(function (result) {
            //console.log(result.result.face_list[4]);
            //写数据库
            if (result.error_code == 0) {
                //console.log(result)
                num = result.result.face_num
                console.log(num)
                for (i = 0; i < num; i++) {
                    face = result.result.face_list[i]
                    FaceTokenSchema.create({
                        "face_token": face.face_token,
                        "location_left": face.location.left,
                        "location_top": face.location.top,
                        "location_width": face.location.width,
                        "location_height": face.location.height,
                        "location_rotation": face.location.rotation,
                        "destination": face.user_list.length != 0 ? mongoose.Types.ObjectId(face.user_list[0].user_id) : null,
                        "destination_score": face.user_list.length != 0 ? face.user_list[0].score : 0,
                        "source": mongoose.Types.ObjectId(img._id)
                    }, function (err, faceToken) {
                        console.log(faceToken);

                    })
                }
                res.send(result)
            } else {
                res.send(result)
            }

        }).catch(function (err) {
            // 如果发生网络错误
            console.log(err);
        });

        // such as write to file:
        fs.writeFile(FILE_PATH + img._id + ".jpg", image, function (err) {
            // handle error, return response, etc...
        });


    })
})

router.get('/photo', function (req, res, next) {
    ImageSchema.find({"userId": null}, function (err, user) {
        res.send(user)
    })
})

router.get('/group', function (req, res, next) {
    ImageSchema.aggregate([{$match: {userId: {$ne: null}}},{$group: {_id : "$userId",first: {$first : "$_id"}}}], function (err, data) {
        if (err) {
            res.send(err)
        }else {
            UserSchema.find({}, function (err1, users) {
                if (err1) {
                    res.send(err1)
                }else {
                    let groups = []
                    for (i in users){
                        let group = {}
                        group._id = users[i]._id
                        group.username = users[i].username
                        for (j in data){
                            if (users[i]._id.toString() == data[j]._id.toString()){
                                group.image = data[j].first
                            }
                        }
                        groups.push(group)
                    }
                    res.send(groups)
                }
            })
        }
    })
})

router.get('/image', function (req, res, next) {
    let id  = req.query.userId;
    console.log(id);
    ImageSchema.find({"userId": mongoose.Types.ObjectId(id)}, function (err, user) {
        res.send(user)
    })
})

router.get('/faceToken', function (req, res, next) {
    let id  = req.query.imageId;
    FaceTokenSchema.find({"source": mongoose.Types.ObjectId(id)}, function (err, faceToken) {
        res.send(faceToken)
    })
})


module.exports = router;
