/**
 * Cookie handling routines from: http://script.aculo.us/docs/Cookie.html
 */
var Cookie = {
  set: function(name, value, daysToExpire, path) {
    var expire = '';
    if (daysToExpire != undefined) {
      var d = new Date();
      d.setTime(d.getTime() + (86400000 * parseFloat(daysToExpire)));
      expire = '; expires=' + d.toGMTString();
    }
    
    var pathParam = '';
    if (path) {
      pathParam = '; path=' +  escape(path);
    }
    
    return (document.cookie = escape(name) + '=' + escape(value || '') + expire + pathParam);
  },
  get: function(name) {
    var cookie = document.cookie.match(new RegExp('(^|;)\\s*' + escape(name) + '=([^;\\s]*)'));
    return (cookie ? unescape(cookie[2]) : null);
  },
  erase: function(name, path) {
    var cookie = Cookie.get(name) || true;
    Cookie.set(name, '', -1, path);
    return cookie;
  },
  accept: function() {
    if (typeof navigator.cookieEnabled == 'boolean') {
      return navigator.cookieEnabled;
    }
    Cookie.set('_test', '1');
    return (Cookie.erase('_test') == '1');
  }
};