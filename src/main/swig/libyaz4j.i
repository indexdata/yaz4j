%javaconst(1);
%module yaz4jlib
	%{
		#include "zoom-extra.h"
		#include <yaz/zoom.h>
	%}
	%include "typemaps.i"
	%include "arrays_java.i"
	%include "cpointer.i"
	%pointer_functions(int, intp);
	%pointer_functions(size_t, size_tp);
	%include "carrays.i"
	%array_functions(ZOOM_record, zoomRecordArray);
	%typemap(jni) CharStarByteArray "jbyteArray"
	%typemap(jtype) CharStarByteArray "byte[]"
	%typemap(jstype) CharStarByteArray "byte[]"
	%typemap(out) CharStarByteArray {
		$result = SWIG_JavaArrayOutSchar(jenv, (signed char*) $1.data, $1.length);
	}
	%typemap(javaout) CharStarByteArray { return $jnicall; }
	%define YAZ_BEGIN_CDECL %enddef
	%define YAZ_END_CDECL %enddef
	%include "zoom-extra.h"
	%include <yaz/zoom.h>

