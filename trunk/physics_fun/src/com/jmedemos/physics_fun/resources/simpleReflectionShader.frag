varying vec4 viewCoords;
uniform sampler2D reflection;
uniform sampler2D tex;
void main()
{
	vec4 projCoord = viewCoords / viewCoords.q;
	projCoord = (projCoord + 1.0) * 0.5;
	projCoord.x = 1.0 - projCoord.x;
	vec4 reflectionColor = 0.5 * (texture2D(reflection, projCoord.xy) + texture2D(tex,gl_TexCoord[0].st));
	gl_FragColor = reflectionColor;
}