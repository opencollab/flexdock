#include <jni.h>
#include "org_flexdock_docking_drag_outline_win32_Win32RubberBand.h"
#include <windows.h>

JNIEXPORT void JNICALL Java_org_flexdock_docking_drag_outline_win32_Win32RubberBand_drawRectangle
		(JNIEnv *env, jobject obj, jint x, jint y, jint width, jint height) {
	drawRectImpl(x, y, width, height,  R2_COPYPEN);
	return;
}

JNIEXPORT void JNICALL Java_org_flexdock_docking_drag_outline_win32_Win32RubberBand_clearRectangle
		(JNIEnv *env, jobject obj, jint x, jint y, jint width, jint height) {
	drawRectImpl(x, y, width, height,  R2_NOTCOPYPEN);
	return;
}

int drawRectImpl(int x, int y, int width, int height, int penType) {
    HDC hdc;
    RECT myRect;
    
    if(width<0 || height<0)
    	return -1;
    
    int x2 = x + width;
    int y2 = y + height;

	hdc = GetWindowDC(GetDesktopWindow());
	SetRect(&myRect, x, y, x2, y2);
	SetROP2(hdc, penType);
	DrawFocusRect(hdc, &myRect);
	ReleaseDC(NULL, hdc);
	return 0;
}

JNIEXPORT void JNICALL Java_org_flexdock_docking_drag_outline_win32_Win32RubberBand_cleanup
		(JNIEnv *env, jobject obj) {
	cleanupImpl();
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
	cleanupImpl();
}

int cleanupImpl() {
	return 0;
}

