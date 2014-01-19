<?php
// Create connection
$con=mysqli_connect("localhost","crowdapp","cs3282","crowd_db");

// Check connection
if (mysqli_connect_errno())
{
 	echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
else{
//connection successfull
	echo "success";
	echo "<br>";
    $result = mysqli_query($con,"SELECT * FROM route");
    
    //echo $result->num_rows; echo "<br>";

    
    echo "[";
    
    for($i=1;$i<=($result->num_rows);$i++){
    	$row =mysqli_fetch_array($result);
    	$arr=array('name'=>$row['name'],'type'=>$row['type'],'begin_time'=>$row['begin_time'],'end_time'=>$row['end_time']);
    	//echo $row['name'] ." ". $row['type']." ".$row['begin_time']." ".$row['end_time']
    	echo json_encode(($arr));
    	
    	if($i!=$result->num_rows){
			echo ",";
		}
    }
    
    echo "]";
    
}
?>
