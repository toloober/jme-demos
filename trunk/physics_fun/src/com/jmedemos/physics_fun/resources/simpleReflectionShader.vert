varying vec4 viewCoords;

void main()
{
	
	// multiply with gl_TextureMatrix to get the correct texture scale
	gl_TexCoord[0] = gl_MultiTexCoord0 * gl_TextureMatrix[0];
	
	//This calculates our current projection coordinates
	viewCoords = ftransform();
	gl_Position = viewCoords;
}