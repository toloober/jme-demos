// input variable for this vertex shader

// variables to pass to the fragment shader
varying vec4 vertexPos;

void main(void)
{   
    float atmoSizeFact = 1.03;
    mat4 atmoSizeMat = mat4(atmoSizeFact);
    atmoSizeMat[3].w = 1.0;
    
    mat4 newWorldMatrix = gl_ModelViewMatrix*atmoSizeMat;
    gl_Position = gl_ProjectionMatrix * newWorldMatrix * gl_Vertex;
    vertexPos = gl_Position;
}