
-  docker exec -it smartcare-db mysql -uroot -p'@Zz10010427' -e "
   CREATE USER IF NOT EXISTS 'smartcare_admin'@'%' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON smartcare_db.* TO 'smartcare_admin'@'%';
   FLUSH PRIVILEGES;
   "