(function() {
  // A map contains all the customize elements on this page.
  window.ElementsManager = {};
  // Events distributer.
  window.EventCenter = {};

  // Global function to register customize elements.
  window.Polymer = function(elementName, elementConfig) {
      console.log(elementName + " created");
      ElementsManager[elementName] = elementConfig;
  };

  EventCenter.publishEvent = function(elementId, functionName) {
    document.getElementById(elementId)[functionName](window.event);
  };

  // Deep clone a Object.
  var cloneObject = function(obj) {
    var newObj = {};
    for (var key in obj) {
      if (typeof obj[key] == "object") {
        newObj[key] = cloneObject(obj[key]);
      } else {
        newObj[key] = obj[key];
      }
    }
    return newObj;
  }

  // Create a new customize element instance.
  var newInstance = function(elementConfig) {
    return cloneObject(elementConfig);
  }

  // Bootstrap function.
  var bootstrap = function() {
    console.log('btstraping customize elements');
    for (var element in ElementsManager) {
      var elementConfig = ElementsManager[element];
      var elementInstance = newInstance(elementConfig);
      var instances = document.querySelectorAll('[kind=' + element+ ']');
      for (var i = 0; i < instances.length; i++) {
          var instance = instances[i];
          for (var property in elementInstance) {
            instance[property] = elementInstance[property]; 
          }
          instance.onload && instance.onload();
      }
    }
  };

  window.onload = function() {
    bootstrap();
};
})();
