window.hu_galambo_gobelin_three_ThreeComponent = function() {
	
	var tree = new mytreelibrary.TreeComponent(this.getElement());
	
	this.onStateChange = function() {
		tree.setRotX(this.getState().rotX);
	};
};