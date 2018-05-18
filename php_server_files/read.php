<?php
error_reporting(0);
include("db_config.php");

// array for JSON response
$response = array();

// get all items from myorder table
$result = mysql_query("SELECT *FROM events") or die(mysql_error());

if (mysql_num_rows($result) > 0) {
 
    $response["my_testmyapp"] = array();

    while ($row = mysql_fetch_array($result)) {
            // temp user array
            $item = array();
            $item["id"] = $row["id"];
            $item["station"] = $row["station"];
       
            // push ordered items into response array
            array_push($response["my_testmyapp"], $item);
           }
      // success
     $response["success"] = 1;
}
else {
    // order is empty
      $response["success"] = 0;
      $response["message"] = "No Items Found";
}
// echoing JSON response
echo json_encode($response);

?> 