update 
  Material
set 
  publicity = 'PUBLIC'
where 
  id <= 23 and
  type in ('DOCUMENT', 'IMAGE', 'PDF', 'FILE', 'BINARY', 'VECTOR_IMAGE', 'GOOGLE_DOCUMENT', 'DROPBOX_FILE' );