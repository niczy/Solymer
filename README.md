Solymer
=======

### Server side polymer ###

The basic idea is to implement server side rendering of a simple version polymer.

The syntax is simlar to polymer. You just need to call ```Polymer('elementName', config) ``` 
to declare a custom element. All the attributes are parsed and rendered on server side.
Events binding is done by ```EventCenter``` When rendering custom elements, server will give each element a uid. EventCenter will dispatch the events according the this uid.

- [x] Render template
- [x] Events binding
- [X] Parse nested custom elements.
- [ ] Allow insert content/children into custom elements.
- [ ] Support forloop and expressions in template
- [ ] Support import.
- [ ] Resolve elements dependencies
