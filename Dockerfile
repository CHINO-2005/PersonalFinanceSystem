# ใช้ Tomcat 10 ที่รองรับ Java 17-21
FROM tomcat:10.1-jdk21

# ลบแอปเดิมที่ติดมากับ Tomcat ออก
RUN rm -rf /usr/local/tomcat/webapps/*

# ก๊อปปี้ไฟล์ .war ของคุณเข้าไปที่ webapps และเปลี่ยนชื่อเป็น ROOT.war
# (เพื่อให้เข้าเว็บได้โดยไม่ต้องพิมพ์ชื่อโปรเจกต์ต่อท้าย)
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

# เปิดพอร์ต 8080
EXPOSE 8080
CMD ["catalina.sh", "run"]