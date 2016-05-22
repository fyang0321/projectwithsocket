JFLAGS = -g -d . -classpath .
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        HttpRequestType.java \
		MimeType.java \
        HttpRequestManager.java \
        HttpResponseManager.java \
        Utils.java \
        HTTPServerThread.java \
        HTTPServer.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
