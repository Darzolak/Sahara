export JAVA_HOME="/home/cosc/student/jml168/Documents/jdk1.8.0_51";
javapackager -deploy -native image -outdir packages -outfile Sahara -srcdir target -srcfiles project-2-1.0-SNAPSHOT-launcher.jar -appclass seng302.group2.App -name "Sahara" -title "Sahara"
icon=resources/icon.ico