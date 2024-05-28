INSERT INTO public.role_tb (id, name) VALUES (1, 'ADMIN');
INSERT INTO public.role_tb (id, name) VALUES (2, 'USER');
INSERT INTO public.role_tb (id, name) VALUES (3, 'MANAGER');

truncate table user_role;
truncate user_tb cascade;

select * from user_tb