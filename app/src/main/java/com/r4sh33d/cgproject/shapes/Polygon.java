package com.r4sh33d.cgproject.shapes;

import android.opengl.GLES20;

import com.r4sh33d.cgproject.ElementType;
import com.r4sh33d.cgproject.PolygonConfig;
import com.r4sh33d.cgproject.gl_engine.BasicPrimitiveGLRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Polygon {

    //PolygonConfig polygonConfig;
    private ElementType elementType;
    private int mPositionHandle;
    private int mColorHandle;
    float[] color;
    float[] polygonCoords;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    private int vertexCount;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex


    private FloatBuffer vertexBuffer;
    private int mProgram;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "gl_PointSize = 15.0;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";


    public Polygon(PolygonConfig polygonConfig, ElementType elementType) {
        this.elementType = elementType;
        this.polygonCoords = polygonConfig.polygonCoord;
        this.color = polygonConfig.color;
        vertexCount = polygonCoords.length / COORDS_PER_VERTEX;
        init();
    }

    public void init() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                polygonCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(polygonCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        //----------

        int vertexShader = BasicPrimitiveGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = BasicPrimitiveGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    public void draw() {
        GLES20.glUseProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        GLES20.glDrawArrays(elementType.openGLDrawMode, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}
