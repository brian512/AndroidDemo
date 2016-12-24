package com.brian.testandroid.common.camera.gles;
/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GLUtil {
    private static final String TAG = "GLUtil";
    /** Identity matrix for general use.  Don't modify or life will get weird. */

    public static final int NO_TEXTURE = -1;

    private static final int SIZEOF_FLOAT = 4;

    private GLUtil() { // do not instantiate
    }

    public static int createProgram(Context applicationContext, @RawRes int vertexSourceRawId,
                                    @RawRes int fragmentSourceRawId) {

        String vertexSource = readTextFromRawResource(applicationContext, vertexSourceRawId);
        String fragmentSource = readTextFromRawResource(applicationContext, fragmentSourceRawId);

        return createProgram(vertexSource, fragmentSource);
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        int program = GLES20.glCreateProgram();
        checkGlError("glCreateProgram");
        if (program == 0) {
            Log.e(TAG, "Could not create program");
        }
        GLES20.glAttachShader(program, vertexShader);
        checkGlError("glAttachShader");
        GLES20.glAttachShader(program, pixelShader);
        checkGlError("glAttachShader");
        GLES20.glLinkProgram(program);
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        checkGlError("glCreateShader type=" + shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, " " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    /**
     * @param textureTarget Texture类型。
     * image1. 相机用 GLES11Ext.GL_TEXTURE_EXTERNAL_OES
     * 2. 图片用GLES20.GL_TEXTURE_2D
     * @param minFilter 缩小过滤类型 (image1.GL_NEAREST ; 2.GL_LINEAR)
     * @param magFilter 放大过滤类型
     * @param wrapS X方向边缘环绕
     * @param wrapT Y方向边缘环绕
     * @return 返回创建的 Texture ID
     */
    public static int createTexture(int textureTarget, @Nullable Bitmap bitmap, int minFilter,
                                    int magFilter, int wrapS, int wrapT) {
        int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        GLUtil.checkGlError("glGenTextures");
        GLES20.glBindTexture(textureTarget, textureHandle[0]);
        GLUtil.checkGlError("glBindTexture " + textureHandle[0]);
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MIN_FILTER, minFilter);
        GLES20.glTexParameterf(textureTarget, GLES20.GL_TEXTURE_MAG_FILTER, magFilter); //线性插值
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_S, wrapS);
        GLES20.glTexParameteri(textureTarget, GLES20.GL_TEXTURE_WRAP_T, wrapT);

        if (bitmap != null) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }

        GLUtil.checkGlError("glTexParameter");
        return textureHandle[0];
    }

    public static int createTexture(int textureTarget) {
        return createTexture(textureTarget, null, GLES20.GL_LINEAR, GLES20.GL_LINEAR,
                GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
    }

    public static int createTexture(int textureTarget, Bitmap bitmap) {
        return createTexture(textureTarget, bitmap, GLES20.GL_LINEAR, GLES20.GL_LINEAR,
                GLES20.GL_CLAMP_TO_EDGE, GLES20.GL_CLAMP_TO_EDGE);
    }

    /**
     * Checks to see if a GLES error has been raised.
     */
    public static void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String msg = op + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * Checks to see if the location we obtained is valid.  GLES returns -image1 if a label
     * could not be found, but does not set the GL error.
     * <p>
     * Throws a RuntimeException if the location is invalid.
     */
    public static void checkLocation(int location, String label) {
        if (location < 0) {
            throw new RuntimeException("Unable to locate '" + label + "' in program");
        }
    }

    public static String readTextFromRawResource(final Context applicationContext,
                                                 @RawRes final int resourceId) {
        final InputStream inputStream =
                applicationContext.getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String nextLine;
        final StringBuilder body = new StringBuilder();
        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }

    public static int createTextureWithTextContent(String text) {
        // Create an empty, mutable bitmap
        Bitmap bitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);
        // get a canvas to paint over the bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(0, 0, 255, 0);
        // get a background image from resources
        // note the image format must match the bitmap format
        //        Drawable background = context.getResources().getDrawable(R.drawable.background);
        //        background.setBounds(0, 0, 256, 256);
        //        background.draw(canvas); // draw the background to our bitmap
        // Draw the text
        Paint textPaint = new Paint();
        textPaint.setTextSize(32);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(0xff, 0xff, 0xff, 0xff);
        // draw the text centered
        canvas.drawText(text, 16, 112, textPaint);

        int[] textures = new int[1];

        //Generate one texture pointer...
        GLES20.glGenTextures(1, textures, 0);

        //...and bind it to our array
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        //Create Nearest Filtered Texture
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        //Different possible texture parameters, e.g. GLES20.GL_CLAMP_TO_EDGE
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        //Alpha blending
        //GLES20.glEnable(GLES20.GL_BLEND);
        //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        //Use the Android GLUtils to specify a two-dimensional texture image from our bitmap
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        //Clean up
        bitmap.recycle();

        return textures[0];
    }

    public static final float[] IDENTITY_MATRIX;
    static {
        IDENTITY_MATRIX = new float[16];
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }
    public static final int BYTES_PER_FLOAT = 4;
    //    public static final int BYTES_PER_INT = 4;
    public static final short BYTES_PER_SHORT = 2;

    public static void checkGLError(String msg) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String str = msg + ": glError 0x" + Integer.toHexString(error);
            Log.e(TAG, str);
            int values[] = new int[2];
            GLES20.glGetIntegerv(GLES20.GL_ARRAY_BUFFER_BINDING, values, 0);
            GLES20.glGetIntegerv(GLES20.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING, values, 1);
            Log.e(TAG, "Current bound array buffer: " + values[0]);
            Log.e(TAG, "Current bound vertex attrib: "+ values[1]);
//            throw new RuntimeException(msg);
        }
    }

    public static FloatBuffer createFloatBuffer(int size) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(size * BYTES_PER_FLOAT);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asFloatBuffer();
    }
    /*public static IntBuffer createIntBuffer(int size) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(size * BYTES_PER_INT);
        buffer.order(ByteOrder.nativeOrder());
        return buffer.asIntBuffer();
    }*/
    public static FloatBuffer createFloatBuffer(float[] coords) {
        FloatBuffer fb = createFloatBuffer(coords.length);
        fb.put(coords);
        fb.position(0);
        return fb;
    }
    /*public static IntBuffer createIntBuffer(int[] data) {
        IntBuffer ib = createIntBuffer(data.length);
        ib.put(data);
        ib.position(0);
        return ib;
    }*/
    public static ShortBuffer createShortBuffer(short[] data) {
        ShortBuffer sb = ByteBuffer.allocateDirect(data.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        sb.put(data).position(0);
        return sb;
    }

    public static int createProgram(String vertSrc, String fragSrc,
                                    String[] attributeNames, int[] attributeBinding,
                                    String[] uniformNames, int[] uniformBinding) {

        int program = GLES20.glCreateProgram();

        int status = 1;
        int[] vertSh = new int[1];
        int[] fragSh = new int[1];
        status *= compileShader(GLES20.GL_VERTEX_SHADER, vertSrc, vertSh);
        status *= compileShader(GLES20.GL_FRAGMENT_SHADER, fragSrc, fragSh);
        checkGLError("Compiling shaders");

        GLES20.glAttachShader(program, vertSh[0]);
        checkGLError("Attach shader");
        GLES20.glAttachShader(program, fragSh[0]);
        checkGLError("Attach shader fragment");

        //Bind attributes
        for(int i=0; i<attributeNames.length; i++){
            GLES20.glBindAttribLocation(program, attributeBinding[i], attributeNames[i]);
            checkGLError("Bind attribute: " + attributeNames[i]);
        }
        status *= linkProgram(program);

        status *= validateProgram(program);

        //location of uniforms

        if (status > 0) {
            for (int i=0; i< uniformNames.length; i++) {
                //			if (uniformsLocations.at(i).first.length()) {
                int loc = GLES20.glGetUniformLocation(program,
                        uniformNames[i]);
                checkGLError("glGetUniformLocation - " + uniformNames[i]);
                if (loc < 0) Log.e(TAG, "Bad uniform " + uniformNames[i]);
                uniformBinding[i] = loc;
            }
        } else {
            GLES20.glDeleteProgram(program);
            program = 0;
        }

        if (vertSh[0] > 0) {
            GLES20.glDeleteShader(vertSh[0]);
            GLES20.glDetachShader(program, vertSh[0]);
        }
        if (fragSh[0] > 0) {
            GLES20.glDeleteShader(fragSh[0]);
            GLES20.glDetachShader(program, fragSh[0]);
        }
        checkGLError("Shaders deleted");
        return program;
    }

    private static int compileShader(int target, String source, int[] output) {
        output[0] = GLES20.glCreateShader(target);

        //	const GLchar *str = src.c_str();
        GLES20.glShaderSource(output[0], source);
        GLES20.glCompileShader(output[0]);
        try{
            checkGLError("Compile shader");
        }catch (Exception e){

        }

        int[] status = new int[1];
        GLES20.glGetShaderiv(output[0], GLES20.GL_COMPILE_STATUS, status, 0);
        if(status[0] == 0){
            Log.e(TAG, "Failed to compile shader: " + GLES20.glGetShaderInfoLog(output[0]));
            GLES20.glDeleteShader(output[0]);
        }
        return status[0];
    }

    private static int linkProgram(int program) {
        int[] status = new int[1];
        GLES20.glLinkProgram(program);
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Error linking program: " + GLES20.glGetProgramInfoLog(program));
            return 0;
        }
        return 1;
    }

    private static int validateProgram(int program) {
        int[] status = new int[1];
        GLES20.glValidateProgram(program);

        GLES20.glGetProgramiv(program, GLES20.GL_VALIDATE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "Error validating program: " + GLES20.glGetProgramInfoLog(program));
            return 0;
        }
        return 1;
    }
}