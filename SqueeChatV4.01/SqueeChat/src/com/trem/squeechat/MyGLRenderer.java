/*
 * Copyright (C) 2011 The Android Open Source Project
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
package com.trem.squeechat;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing life cycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private MeterMarker mMeterMarker;
    private TouchMarker mTouchMarker;
    private Meter mMeter;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mTranslationMatrix = new float[16];
    
    private float meterColor[] = new float[4];
    private float yTouchPosition;
    private float yMeterPosition;
    
    private void calculateMeterColor() {
    	// calculate color based on current yMeterPosition
        if(yMeterPosition >= 1){
        	meterColor[0] = 1f;
        	meterColor[1] = 0f;
        	meterColor[2] = 0f;
        	meterColor[3] = 0f;
        }
        else if(yMeterPosition >= 0 && yMeterPosition < 1) {
        	meterColor[0] = 1f;
        	meterColor[1] = 1 - yMeterPosition;
        	meterColor[2] = 0f;
        	meterColor[3] = 0f;
        }
        else if(yMeterPosition >= -1 && yMeterPosition < 0) {
        	meterColor[0] = 1f + yMeterPosition;;
        	meterColor[1] = 1f;
        	meterColor[2] = 0f;
        	meterColor[3] = 0f;
        }
        else if(yMeterPosition < -1) {
        	meterColor[0] = 0f;
        	meterColor[1] = 1f;
        	meterColor[2] = 0f;
        	meterColor[3] = 0f;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.25f, 0.25f, 0.25f, 1.0f);

        mMeterMarker = new MeterMarker();
        mTouchMarker = new TouchMarker();
        mMeter = new Meter();
        yTouchPosition = -1f;
        yMeterPosition = -1f;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        
        // change meter position
        if(yMeterPosition < (yTouchPosition - .025)){
	        yMeterPosition += .05;
        }
        else if(yMeterPosition > (yTouchPosition + .025)){
 	        yMeterPosition -= .05;
        }
        calculateMeterColor();
        
        Matrix.translateM(mTranslationMatrix, 0, mMVPMatrix, 0, 0f, yMeterPosition + 1f, 0f);
        
        mMeter.draw(mTranslationMatrix, meterColor);
        
        // Draw a meter marker on top of the screen (for hard)
        mMeterMarker.draw(mMVPMatrix);
        
        // Create a translation matrix for the middle meter marker
        Matrix.translateM(mTranslationMatrix, 0, mMVPMatrix, 0, 0f, -0.66f, 0f);
        
        // draw the middle meter marker
        mMeterMarker.draw(mTranslationMatrix);
        
        // create a translation matrix for the lowest meter marker 
        Matrix.translateM(mTranslationMatrix, 0, mMVPMatrix, 0, 0f, -1.32f, 0f);
        
        // draw the lowest meter marker
        mMeterMarker.draw(mTranslationMatrix);
        
        // translate and draw the touch marker
        Matrix.translateM(mTranslationMatrix, 0 , mMVPMatrix, 0, 0f, yTouchPosition + 1f, 0f);
        
        mTouchMarker.draw(mTranslationMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setY(float y) {
        yTouchPosition = y;
    }

}