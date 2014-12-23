Highcharts Server Export Servlet 2.5
====================================

The primary goal of the Highcharts Server Export Servlet 2.5 is to make Highcharts Server Export working on tomcat6 / servlet 2.5.

It is based on the source code of Highcharts Server Export (working on servlet 3.0).

Thanks to Highcharts we are able to redistribute this code.

This code works only with Highcharts 2.3.3 (and possibly later), people are welcome to contribe to make it work with previous versions.

Prerequisites
-------------

* First, download Maven (http://maven.apache.org/download.html) and follow the installation instructions (http://maven.apache.org/download.html#Installation). 

* Download com.oreilly.servlet: [Download cos cos-26Dec2008.zip](http://www.servlets.com/cos/)


Installation
------------

* Import cos jar into maven:

	mvn install:install-file -DgroupId=servlets.com -DartifactId=cos -Dversion=26Dec2008 -Dpackaging=jar -Dfile=/path/to/cos.jar

* After that, type the following in a terminal or in a command prompt: mvn clean package

* Now you have a highcharts-export.war file which you can upload/copy to your application server.

* Change you url configuration in the exporting option, that it is pointing to your new installed exporting-server.

	exporting:{
		url:'http://new.server.com/highcharts-export'
	}


Known Issues
------------

If you use OpenJDK instead of Oracle/Sun JDK, you could have some issues with JPEGImageEncoder. There is 2 solutions to solve it:

* install Sun's JDK

* or edit the batik-codec.jar file. In the META-INF/services folder find the two configuration files. Edit these files that they use the org.apache.batik.ext.awt.image.codec.imageio classe from now on


Copyright and License
---------------------
See http://shop.highsoft.com/highcharts.html for detail.

Attribution-NonCommercial 3.0 Unported (CC BY-NC 3.0)

http://creativecommons.org/licenses/by-nc/3.0/


Special thanks
--------------

Thanks to the Highchart support for their help ;).
