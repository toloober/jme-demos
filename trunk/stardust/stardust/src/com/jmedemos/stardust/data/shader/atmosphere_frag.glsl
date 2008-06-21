// constants used in this fragment shader
const vec4 fvAtmoColor = vec4(0.4, 0.4, 1.0, 0.6);

varying vec4 vertexPos;

void main(void)
{
	// set the Fragment Color
    // mix in the Fog Color depending on the distance
    float tmp = 1 - (gl_Fog.end - vertexPos.z) * gl_Fog.scale;
    if (tmp < 0.0) {
    	tmp = 0.0;
    }
    
    gl_FragColor = mix(fvAtmoColor, gl_Fog.color, tmp);
}