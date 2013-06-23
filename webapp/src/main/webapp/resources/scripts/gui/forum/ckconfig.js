CKEDITOR.config.resize_enabled = false;
CKEDITOR.config.entities = false;
CKEDITOR.config.entities_processNumerical = true;
CKEDITOR.config.ignoreEmptyParagraph = true;
//CKEDITOR.config.dialog_backgroundCoverColor = '#000000';
//CKEDITOR.config.dialog_backgroundCoverOpacity = 0.65;
//CKEDITOR.config.baseFloatZIndex = 22;
//CKEDITOR.config.format_tags = 'p;h1;h2;h3;h4;h5;h6';
//CKEDITOR.config.font_names = 'Arial/arial;Times New Roman/times new roman;Courier New/courier new;Palatino/palatino;Garamond/garamond;Bookman/bookman;Avant Garde/avant garde;Verdana/verdana;Georgia/georgia;Comic Sans MS/comic sans ms;Trebuchet MS/trebuchet ms;Arial Black/arial black;Impact/impact;Serif/serif';
CKEDITOR.config.scayt_autoStartup = false;

CKEDITOR.config.keystrokes = [ 
  [ CKEDITOR.CTRL + 90 /* Z */, 'undo' ],
  [ CKEDITOR.CTRL + 89 /* Y */, 'redo' ],
  [ CKEDITOR.CTRL + CKEDITOR.SHIFT + 90 /* Z */, 'redo' ],
  [ CKEDITOR.CTRL + 76 /* L */, 'link' ],
  [ CKEDITOR.CTRL + 66 /* B */, 'bold' ],
  [ CKEDITOR.CTRL + 73 /* I */, 'italic' ],
  [ CKEDITOR.CTRL + 85 /* U */, 'underline' ],
  [ CKEDITOR.CTRL + 48 /* 0 */, 'format_p' ],
  [ CKEDITOR.CTRL + 49 /* 1 */, 'format_h1' ],
  [ CKEDITOR.CTRL + 50 /* 2 */, 'format_h2' ],
  [ CKEDITOR.CTRL + 51 /* 3 */, 'format_h3' ],
  [ CKEDITOR.CTRL + 52 /* 4 */, 'format_h4' ],
  [ CKEDITOR.CTRL + 53 /* 5 */, 'format_h5' ],
  [ CKEDITOR.CTRL + 54 /* 6 */, 'format_h6' ] 
];

CKEDITOR.config.toolbar_ForumTopic = CKEDITOR.config.toolbar_ForumPost = 
  [
   { name: 'clipboard',   items : [ 'Cut','Copy','Paste','-','Undo','Redo' ] },
   { name: 'editing',     items : [ 'Find','Replace','-','SelectAll','-','Scayt' ] },
   { name: 'basicstyles', items : [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ] },
   { name: 'paragraph',   items : [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote' ] },
   { name: 'links',       items : [ 'Link','Unlink' ] },
   { name: 'insert',      items : [ 'Image','SpecialChar' ] },
   { name: 'tools',       items : [ 'Maximize' ] }
];