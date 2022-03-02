<?php

class DBManager {
	private $db;
	private $host;
	private $user;
	private $pass;

	function __construct() {
		$this->db = 'proyecto3';
		$this->host = '127.0.0.1';
		$this->user = 'root';
		$this->pass = '';
	}

	private function open() {
		$link = mysqli_connect(
			$this->host, 
			$this->user, 
			$this->pass, 
			$this->db
		) or die('Error connecting to DB');
		return $link;
	}

	private function close($link) {
		mysqli_close($link);
	}

	public function login($email, $pass) {
		$link = $this->open();

		$sql = "SELECT * FROM usuarios WHERE email='$email' AND contrasenia='$pass'";

		// Se ejecuta la consulta y se espera un arreglo como respuesta
		$resultArray = mysqli_query($link, $sql);

		// Los resultados se agregan a un arreglo
		$resultados = array();
		while( ($fetch = mysqli_fetch_array($resultArray, MYSQLI_ASSOC)) != NULL) {
			array_push($resultados, $fetch);
		}

		$returns = new \stdClass();
		$returns->code = 200;
		$returns->resultados = $resultados;

		$this->close($link);

		return json_encode($returns);
	}

	public function add($email, $pass, $name) {
		$link = $this->open();


		$sql = "INSERT INTO usuarios(email,contrasenia,nombre) VALUES('$email','$pass','$name')";

		// Se ejecuta la consulta y se espera un arreglo como respuesta
		$resultArray = mysqli_query($link, $sql) or die('{"code":500}');

		$returns = new \stdClass();
		$returns->code = 200;
		$returns->resultados = array();

		$this->close($link);
		return json_encode($returns);
	}

	public function addAuction($prod, $desc, $foto, $cate, $price, $date){
		//se abre
		$link = $this->open();

		//subiendo img al local server
		$sql ="SELECT id FROM subastas ORDER BY id ASC";
		$res = mysqli_query($link,$sql);	
		$id = 0;//?
		while($row = mysqli_fetch_array($res)){
		$id = $row['id'];
		}

		$nameImg = "$id.png";
		$path = "imagenes/$id.png";
		//$actualpath = "http://192.168.100.196:8080/Proyecto3WS/$path";

		if(mysqli_query($link,$sql)){
			file_put_contents($path,base64_decode($foto)or die("no se subio la imagen al servidor"));
			}
			
		//insertando dato en mysql
		$sql = "INSERT INTO subastas(producto,descripcion,foto,categoria,precio_inicial,vence) VALUES('$prod','$desc','$nameImg','$cate','$price','$date')";
		// Se ejecuta la consulta y se espera un arreglo como respuesta
		$resultArray = mysqli_query($link, $sql) or die('{"code":501}');

		$returns = new \stdClass();
		$returns->code = 200;
		$returns->resultados = array();
		
		//se cierra
		$this->close($link);
		return json_encode($returns);
	}

	public function showAll() {
		$link = $this->open();

		$sql = "SELECT * FROM subastas";

		// Se ejecuta la consulta y se espera un arreglo como respuesta
		$resultArray = mysqli_query($link, $sql);

		// Los resultados se agregan a un arreglo
		$resultados = array();
		while( ($fetch = mysqli_fetch_array($resultArray, MYSQLI_ASSOC)) != NULL) {
			array_push($resultados, $fetch);
		}

		$returns = new \stdClass();
		$returns->code = 200;
		$returns->resultados = $resultados;

		$this->close($link);

		return json_encode($returns);
	}

	public function bid($amount, $userId, $productId) {
		$link = $this->open();

		$sql = "UPDATE subastas SET precio_final=$amount, id_usuario=$userId WHERE id=$productId";

		mysqli_query($link, $sql) or die('{"code":501}');
		
		//limpia la subasta anterior 
		$sqlLimpia = "DELETE FROM compras WHERE id_subasta=$productId AND id_usuario = $userId";

		mysqli_query($link, $sqlLimpia) or die('{"code":502}');

		$hoy = date("D M j G:i:s T Y");
		$sqlCompra = "INSERT INTO compras(id_subasta,id_usuario,datos_compra) VALUES($productId,$userId,'$hoy')";

		mysqli_query($link, $sqlCompra) or die('{"code":503}');

		$returns = new \stdClass();
		$returns->code = 200;
		$returns->resultados = array();

		$this->close($link);

		return json_encode($returns);
	}
	
	public function bought($idSession){

		$link = $this->open();

		//de la tabla subastas ve que usuario tiene actualmente el articulo
		$sql = "SELECT * FROM subastas INNER JOIN usuarios ON subastas.id_usuario = usuarios.id WHERE usuarios.id = $idSession;";

		// Se ejecuta la consulta y se espera un arreglo como respuesta
		$resultArray = mysqli_query($link, $sql);

		// Los resultados se agregan a un arreglo
		$resultados = array();
		while( ($fetch = mysqli_fetch_array($resultArray, MYSQLI_ASSOC)) != NULL) {
			array_push($resultados, $fetch);
		}

		$returns = new \stdClass();
		$returns->code = 200;
		$returns->resultados = $resultados;

		$this->close($link);

		return json_encode($returns);
		
	}

	public function editUser($pass, $name, $phone, $dataExtra, $idSession){
		$link = $this->open();

		$sql = "UPDATE usuarios SET contrasenia = '$pass', nombre = '$name', telefono = '$phone', datos_extras = '$dataExtra' WHERE usuarios.id = $idSession";
		
		mysqli_query($link,$sql) or die('{"code":504}');

		$returns = new \stdClass();
		$returns->code = 200;
		$returns->resultados = array();

		$this->close($link);

		return json_encode($returns);
	}



}
