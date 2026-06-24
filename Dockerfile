# ใช้ Tomcat ที่รันบน Java 21
FROM tomcat:10.1-jdk21-temurin

# ลบแอปเดิมทิ้ง
RUN rm -rf /usr/local/tomcat/webapps/*

# นำไฟล์ .war จากโฟลเดอร์ target ไปวางเป็น ROOT.war
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]