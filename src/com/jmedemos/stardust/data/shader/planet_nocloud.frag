/*    
 	This file is part of jME Planet Demo.

    jME Planet Demo is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation.

    jME Planet Demo is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with jME Planet Demo.  If not, see <http://www.gnu.org/licenses/>.
*/

varying vec3 vLightDirectionInTangentSpace;
varying vec3 vViewDirectionInTangentSpace;

varying vec2 vTexCoords;

uniform sampler2D baseMap;
uniform sampler2D specMap;
uniform sampler2D normalMap;

void main(void)
{
   vec3 normal           = normalize( ( texture2D( normalMap, vTexCoords ).xyz * 2.0 ) - 1.0 );
   
   vec3 lightDirection   = normalize(vLightDirectionInTangentSpace);
   vec3 viewDirection    = normalize(vViewDirectionInTangentSpace);
   
   vec4 diffuseColor = texture2D(baseMap, vTexCoords); 
   
   float NdotL = dot(normal, lightDirection);
   
   vec3 R = reflect(-lightDirection, normal);
   float NdotR = max(0.0, dot(viewDirection, R));
   
   gl_FragColor = diffuseColor * NdotL * gl_LightSource[0].diffuse + 
		  pow(NdotR, gl_FrontMaterial.shininess) 
			* texture2D(specMap, vTexCoords) * gl_LightSource[0].specular;
}