package com.minimal

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes.Usage

class TriangleExplosion(val camera: Camera) {
    val maxParticles = 1000
    val maxVertices = maxParticles * 3
    val maxIndices = maxParticles * 3
    val particleLifeTimeSec = 2.0f
    val particleLifeTime = "2.0"        // to be included in the shader code

    val mesh = Mesh(false, maxVertices, maxIndices,
            VertexAttribute(Usage.Position, 2, "a_pos"),
            VertexAttribute(Usage.Position, 2, "a_vel"),
            VertexAttribute(Usage.Position, 1, "a_ang_vel"),
            VertexAttribute(Usage.Generic, 1, "a_startTime"),
            VertexAttribute(Usage.Generic, 1, "a_angle"))

    val vertexShader = """
        attribute vec2 a_pos;
        attribute vec2 a_vel;
        attribute float a_ang_vel;
        attribute float a_startTime;
        attribute float a_angle;

        uniform mat4 u_projTrans;
        uniform float u_time;

        varying vec4 v_color;

        void main() {
            float alpha = 1.0 - (u_time - a_startTime) / $particleLifeTime;
            v_color = vec4(1.0, 1.0, 1.0, alpha);
            vec2 center_pos = vec4(a_pos + a_vel * (u_time - a_startTime), 0.0, 1.0);
            float angle_t = a_ang_vel * (u_time - a_startTime)
            vec2 vertex_pos = pos + rotate(size * vec(1,0), a_angle + angle_t)
			gl_Position = u_projTrans * pos
        }
        """

    val fragmentShader = """
        #ifdef GL_ES
            #define LOWP lowp
            precision mediump float;
        #else
            #define LOWP
        #endif
        varying LOWP vec4 v_color;

        void main() {
            gl_FragColor = v_color;
        }
        """


}