var express = require('express');
var formidable = require('formidable');
var db = require('../db')
var router = express.Router();

//member/:phone
router.get('/:phone', function(req, res, next) {
  var phone = req.params.phone;
  
  var sql = "select * " +
            "from bestfood_member " + 
            "where phone = ? limit 1;";  
  console.log("sql : " + sql);    
	
	db.get().query(sql, phone, function (err, rows) {
      console.log("rows : " + JSON.stringify(rows));
      console.log("row.length : " + rows.length);
      if (rows.length > 0) {
        res.status(200).json(rows[0]);
      } else {
        res.sendStatus(400);
      }
  });
});

//member/phone
router.post('/phone', function(req, res) {
  var phone = req.body.phone;

  var sql_count = "select count(*) as cnt " +
            "from bestfood_member " + 
            "where phone = ?;";  
  console.log("sql_count : " + sql_count);

  var sql_insert = "insert into bestfood_member (phone) values(?);";
    
  db.get().query(sql_count, phone, function (err, rows) {
    console.log(rows);
    console.log(rows[0].cnt);

    if (rows[0].cnt > 0) {
      return res.sendStatus(400);
    }

    db.get().query(sql_insert, phone, function (err, result) {
      console.log(err);
      if (err) return res.sendStatus(400);
      res.status(200).send('' + result.insertId);
    });
  });
});

//member/info
router.post('/info', function(req, res) {
  var phone = req.body.phone;
  var name = req.body.name;
  var sextype = req.body.sextype;
  var birthday = req.body.birthday;

  console.log({name, sextype, birthday, phone});

  var sql_count = "select count(*) as cnt " +
            "from bestfood_member " + 
            "where phone = ?;";

  var sql_insert = "insert into bestfood_member (phone, name, sextype, birthday) values(?, ?, ?, ?);";
  var sql_update = "update bestfood_member set name = ?, sextype = ?, birthday = ? where phone = ?; ";
  var sql_select = "select seq from bestfood_member where phone = ?; ";
  
  db.get().query(sql_count, phone, function (err, rows) {
    if (rows[0].cnt > 0) {
      console.log("sql_update : " + sql_update);

      db.get().query(sql_update, [name, sextype, birthday, phone], function (err, result) {
        if (err) return res.sendStatus(400);
        console.log(result);

        db.get().query(sql_select, phone, function (err, rows) {
          if (err) return res.sendStatus(400);

          res.status(200).send('' + rows[0].seq);
        });
      });
    } else {
      console.log("sql_insert : " + sql_insert);

      db.get().query(sql_insert, [phone, name, sextype, birthday], function (err, result) {
        if (err) return res.sendStatus(400);

        res.status(200).send('' + result.insertId);
      });
    }
  });
});

//member/icon_upload
router.post('/icon_upload', function (req, res) {
  var form = new formidable.IncomingForm();

  form.on('fileBegin', function (name, file){
    file.path = './public/member/' + file.name;
  });

  form.parse(req, function(err, fields, files) {
    var sql_update = "update bestfood_member set member_icon_filename = ? where seq = ?;";

    db.get().query(sql_update, [files.file.name, fields.member_seq], function (err, rows) {
      res.sendStatus(200);
    });
  });
});


module.exports = router;