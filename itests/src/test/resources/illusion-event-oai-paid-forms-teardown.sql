delete from IllusionEventRegistrationFormFieldAnswer where field_id in (select id from IllusionEventRegistrationFormField where form_id in (2, 3, 4));
delete from IllusionEventRegistrationFormField where form_id in (2, 3, 4);
delete from IllusionEventRegistrationForm where id in (2, 3, 4);