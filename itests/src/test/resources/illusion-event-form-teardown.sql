delete from IllusionEventRegistrationFormFieldAnswer where field_id in (select id from IllusionEventRegistrationFormField where form_id = {id|2});
delete from IllusionEventRegistrationFormField where form_id = {id|2};
delete from IllusionEventRegistrationForm where id = {id|2};