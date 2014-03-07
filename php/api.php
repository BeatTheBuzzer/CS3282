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
	 *  Initial Database connection 
	 */
	private function dbConnect(){
		$this->db = mysql_connect(self::DB_SERVER,self::DB_USER,self::DB_PASSWORD);
		if($this->db)
			mysql_select_db(self::DB,$this->db);
	}

	/*
	 * Public method for process the api.
	 * This method dynmically call the method based on the query string
	 *
	 */
	public function processApi(){
		$func = strtolower(trim(str_replace("/","",$_REQUEST['rquest'])));
		// If the method exists
		if((int)method_exists($this,$func) > 0)
			$this->$func();
		// If the method not exist with in this class, response would be "Page not found".
		else
			$this->response('',404);
	}


	/*
	 *	Encode the data array into JSON
	 */
	private function json($data){
		if(is_array($data)){
			return json_encode($data);
		}
	}

	/*
	 *	Used for debugging purpose
	 */
	private function users(){	
		
		$sql = mysql_query("SELECT user_id, name FROM user", $this->db);
		if(mysql_num_rows($sql) > 0){
			$result = array();
			while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){
				//	append to the result array
				$result[] = $rlt;
			}
			// If query is success and everythig is good, send header as "OK" and return list of users in JSON format
			$this->response($this->json($result), 200);
		}
		// If no records "No Content" status
		$this->response('',204);
	}

	/*
	 *	register an account, parameter: name, email and password
	 *	Return: {"result":"Registration success"} or {"result":"Registration failed"}
	 */
	private function register(){
		$name = mysql_real_escape_string($this -> _request['name']);
		$email = mysql_real_escape_string($this -> _request['email']);
		$pwd = mysql_real_escape_string($this -> _request['pwd']);
		$sql=mysql_query("INSERT INTO user (name,email,password) VALUES('$name', '$email','$pwd')",$this->db);
		if($sql){
			$this->response(("{\"result\":\"Registration success\"}"), 200);
		}
		$this->response(("{\"result\":\"Registration failed\"}"), 200);
	}

	/*
	 *	Login, parameter: email and password
	 *	Return: if login is successfule, return user_id,else return {"result":"No such user"}
	 */
	private function login(){
		$email = mysql_real_escape_string($this -> _request['email']);
		$pwd = mysql_real_escape_string($this -> _request['pwd']);
		$sql=mysql_query("SELECT user_id FROM user WHERE email = '$email' AND password = '$pwd'",$this->db);
		if(mysql_num_rows($sql) > 0){
			$result = array();
			$result = mysql_fetch_array($sql,MYSQL_ASSOC);
			$this->response($this->json($result), 200);
		}
		$this->response(("{\"result\":\"No such user\"}"), 200);
	}


	/* 
	 *	Parameter: stop_id
	 *	Return all buses with their time in a particular bus stop
	 */
	private function businfo(){
		$stop_id = mysql_real_escape_string($this -> _request['stop_id']);
		$query = "SELECT schedule.route_id, name, time FROM route,schedule WHERE schedule.stop_id='$stop_id' AND schedule.route_id = route.route_id AND time >= curtime() GROUP BY route_id ORDER BY time";
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
	 *	Return all bus stops with their GPS coordinates
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
	/*
	 * Provide crowdedness info to the database
	 */
	private function provide(){
		$route_id = mysql_real_escape_string($this -> _request['route_id']);
		$stop_id = mysql_real_escape_string($this -> _request['stop_id']);
		$crowded = mysql_real_escape_string($this -> _request['crowded']);
		$query="INSERT INTO info (route_id, stop_id, time, date, crowded) VALUES ($route_id, '$stop_id', CURTIME(), CURDATE(), '$crowded')";
		$sql = mysql_query($query, $this->db);
		if($sql){
			$this->response(("{\"result\":\"Success\"}"), 200);
		}
		$this->response(("{\"result\":\"Failed\"}"), 200);
	}

	/*
	 *	Return crowdedness info from the data posted in the current day.
	 */
	private function current(){
		$route_id = mysql_real_escape_string($this -> _request['route_id']);
		$stop_id = mysql_real_escape_string($this -> _request['stop_id']);
		$table = 'bus95';
		$result = array();

		// Get the mapping table from bus to its route
		$query = "SELECT listofstops FROM route WHERE route_id= $route_id";
		$sql=mysql_query($query,$this->db);
		if(mysql_num_rows($sql) > 0){

			while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){

				$table = $rlt['listofstops'];
			}
		}

		//	Get the info of the previous stop		
		$query = "SELECT info.stop_id,bus_stop.name, crowded, SUBTIME(CURTIME(), time) AS difference from info, bus_stop where info.stop_id = bus_stop.stop_id AND info.stop_id = (SELECT stop_id FROM $table WHERE idx = (SELECT (idx-1) FROM $table WHERE stop_id='$stop_id')) AND date = CURDATE() AND time <= CURTIME() ORDER BY difference LIMIT 1";
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

	/*
	 *	Return the historical data of the crowdedness info
	 */
	private function history(){
		$route_id = mysql_real_escape_string($this -> _request['route_id']);
		$stop_id = mysql_real_escape_string($this -> _request['stop_id']);

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

	private function historicaldata(){
		$route_id = mysql_real_escape_string($this -> _request['route_id']);
		$stop_id = mysql_real_escape_string($this -> _request['stop_id']);
		$left = mysql_real_escape_string($this -> _request['left']);
		$right = mysql_real_escape_string($this -> _request['right']);
		$query="";
		for ($i=1;$i<=7;$i++){
			$query ="SELECT (SELECT SUBDATE(CURDATE(),INTERVAL $i DAY)) AS Date, (SELECT COUNT(1) FROM info WHERE route_id = $route_id AND stop_id ='$stop_id' AND date = SUBDATE(CURDATE(),INTERVAL $i DAY) AND crowded ='yes' AND time <= ADDTIME(CURTIME(),'$right') AND time >= SUBTIME(CURTIME(),'$left')) AS YES, (SELECT COUNT(1) FROM info WHERE route_id = $route_id AND stop_id ='$stop_id' AND date = SUBDATE(CURDATE(),INTERVAL $i DAY) AND crowded='no' AND time <= ADDTIME(CURTIME(),'$right') AND time >= SUBTIME(curtime(),'$left')) AS NO";
			$sql=mysql_query($query,$this->db);
			if($sql){
				if(mysql_num_rows($sql) > 0){

					while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){
						$result[] = $rlt;
					}
					// If success everythig is good send header as "OK" and return list of users in JSON format				
					
				}
				
			}
			
		}	
		$this->response($this->json($result), 200);
		
	}
	/*
	 *	Return the bus name according to the stop id
	 */

	private function busname(){

		$stop_id = mysql_real_escape_string($this->_request['stop_id']);
		$query = "SELECT name FROM bus_stop WHERE stop_id='$stop_id'";
		$sql=mysql_query($query,$this->db);
		if($sql){
			if(mysql_num_rows($sql) > 0){
				while($rlt = mysql_fetch_array($sql,MYSQL_ASSOC)){
					$result[]=$rlt;
				}
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
