#include <jni.h>
#include "org_flexdock_docking_drag_outline_xlib_XlibRubberBand.h"
#include <X11/Xlib.h>
#include <stdio.h>

struct xScreen {
    Display* display;
    Window window;
    int index;
    GC gc;
    int err;
} screen;

JNIEXPORT void JNICALL Java_org_flexdock_docking_drag_outline_xlib_XlibRubberBand_drawRectangle
        (JNIEnv *env, jobject obj, jint x, jint y, jint width, jint height) {
    drawRectImpl(x, y, width, height);
}

JNIEXPORT void JNICALL Java_org_flexdock_docking_drag_outline_xlib_XlibRubberBand_clearRectangle
        (JNIEnv *env, jobject obj, jint x, jint y, jint width, jint height) {
    drawRectImpl(x, y, width, height);
}


int drawRectImpl(int x, int y, int width, int height) {
    if(screen.err)
        return -1;

    if(!screen.display) {
        initialize();
        if(screen.err) {
            (void) fprintf(stderr, "cannot connect to X server %s\n", XDisplayName(NULL));
            return -1;
        }
    }

    if(width<0 || height<0)
        return -1;

    XSetForeground(screen.display, screen.gc, WhitePixel(screen.display, screen.index));
    XSetSubwindowMode(screen.display, screen.gc, IncludeInferiors);
    XSetFunction(screen.display, screen.gc, GXxor);
    XDrawRectangle(screen.display, screen.window, screen.gc, x, y, width, height);
    XFlush(screen.display);
    return 0;
}

int initialize() {
    if(screen.display)
        return -1;

    screen.display = screen.display=XOpenDisplay("");
    if(!screen.display) {
        screen.err = 1;
        return -1;
    }

    screen.index = XDefaultScreen(screen.display);
    screen.window = XDefaultRootWindow(screen.display);
    screen.gc = XCreateGC(screen.display, screen.window, 0, NULL);
    screen.err = 0;
    return 0;
}




JNIEXPORT void JNICALL Java_org_flexdock_docking_drag_outline_xlib_XlibRubberBand_cleanup
        (JNIEnv *env, jobject obj) {
    cleanupImpl();
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
	cleanupImpl();
}

int cleanupImpl() {
    if(screen.display)
        XCloseDisplay(screen.display);
    return 0;
}


