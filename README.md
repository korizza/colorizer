# colorizer

A simple swing demo application that demonstrates working with native library using JNI. The application uses `ColorizerNativeTest` class derived from `javax.swing.JTextPane` and represents an extended text editor component. This component allows to interactively highlight entered symbols. `libcolorizer` - a native library written in c++, applies necessary color to every symbol depending on whether it's a digit, text or space.


## Build application
Use `gradlew` (Linux) or `gradlew.bat` (Windows) scripts to build java application:

~~~
git clone https://github.com/korizza/colorizer.git
cd colorizer
./gradlew build
~~~


## Run application
After building you may use application distributions created by gradle. Run the following:
~~~
cd  build/distributions
tar xf colorizer-demo-1.0.tar
cd colorizer-demo-1.0/bin
~~~
and (Linux):
~~~
./colorizer-demo
~~~
or (for Windows):
~~~
./colorizer-demo.bat
~~~

