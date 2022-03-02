<?php

require 'DBManager.php';

$usr = $_POST['usr'];
$pass = $_POST['pass'];
$name = $_POST['name'];

if($usr && $pass && $name) {
	$db = new DBManager();
	$json = $db->add($usr, $pass, $name);

	echo $json;
} else {
	die('{"code":403}');
}

?>