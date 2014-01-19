<?php
// Create connection
$con=mysqli_connect("localhost","crowdapp","cs3282","crowd_db");

// Check connection
if (mysqli_connect_errno())
{
 	echo "Failed to connect to MySQL: " . mysqli_connect_error();
}
else{
	echo "success";
	echo "<br>";
    $result = mysqli_query($con,"SELECT * FROM route");
    while($row =mysqli_fetch_array($result))
    {
    	echo $row['id'] . " " . $row['name'] ." ". $row['type']."".$row['begin_time']." ".$row['end_time'];
    	echo "<br>"; }
    }
?>
