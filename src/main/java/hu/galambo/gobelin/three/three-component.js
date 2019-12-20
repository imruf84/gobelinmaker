var mytreelibrary = mytreelibrary || {};

mytreelibrary.TreeComponent = function(element) {

	var camera, scene, renderer;
	var geometry, material, mesh;
	var rotation = [0, 0, 0];

	init();
	animate();

	function init() {

		camera = new THREE.PerspectiveCamera(70, 1, 0.01, 10);
		camera.position.z = 1;

		scene = new THREE.Scene();

		geometry = new THREE.BoxGeometry(0.2, 0.2, 0.2);
		material = new THREE.MeshNormalMaterial();

		mesh = new THREE.Mesh(geometry, material);
		scene.add(mesh);

		renderer = new THREE.WebGLRenderer({
			antialias : true
		});
		renderer.setClearColor("#000000");
		var rect = element.getBoundingClientRect();
		renderer.setSize(rect.width, rect.height);
		element.appendChild(renderer.domElement);

		window.addEventListener('resize', onWindowResize, false);
		function onWindowResize() {
			var rect = element.getBoundingClientRect();
			camera.aspect = rect.width / rect.height;
			camera.updateProjectionMatrix();
			renderer.setSize(rect.width, rect.height);
		}

		onWindowResize();
	}

	function animate() {
		requestAnimationFrame(animate);
		
		var rotationSpeed = .1;
		var drx = Math.min(rotationSpeed, Math.max(-rotationSpeed, rotation[0]-mesh.rotation.z));
		mesh.rotation.z += drx;
		renderer.render(scene, camera);
	}
	
	function degToRad(deg) {
		return deg * (Math.PI/180);
	}
	
	this.getRotX = function () {
		return rotation[0];
	};
	this.setRotX = function (x) {
		rotation[0] = degToRad(x);
	};
};
