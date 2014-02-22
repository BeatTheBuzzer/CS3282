<?php
require_once("Rest.inc.php");

class API extends REST {

	public $data = "";

	const DB_SERVER = "localhost";
	const DB_USER = "crowdapp";
	const DB_PASSWORD = "cs3282";
	const DB = "crowdapp_db";

	private $db = NULL;

	public function __construct(){
		parent::__construct();				// Init parent contructor
		$this->dbConnect();					// Initiate Database connection
	}

	/*
	 *  Database connection 
	 */
	private function dbConnect(){
		$this->db = mysql_connect(self::DB_SERVER,self::DB_USER,self::DB_PASSWORD);
		if($this->db)
			mysql_select_db(self::DB,$this->db);
	}

	/*
	 * Public method for access api.
	 * This method dynmically call the method based on the query string
	 *
	 */
	public function processApi(){
		$func = strtolower(trim(str_replace("/","",$_REQUEST['rquest'])));
		if((int)method_exists($this,$func) > 0)
			$this->$func();
		else
			$this->response('',404);				// If the method not exist with in this class, response would be "Page not found".
	}


	/*
	 *	Encode array into JSON
	 */
	private function json($data){
		if(is_array($data)){
			return json_encode($data);
		}
	}

	private function users(){	
		// debugging purpose
		$sql = mysql_query("SELECT user_id, name FROM user", $this->db);
		if(mysql_num_rows($sql) > 0){
			$result = array();
			while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){
				$result[] = $rlt;
			}
			// If success everythig is good send header as "OK" and return list of users in JSON format
			$this->response($this->json($result), 200);
		}
		$this->response('',204);	// If no records "No Content" status
	}

	private function register(){
		$name = $this->_request['name'];
		$email = $this->_request['email'];
		$pwd = $this->_request['pwd'];
		$sql=mysql_query("INSERT INTO user (name,email,password) VALUES('$name', '$email','$pwd')",$this->db);
		if($sql){
			$this->response(("{\"Registration success\"}"), 200);
		}
		$this->response(("{\"Registration failed\"}"), 200);
	}

	private function login(){
		$email = $this->_request['email'];
		$pwd = $this->_request['pwd'];
		$sql=mysql_query("SELECT user_id FROM user WHERE email = '$email' AND password = '$pwd'",$this->db);
		if(mysql_num_rows($sql) > 0){
			$result = array();
			$result = mysql_fetch_array($sql,MYSQL_ASSOC);
			$this->response($this->json($result), 200);
		}
		$this->response(("{\"No such user\"}"), 200);
	}


	/* return all buses with their time in a particular bus stop
	   SELECT name, time FROM route,stop_time WHERE stop_time.stop_id='B18331' AND stop_time.route_id = route.route_id;
	 */
	private function businfo(){
		$stop_id=$this->_request['stop_id'];
		$query="SELECT schedule.route_id, name, time FROM route,schedule WHERE schedule.stop_id='$stop_id' AND schedule.route_id = route.route_id AND time >= subtime(curtime(),'00:05:00') AND time <= addtime(curtime(),'00:10:00')";
		$sql=mysql_query($query,$this->db);

		if(mysql_num_rows($sql) > 0){
			$result = array();
			while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){
				$result[] = $rlt;
			}
			// If success everythig is good send header as "OK" and return list of users in JSON format				
			$this->response($this->json($result), 200);
		}
		$this->response('',204);
	}
	/*
	   return all bus stops with their GPS coordinates
	 */
	private function allstops(){
		$query="SELECT stop_id, name, location, latitude, longitude FROM bus_stop";
		$sql=mysql_query($query,$this->db);
		if(mysql_num_rows($sql) > 0){
			$result = array();
			while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){
				$result[] = $rlt;
			}
			// If success everythig is good send header as "OK" and return list of users in JSON format				
			$this->response($this->json($result), 200);
		}
		$this->response('',204);
	}

	private function provide(){
		$route_id = $this -> _request['route_id'];;
		$stop_id = $this -> _request['stop_id'];
		$crowded=$this -> _request['crowded'];
		$query="INSERT INTO info (route_id, stop_id, time, date, crowded) VALUES ($route_id, '$stop_id', CURTIME(), CURDATE(), '$crowded')";
		$sql = mysql_query($query, $this->db);
		if($sql){
			$this->response(("{\"Success\"}"), 200);
		}
		$this->response(("{\"Failed\"}"), 200);
	}

	private function current(){
		$route_id = $this->_request['route_id'];
		$stop_id = $this -> _request['stop_id'];
		$table = 'bus95';
		$result = array();
		$query = "SELECT listofstops FROM route WHERE route_id= $route_id";
		$sql=mysql_query($query,$this->db);
		if(mysql_num_rows($sql) > 0){

			while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){

				$table = $rlt['listofstops'];
			}
		}
		//$this->response($this->json($result), 200);

		$query = "SELECT stop_id, crowded, SUBTIME(CURTIME(), time) AS difference from info where stop_id = (select stop_id from $table where idx = (select (idx-1) from bus95 where stop_id='$stop_id')) AND date = CURDATE() AND time <= CURTIME() LIMIT 1";
		$sql=mysql_query($query,$this->db);
		if($sql){
			if(mysql_num_rows($sql) > 0){

				while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){
					$result[] = $rlt;
				}
				// If success everythig is good send header as "OK" and return list of users in JSON format				
				$this->response($this->json($result), 200);
			}
			$this->response(("{\"failed\"}"),200);
		}
	}

	private function history(){
		$route_id = $this->_request['route_id'];
		$stop_id = $this -> _request['stop_id'];

		$query ="SELECT (SELECT COUNT(1) FROM info WHERE route_id = $route_id AND stop_id ='$stop_id' AND date < curdate() AND time < addtime ( curtime(), '00:10:00') AND crowded ='yes' AND time > subtime(curtime(),'00:05:00')) AS yes, (SELECT COUNT(1) FROM info WHERE route_id = $route_id AND stop_id ='$stop_id' AND date < curdate() AND time < addtime ( curtime(), '00:10:00') AND time > subtime(curtime(),'00:05:00') AND crowded = 'no') AS no";
		$sql=mysql_query($query,$this->db);
		if($sql){
			if(mysql_num_rows($sql) > 0){

				while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){
					$result[] = $rlt;
				}
				// If success everythig is good send header as "OK" and return list of users in JSON format				
				$this->response($this->json($result), 200);
			}
			$this->response('',204);
		}
	}

}
// Initiiate Library

$api = new API;
$api->processApi();
?>