<?php

require 'DBManager.php';

$usr = $_POST['usr'];
$amount = $_POST['amount'];
$product = $_POST['product'];

if($usr && $product && $amount) {
	$db = new DBManager();
	$json = $db->bid($amount, $usr, $product);
	echo $json;
} else {
	die('{"code":403}');
}

?>