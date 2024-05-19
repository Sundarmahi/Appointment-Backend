From openjdk:17
copy target/Petzey_Appointment_Service-0.0.1-SNAPSHOT.jar Petzey_Appointment_Service-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","Petzey_Appointment_Service-0.0.1-SNAPSHOT.jar"]
EXPOSE 8099