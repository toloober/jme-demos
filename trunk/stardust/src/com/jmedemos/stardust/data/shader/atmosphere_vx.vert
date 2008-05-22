
uniform vec4 fvAtmoColor;
uniform float fAtmoDensity;
uniform float fAbsPower;
uniform float fGlowPower;

uniform float fCloudHeight;

varying vec3 fvNormal;
varying vec3 fvViewDirection;
varying vec3 fvLightDirection;

vec3 maxVal(in vec3 color) {
   float m = max(color.g, color.b);
   m = max(m, color.r);
   return color.xyz / vec3(m);
}

void main(void)
{
   float atmoSizeFact = 1.0 + fCloudHeight*10.0;
   mat4 atmoSizeMat = mat4(atmoSizeFact);
   atmoSizeMat[3].w = 1.0;

   mat4 newWorldMatrix = gl_ModelViewMatrix * atmoSizeMat;
   gl_Position = gl_ProjectionMatrix * newWorldMatrix * gl_Vertex;
  
   vec3 vertexPosition = (gl_ModelViewMatrix * gl_Vertex).xyz;
  
   vec3 viewDir     = normalize(-vertexPosition);
   vec3 lightDir    = normalize(gl_LightSource[0].position.xyz - vertexPosition);
   vec3 normal      = normalize(gl_NormalMatrix * gl_Normal);
   
   //float VdotL = -dot(lightDir, viewDir);
   float NdotL = dot(normal, lightDir);
   float NdotV = dot(normal, viewDir);
   
   float abs_power = clamp(pow( -dot(viewDir, lightDir), 1.0/fAbsPower), 0.0, 1.0);

   if (abs_power == 1.0)
      abs_power = 0.0;
   
   float glow =  1.0 - pow(cos(abs(NdotV) * 1.57079), fGlowPower);  
    
   vec4 color = gl_LightSource[0].diffuse * fvAtmoColor;
   vec4 invColor = 1.0 - color;
   invColor.w = color.w;
    
   vec4 diffuseColor = mix(color, invColor, abs_power);
    
   float falloff = max(0.0,(1.0 - NdotV) *  min(1.0, NdotL+0.35) * glow );
   falloff = pow(falloff, 1.0/fAtmoDensity);
 
   gl_FrontColor = vec4( maxVal(diffuseColor.xyz * falloff), diffuseColor.w * falloff);
}