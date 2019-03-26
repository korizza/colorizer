#include "colorizer_native.h"
#include "highlight.h"

#include <iostream>
#include <vector>
#include <string>
#include <list>
#include <iterator>

JNIEXPORT jboolean JNICALL Java_org_korizza_colorizer_io_ColorizerNative_test
  (JNIEnv *, jobject)
{
    return 1;
}


JNIEXPORT jintArray JNICALL Java_org_korizza_colorizer_io_ColorizerNative_colorize
		(JNIEnv *env, jobject obj, jstring text)
{

	if (!text) {
		std::cerr << "The text parameter should not be null\n";
		return env->NewIntArray(0);
	}

	// get size of string
	jsize text_size = env->GetStringUTFLength(text);

	// make vector of chars from the java string
	const char *ptext= env->GetStringUTFChars(text, nullptr);
	if (ptext == nullptr) {
		return env->NewIntArray(0);
	}
	std::vector<char> vtext;
	vtext.assign(ptext, ptext + text_size);

    // define calculated color list
	std::list<Color> calc_colors;

	// define callback
	auto on_canceled = [&calc_colors, env, obj]() {
		jclass cls = env->GetObjectClass(obj);
		jmethodID m_id = env->GetMethodID(cls, "isCanceled", "(I)Z");
		if (m_id == 0) {
			std::cerr << "Cannot get a native method id\n";
			return false;
		}

		return static_cast<bool>(env->CallBooleanMethod(obj, m_id, static_cast<jint>(calc_colors.size())) == JNI_TRUE);
	};

	// call colorize function
	highlight(vtext, on_canceled, std::back_insert_iterator<std::list<Color>>(calc_colors));

	// get result array
	jintArray colors = env->NewIntArray(calc_colors.size());
	jint *pcolors = env->GetIntArrayElements(colors, nullptr);
	if (pcolors == nullptr) {	
		env->ReleaseStringUTFChars(text, ptext); 
		return env->NewIntArray(0);
	}

	int idx = 0;
	for (auto& c : calc_colors) {
		pcolors[idx++] = 0xff000000 | 
						((static_cast<int>(c.r) << 0x10) & 0x00ff0000) | 
						((static_cast<int>(c.g) << 0x8) & 0x0000ff00) | 
						(static_cast<int>(c.b) & 0x000000ff);
	}  

	// clean up jni data
	env->ReleaseIntArrayElements(colors, pcolors, 0);
	env->ReleaseStringUTFChars(text, ptext); 

	return colors;
}
