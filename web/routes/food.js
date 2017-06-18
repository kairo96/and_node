var express = require('express');
var formidable = require('formidable');
var db = require('../db')
var router = express.Router();

var LOADING_SIZE = 20;
var DEFAULT_USER_LATITUDE = 37.566229;
var DEFAULT_USER_LONGITUDE = 126.977689;

//food/info
router.post('/info', function(req, res, next) {
  if (!req.body.member_seq) {
    return res.sendStatus(400);
  }

  var member_seq = req.body.member_seq;
  var name = req.body.name;
  var tel = req.body.tel;
  var address = req.body.address;
  var latitude = req.body.latitude;
  var longitude = req.body.longitude;
  var description = req.body.description;


  var sql_insert = 
    "insert into bestfood_info (member_seq, name, tel, address, latitude, longitude, description) " +
    "values(?, ?, ?, ?, ?, ?, ?); ";

  console.log(sql_insert);

  var params = [member_seq, name, tel, address, latitude, longitude, description];

  db.get().query(sql_insert, params, function (err, result) {
    console.log(result.insertId);
    res.status(200).send('' + result.insertId);
  });
});


//food/info/image
router.post('/info/image', function (req, res) {
  var form = new formidable.IncomingForm();

  form.on('fileBegin', function (name, file){    
    file.path = './public/img/' + file.name;
  });

  form.parse(req, function(err, fields, files) {
    var sql_insert = "insert into bestfood_info_image (info_seq, filename, image_memo) values (?, ?, ?);";

    db.get().query(sql_insert, [fields.info_seq, files.file.name, fields.image_memo], function (err, rows) {
      res.sendStatus(200);
    });
  });
});

//food/info/:seq
router.get('/info/:seq', function(req, res, next) {
  var seq = req.params.seq;
  var member_seq = req.query.member_seq;
  
  var sql = 
    "select a.*, " +
    "  '0' as user_distance_meter, " +
    "  if( exists(select * from bestfood_keep where member_seq = ? and a.seq = info_seq), 'true', 'false') as is_keep, " +
    "  (select filename from bestfood_info_image where info_seq = a.seq order by seq limit 1) as image_filename " +            
    "from bestfood_info as a " +
    "where seq = ? ; ";
  console.log("sql : " + sql);
    
  db.get().query(sql, [member_seq, seq], function (err, rows) {
      if (err) return res.sendStatus(400);;

      console.log("rows : " + JSON.stringify(rows));
      res.json(rows[0]);
  });      
});

//food/list
router.get('/list', function(req, res, next) {
  var member_seq = req.query.member_seq;
  var user_latitude = req.query.user_latitude || DEFAULT_USER_LATITUDE;
  var user_longitude = req.query.user_longitude || DEFAULT_USER_LONGITUDE;
  var order_type = req.query.order_type;
  var current_page = req.query.current_page || 0;

  if (!member_seq) {
    return res.sendStatus(400);
  }

  var order_add = '';

  if (order_type) {
    order_add = order_type + ' desc, user_distance_meter';
  } else {
    order_add = 'user_distance_meter';
  }

  var start_page = current_page * LOADING_SIZE;

  
  var sql = 
    "select a.*, " +
    "  (( 6371 * acos( cos( radians(?) ) * cos( radians( latitude ) ) * cos( radians( longitude ) - radians(?) )  " +
    "  + sin( radians(?) ) * sin( radians( latitude ) ) ) ) * 1000) AS user_distance_meter, " +
    "  if( exists(select * from bestfood_keep where member_seq = ? and info_seq = a.seq), 'true', 'false') as is_keep, " +
    "  (select filename from bestfood_info_image where info_seq = a.seq) as image_filename " +
    "from bestfood_info as a " +
    "order by  " + order_add + " " +
    "limit ? , ? ; ";
  console.log("sql : " + sql);
  console.log("order_add : " + order_add);

  var params = [user_latitude, user_longitude, user_latitude, member_seq, start_page, LOADING_SIZE];

  db.get().query(sql, params, function (err, rows) {
      if (err) return res.sendStatus(400);

      console.log("rows : " + JSON.stringify(rows));      
      res.status(200).json(rows);
  });
});

//food/map/list
router.get('/map/list', function(req, res, next) {
  var member_seq = req.query.member_seq;
  var latitude = req.query.latitude;
  var longitude = req.query.longitude;
  var distance = req.query.distance;
  var user_latitude = req.query.user_latitude || DEFAULT_USER_LATITUDE;
  var user_longitude = req.query.user_longitude || DEFAULT_USER_LONGITUDE;
  
  if (!member_seq || !latitude || !longitude) {      
      return res.sendStatus(400);
  }

  var sql = 
    "select a.*, " + 
    "  (( 6371 * acos( cos( radians(?) ) * cos( radians( latitude ) ) * cos( radians( longitude ) - radians(?) ) " + 
    "  + sin( radians(?) ) * sin( radians( latitude ) ) ) ) * 1000) AS distance_meter," + 
    "  (( 6371 * acos( cos( radians(?) ) * cos( radians( latitude ) ) * cos( radians( longitude ) - radians(?) ) " + 
    "  + sin( radians(?) ) * sin( radians( latitude ) ) ) ) * 1000) AS user_distance_meter," + 
    "  IF(EXISTS (select * from bestfood_keep where member_seq = ? and a.seq = info_seq), 'true', 'false') as is_keep," + 
    "  (select filename from bestfood_info_image where info_seq = a.seq) as image_filename " + 
    "from bestfood_info as a " + 
    "having distance_meter <= ? " + 
    "order by user_distance_meter ";
  console.log("sql : " + sql);  
    
  var params = [latitude, longitude, latitude, user_latitude, user_longitude, user_latitude, member_seq, distance];

  db.get().query(sql, params, function (err, rows) {
      if (err) return res.sendStatus(400);

      console.log("rows : " + JSON.stringify(rows));      
      res.status(200).json(rows);
  });
});

module.exports = router;