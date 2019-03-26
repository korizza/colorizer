all : colorizer_native.so

colorizer_native.so : colorizer_native.obj
	g++ -o bin/libcolorizer.so -shared bin/libcolorizer.o
	cp bin/libcolorizer.so ../resources/native/libcolorizer.so

colorizer_native.obj: colorizer_native.cpp jni_headers
	g++ -O3 -o bin/libcolorizer.o -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux" -fpic -c colorizer_native.cpp

jni_headers: java_class_files
	javah -jni -o colorizer_native.h -classpath bin org.korizza.colorizer.io.ColorizerNative

java_class_files: ../java/org/korizza/colorizer/io/ColorizerNative.java
	mkdir -p bin
	javac -d bin -cp bin  ../java/org/korizza/colorizer/io/ColorizerNative.java

clean:
	rm -rf bin
	rm -rf ../resources/native/*.so


