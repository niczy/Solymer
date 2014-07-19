(function() {
  window.ElementsManager = {};
  window.Polymer = function(elementName, elementConfig) {
      console.log(elementName + " created");
      ElementsManager[elementName] = elementConfig;
  };

  var bootstrap = function() {
    console.log('btstraping customize elements');
    for (var element in ElementsManager) {
      var elementConfig = ElementsManager[element];
      var elementInstance = JSON.parse(JSON.stringify(elementConfig));
      console.log(elementInstance);
    }
  };

  window.onload = function() {
    bootstrap();
};
})();
