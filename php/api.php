<?php
require_once("Rest.inc.php");

class API extends REST {

	public $data = "";

	const DB_SERVER = "localhost";
	const DB_USER = "crowdapp";
	const DB_PASSWORD = "cs3282";
	const DB = "crowd_db";

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

	private function users(){	
		// Cross validation if the request method is GET else it will return "Not Acceptable" status
		//if($this->get_request_method() != "GET"){
		//	$this->response('',406);
		//}
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

	private function lookup(){
		$type = $this->_request['type'];
		$number = $this->_request['number'];
		$time = $this->_request['time'];
		$location = $this->_request['location'];
		$sql = mysql_query("SELECT post_id, author_id, content FROM post WHERE stop_time_id = (SELECT stop_time_id FROM stop_time WHERE time = '$time' AND route_id = (SELECT route_id FROM route WHERE name='$number') AND stop_id = '$location')", $this->db);

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

	private function rate(){
		$author_id = $this->_request['author_id'];
		$reviewer_id = $this->_request['reviewer_id'];
		$post_id = $this->_request['post_id'];
		$rate = $this->_request['rate'];
		$sql=mysql_query("INSERT INTO rating (author_id, reviewer_id, post_id, rate) VALUES($author_id, $reviewer_id, $post_id, $rate)",$this->db);
		if($sql){
			$this->response(("{\"Rating success\"}"), 200);
		}
		$this->response(("{\"Rating failed\"}"), 200);
	}



	private function json_string($data){
		$result = array();
		$result[] = $data;
		return json_encode($result);
	}
	/*
	 *	Encode array into JSON
	 */
	private function json($data){
		if(is_array($data)){
			return json_encode($data);
		}
	}


	/* return all buses with their time in a particular bus stop
	   SELECT name, time FROM route,stop_time WHERE stop_time.stop_id='B18331' AND stop_time.route_id = route.route_id;
	 */
	private function businfo(){
		$stop_id=$this->_request['stop_id'];
		$query="SELECT name, time FROM route,stop_time WHERE stop_time.stop_id='$stop_id' AND stop_time.route_id = route.route_id AND time >= subtime(curtime(),'00:05:00') AND time <= addtime(curtime(),'00:10:00')";
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
		$user_id = $this -> _request['user_id'];
		$time = $this -> _request['time'];
		$content = $this -> _request['content'];
		$number = $this -> _request['number'];
		$stop = $this -> _request['stop'];

		$sql=mysql_query("SELECT route_id FROM route WHERE name = '$number'",$this->db);
		if(mysql_num_rows($sql) > 0){
			$result = array();
			$result[] = mysql_fetch_array($sql,MYSQL_ASSOC);

			$route_id = $result[0];
			$route_id = $route_id['route_id'];
			//$this->response($this->json_string($route_id), 200);
			$sql=mysql_query("SELECT stop_time_id FROM stop_time WHERE route_id = $route_id AND stop_id = '$stop' AND time = '$time'",$this->db);
			//$this->response($this->json($sql2), 200);
			if(mysql_num_rows($sql) > 0){
				$result = array();
				$result[] = mysql_fetch_array($sql,MYSQL_ASSOC);
				$stop_time_id = $result[0];
				$stop_time_id = $stop_time_id['stop_time_id'];
				//$this->response($this->json_string("success"), 200);
				//$this->response($this->json_string($stop_time_id), 200);
			}else{
				mysql_query("INSERT INTO stop_time (route_id, stop_id, time) VALUES($route_id, '$stop', '$time')",$this->db);
				$sql=mysql_query("SELECT stop_time_id FROM stop_time WHERE route_id = $route_id AND stop_id = '$stop' AND time = '$time'",$this->db);
				//$this->response($this->json($sql2), 200);
				if(mysql_num_rows($sql) > 0){
					$result = array();
					$result[] = mysql_fetch_array($sql,MYSQL_ASSOC);
					$stop_time_id = $result[0];
					$stop_time_id = $stop_time_id['stop_time_id'];
					//$this->response($this->json_string("success"), 200);
					//$this->response($this->json_string($stop_time_id), 200);
				}
			}
			//$this->response($this->json_string($route_id), 200);
			$sql4 = mysql_query("INSERT INTO post (time, content, author_id,stop_time_id,route_id) VALUES('$time','$content', $user_id,'$stop_time_id', $route_id)",$this->db);
			if($sql4){
				$this->response(("{\"Providing info success\"}"), 200);
			}
		}
		$this->response(("{\"Providing info failed\"}"), 200);

	}
}
// Initiiate Library

$api = new API;
$api->processApi();
?>
